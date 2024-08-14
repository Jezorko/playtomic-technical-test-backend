package com.playtomic.tests.wallet;

import com.playtomic.tests.wallet.wallets.Wallet;
import org.assertj.core.api.HamcrestCondition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;

public final class TestingExtensions {

    public static <T extends Comparable<T>> HamcrestCondition<T> comparingEqualTo(final T other) {
        return new HamcrestCondition<>(comparesEqualTo(other));
    }

    public static void assertWalletsEqual(final Wallet expected, final Wallet actual) {
        assertThat(actual)
                .returns(expected.getId(), Wallet::getId)
                .extracting(Wallet::getBalance)
                .is(comparingEqualTo(expected.getBalance()));
    }

}
