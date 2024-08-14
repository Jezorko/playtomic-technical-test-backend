package com.playtomic.tests.wallet.wallets;

import com.playtomic.tests.wallet.IntegrationTest;
import com.playtomic.tests.wallet.service.Payment;
import com.playtomic.tests.wallet.service.StripeAmountTooSmallException;
import com.playtomic.tests.wallet.service.StripeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static com.playtomic.tests.wallet.TestingExtensions.comparingEqualTo;
import static java.math.BigDecimal.ZERO;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@IntegrationTest
class WalletServiceIT {

    private final static String SOME_CARD_NUMBER = "372157631126685";
    private final static BigDecimal SOME_TOP_UP_AMOUNT = BigDecimal.valueOf(100L);
    private final static String SOME_PAYMENT_ID = "test";

    @Autowired
    private WalletService walletService;

    @MockBean
    private StripeService stripeService;

    @Test
    void shouldCreateAndFetchANewWallet() {
        // when a new wallet is created
        var createdResult = assertDoesNotThrow(() -> walletService.create());

        // then the balance is zero
        assertThat(createdResult)
                .isNotNull()
                .extracting(Wallet::getBalance)
                .is(comparingEqualTo(ZERO));

        // when the same wallet is fetched by ID
        var fetchedResult = walletService.getById(createdResult.getId());

        // then the result is equal (but not same as) the created original
        assertThat(fetchedResult)
                .get()
                .isNotSameAs(createdResult)
                .returns(createdResult.getId(), Wallet::getId)
                .extracting(Wallet::getBalance)
                .is(comparingEqualTo(createdResult.getBalance()));
    }

    @Test
    void whenToppingUpSuccessful_shouldUpdateBalance() {
        // given a wallet
        final var wallet = walletService.create();

        // and a successful payment
        when(stripeService.charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .thenReturn(new Payment(SOME_PAYMENT_ID));

        // when topped up
        walletService.topUp(wallet.getId(), SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT);

        // should call stripe
        verify(stripeService, times(1)).charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT);

        // and fetch new balance
        final var newBalance = walletService.getById(wallet.getId());
        assertThat(newBalance).get()
                .extracting(Wallet::getBalance)
                .is(comparingEqualTo(SOME_TOP_UP_AMOUNT));
    }

    @Test
    void whenToppingUpUnsuccessfulBecauseOfStripe_shouldThrowAndNotUpdate() {
        // given a wallet
        final var wallet = walletService.create();

        // and an unsuccessful payment
        when(stripeService.charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .thenThrow(new StripeAmountTooSmallException());

        // when topped up
        assertThatCode(() -> walletService.topUp(wallet.getId(), SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .isInstanceOf(StripeAmountTooSmallException.class);

        // should call stripe
        verify(stripeService, times(1)).charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT);

        // and return same balance
        final var newBalance = walletService.getById(wallet.getId());
        assertThat(newBalance).get()
                .extracting(Wallet::getBalance)
                .is(comparingEqualTo(ZERO));
    }

    @Test
    void whenToppingUpUnsuccessfulBecauseOfWallet_shouldThrowAndRefund() {
        // given non-existing wallet
        final var walletId = randomUUID();

        // and a successful payment
        when(stripeService.charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .thenReturn(new Payment(SOME_PAYMENT_ID));

        // when topping up fails
        assertThatCode(() -> walletService.topUp(walletId, SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .isInstanceOf(WalletCouldNotBeToppedUpException.class);

        // should refund with Stripe
        verify(stripeService, times(1)).refund(SOME_PAYMENT_ID);
    }

}