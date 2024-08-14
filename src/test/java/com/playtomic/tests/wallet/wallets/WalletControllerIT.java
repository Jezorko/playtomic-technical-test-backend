package com.playtomic.tests.wallet.wallets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.IntegrationTest;
import com.playtomic.tests.wallet.service.Payment;
import com.playtomic.tests.wallet.service.StripeAmountTooSmallException;
import com.playtomic.tests.wallet.service.StripeService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static com.playtomic.tests.wallet.TestingExtensions.assertWalletsEqual;
import static com.playtomic.tests.wallet.TestingExtensions.comparingEqualTo;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class WalletControllerIT {

    private final static String SOME_CARD_NUMBER = "372157631126685";
    private final static BigDecimal SOME_TOP_UP_AMOUNT = BigDecimal.valueOf(100L);
    private final static String SOME_PAYMENT_ID = "test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired // could be mocked out
    private WalletService service;

    @Autowired
    private WalletRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StripeService stripeService;


    @Test
    @SneakyThrows
    void whenWalletDoesNotExist_shouldReturnNotFound() {
        whenWalletIsFetchedById(randomUUID()).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void whenWalletExists_shouldReturnWallet() {
        // given an existing wallet
        final var wallet = givenAnExistingWallet();

        // when fetched by ID
        final var fetchedWallet = whenWalletObjectIsFetchedById(wallet.getId());

        // should have same balance as original
        assertWalletsEqual(fetchedWallet, wallet);
    }

    @Test
    @SneakyThrows
    void whenTopUpSuccessful_shouldReturnWalletWithNewBalance() {
        // given an existing wallet
        final var wallet = givenAnExistingWallet();

        // and a top-up request
        final var request = new WalletTopUpRequest(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT);

        // and a successful payment
        when(stripeService.charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .thenReturn(new Payment(SOME_PAYMENT_ID));

        // when topped up
        whenWalletIsToppedUp(wallet.getId(), request)
                .andExpect(status().isNoContent());

        // and fetched
        final var fetchedWallet = whenWalletObjectIsFetchedById(wallet.getId());

        // should have updated balance
        assertThat(fetchedWallet)
                .returns(wallet.getId(), Wallet::getId)
                .extracting(Wallet::getBalance)
                .is(comparingEqualTo(SOME_TOP_UP_AMOUNT));
    }

    @Test
    @SneakyThrows
    void whenTopUpUnsuccessfulBecauseWalletDoesNotExist_shouldReturnNotFound() {
        // given non-existing wallet
        final var walletId = randomUUID();

        // and a top-up request
        final var request = new WalletTopUpRequest(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT);

        // and a successful payment
        when(stripeService.charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .thenReturn(new Payment(SOME_PAYMENT_ID));

        // when topped up
        whenWalletIsToppedUp(walletId, request)
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void whenTopUpUnsuccessfulBecauseOfStripeIssue_shouldReturnSameBalance() {
        // given an existing wallet
        final var wallet = givenAnExistingWallet();

        // and a top-up request
        final var request = new WalletTopUpRequest(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT);

        // and a failed payment
        when(stripeService.charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .thenThrow(new StripeAmountTooSmallException());

        // when topped up
        whenWalletIsToppedUp(wallet.getId(), request).andExpect(status().isInternalServerError());

        // and fetched
        final var fetchedWallet = whenWalletObjectIsFetchedById(wallet.getId());

        // should have same balance as original
        assertWalletsEqual(fetchedWallet, wallet);
    }

    private Wallet givenAnExistingWallet() {
        final var wallet = service.create();
        repository.flush();
        return wallet;
    }

    @SneakyThrows
    private ResultActions whenWalletIsFetchedById(final UUID id) {
        return mockMvc.perform(get("/wallets/" + id));
    }

    @SneakyThrows
    private Wallet whenWalletObjectIsFetchedById(final UUID id) {
        final var fetchResponse = whenWalletIsFetchedById(id)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        return objectMapper.readValue(fetchResponse.getContentAsString(), Wallet.class);
    }

    @SneakyThrows
    private ResultActions whenWalletIsToppedUp(UUID walletId, WalletTopUpRequest request) {
        return mockMvc.perform(
                post("/wallets/" + walletId + "/topUps")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        );
    }

}