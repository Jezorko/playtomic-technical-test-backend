package com.playtomic.tests.wallet.wallets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class WalletService {

    private final WalletRepository repository;

    WalletEntity create() {
        return repository.save(new WalletEntity(null, 0L));
    }

}
