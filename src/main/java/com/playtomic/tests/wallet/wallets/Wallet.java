package com.playtomic.tests.wallet.wallets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;

@Getter
@RequiredArgsConstructor(access = PACKAGE)
public final class Wallet {

    private final UUID id;
    private final BigDecimal balance;

    Wallet(final WalletEntity entity) {
        this(entity.getId(), entity.getBalance());
    }

}
