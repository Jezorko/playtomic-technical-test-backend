package com.playtomic.tests.wallet.wallets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
class WalletController {

    @RequestMapping("/")
    void log() {
        log.info("Logging from /");
    }
}
