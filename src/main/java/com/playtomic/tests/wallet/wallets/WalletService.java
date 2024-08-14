package com.playtomic.tests.wallet.wallets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;

@Service
@RequiredArgsConstructor
class WalletService {

    private final WalletRepository repository;

    WalletEntity create() {
        return repository.save(new WalletEntity(null, ZERO));
    }

    Optional<WalletEntity> getById(final UUID id) {
        return repository.findById(id);
    }

    long topUp(final UUID id, String creditCardNumber, BigDecimal amount) {
        throw new IllegalStateException("not implemented yet");
    }

}
