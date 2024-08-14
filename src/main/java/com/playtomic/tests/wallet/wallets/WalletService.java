package com.playtomic.tests.wallet.wallets;

import com.playtomic.tests.wallet.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
class WalletService {

    private final WalletRepository repository;
    private final StripeService stripeService;

    WalletEntity create() {
        return repository.save(new WalletEntity(null, ZERO));
    }

    Optional<WalletEntity> getById(final UUID id) {
        return repository.findById(id);
    }

    @Transactional
    void topUp(final UUID id, String creditCardNumber, BigDecimal amount) {
        final var payment = stripeService.charge(
                creditCardNumber, // TODO: this should be char[]
                amount
        );

        try {
            final var walletsToppedUp = repository.topUp(id, amount);
            if (walletsToppedUp != 1) {
                throw new WalletCouldNotBeToppedUpException(id);
            }
        } catch (final Exception walletException) {
            stripeService.refund(payment.getId());
            throw walletException;
        }
    }

}
