package com.playtomic.tests.wallet.wallets;

import java.util.UUID;

final class WalletCouldNotBeToppedUpException extends RuntimeException {

    WalletCouldNotBeToppedUpException(final UUID id) {
        super("failed to top up wallet with ID " + id);
    }

}
