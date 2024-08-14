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

    Wallet create() {
        return new Wallet(repository.save(new WalletEntity(null, ZERO)));
    }

    Optional<Wallet> getById(final UUID id) {
        return repository.findById(id).map(Wallet::new);
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
                throw new NoWalletToppedUpException(id);
            }
        } catch (final Exception walletException) {
            stripeService.refund(payment.getId());
            throw walletException;
        }
    }

}
