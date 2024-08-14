package com.playtomic.tests.wallet;

import org.assertj.core.api.HamcrestCondition;

import static org.hamcrest.Matchers.comparesEqualTo;

public final class TestingExtensions {

    public static <T extends Comparable<T>> HamcrestCondition<T> comparingEqualTo(final T other) {
        return new HamcrestCondition<>(comparesEqualTo(other));
    }

}
