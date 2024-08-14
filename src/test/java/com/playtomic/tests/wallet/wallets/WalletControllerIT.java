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

import java.math.BigDecimal;

import static com.playtomic.tests.wallet.TestingExtensions.comparingEqualTo;
import static java.math.BigDecimal.ZERO;
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
        mockMvc.perform(get("/wallets/" + randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void whenWalletExists_shouldReturnWallet() {
        // given an existing wallet
        final var wallet = service.create();
        repository.flush();

        // when fetched by ID
        final var fetchResponse = mockMvc.perform(get("/wallets/" + wallet.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final var fetchedWallet = objectMapper.readValue(fetchResponse.getContentAsString(), Wallet.class);

        // should have same balance as original
        assertThat(fetchedWallet)
                .returns(wallet.getId(), Wallet::getId)
                .extracting(Wallet::getBalance)
                .is(comparingEqualTo(wallet.getBalance()));
    }

    @Test
    @SneakyThrows
    void whenTopUpSuccessful_shouldReturnWalletWithNewBalance() {
        // given an existing wallet
        final var wallet = service.create();
        repository.flush();

        // and a top-up request
        final var request = new WalletTopUpRequest(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT);

        // and a successful payment
        when(stripeService.charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .thenReturn(new Payment(SOME_PAYMENT_ID));

        // when topped up
        mockMvc.perform(
                        post("/wallets/" + wallet.getId() + "/topUps")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isNoContent());

        // and fetched
        final var fetchResponse = mockMvc.perform(get("/wallets/" + wallet.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final var fetchedWallet = objectMapper.readValue(fetchResponse.getContentAsString(), Wallet.class);

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
        mockMvc.perform(
                        post("/wallets/" + walletId + "/topUps")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void whenTopUpUnsuccessfulBecauseOfStripeIssue_shouldReturnSameBalance() {
        // given an existing wallet
        final var wallet = service.create();
        repository.flush();

        // and a top-up request
        final var request = new WalletTopUpRequest(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT);

        // and a failed payment
        when(stripeService.charge(SOME_CARD_NUMBER, SOME_TOP_UP_AMOUNT))
                .thenThrow(new StripeAmountTooSmallException());

        // when topped up
        mockMvc.perform(
                        post("/wallets/" + wallet.getId() + "/topUps")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isInternalServerError());

        // and fetched
        final var fetchResponse = mockMvc.perform(get("/wallets/" + wallet.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final var fetchedWallet = objectMapper.readValue(fetchResponse.getContentAsString(), Wallet.class);

        // should have same balance as original
        assertThat(fetchedWallet)
                .returns(wallet.getId(), Wallet::getId)
                .extracting(Wallet::getBalance)
                .is(comparingEqualTo(ZERO));
    }

}