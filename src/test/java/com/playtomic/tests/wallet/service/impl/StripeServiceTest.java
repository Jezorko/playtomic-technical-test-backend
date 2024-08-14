package com.playtomic.tests.wallet.service.impl;


import com.playtomic.tests.wallet.service.StripeAmountTooSmallException;
import com.playtomic.tests.wallet.service.StripeServiceException;
import com.playtomic.tests.wallet.service.StripeService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.math.BigDecimal;
import java.net.URI;

/**
 * This test is failing with the current implementation.
 *
 * How would you test this?
 *
 * A: Ideally use contract testing, but if contracts not provided - mock of the service.
 *    The latter could be done with e.g. WireMock.
 */
public class StripeServiceTest {

    URI testUri = URI.create("http://how-would-you-test-me.localhost");
    StripeService s = new StripeService(testUri, testUri, new RestTemplateBuilder());

    @Test
    public void test_exception() {
        Assertions.assertThrows(StripeAmountTooSmallException.class, () -> {
            s.charge("4242 4242 4242 4242", new BigDecimal(5));
        });
    }

    @Test
    public void test_ok() throws StripeServiceException {
        s.charge("4242 4242 4242 4242", new BigDecimal(15));
    }
}
