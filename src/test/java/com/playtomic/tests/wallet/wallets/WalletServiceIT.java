package com.playtomic.tests.wallet.wallets;

import com.playtomic.tests.wallet.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@IntegrationTest
class WalletServiceIT {

    @Autowired
    private WalletService walletService;

    @Test
    void shouldCreateANewWallet() {
        var result = assertDoesNotThrow(() -> walletService.create());

        assertThat(result)
                .isNotNull()
                .returns(0L, WalletEntity::getBalanceInCents);
    }

}