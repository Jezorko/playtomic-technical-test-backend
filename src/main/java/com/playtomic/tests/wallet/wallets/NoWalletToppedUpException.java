package com.playtomic.tests.wallet.wallets;

import java.util.UUID;

final class NoWalletToppedUpException extends RuntimeException {

    NoWalletToppedUpException(final UUID id) {
        super("failed to top up wallet with ID " + id + ", wallet likely does not exist");
    }

}
