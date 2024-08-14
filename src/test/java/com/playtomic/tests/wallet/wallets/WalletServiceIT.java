package com.playtomic.tests.wallet.wallets;

import com.playtomic.tests.wallet.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.playtomic.tests.wallet.TestingExtensions.comparingEqualTo;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@IntegrationTest
class WalletServiceIT {

    @Autowired
    private WalletService walletService;

    @Test
    void shouldCreateAndFetchANewWallet() {
        // when a new wallet is created
        var createdResult = assertDoesNotThrow(() -> walletService.create());

        // then the balance is zero
        assertThat(createdResult)
                .isNotNull()
                .extracting(WalletEntity::getBalance)
                .is(comparingEqualTo(ZERO));

        // when the same wallet is fetched by ID
        var fetchedResult = walletService.getById(createdResult.getId());

        // then the result is equal (but not same as) the created original
        assertThat(fetchedResult)
                .get()
                .isNotSameAs(createdResult)
                .returns(createdResult.getId(), WalletEntity::getId)
                .extracting(WalletEntity::getBalance)
                .is(comparingEqualTo(createdResult.getBalance()));
    }

}