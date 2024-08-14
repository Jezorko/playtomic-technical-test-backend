package com.playtomic.tests.wallet.wallets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class WalletService {

    private final WalletRepository repository;

    WalletEntity create() {
        return repository.save(new WalletEntity(null, 0L));
    }

    Optional<WalletEntity> getById(final UUID id) {
        return repository.findById(id);
    }

}
