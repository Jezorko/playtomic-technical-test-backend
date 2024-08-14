package com.playtomic.tests.wallet.wallets;

import java.math.BigDecimal;

record WalletTopUpRequest(String cardNumber, BigDecimal topUpAmount) {
}
