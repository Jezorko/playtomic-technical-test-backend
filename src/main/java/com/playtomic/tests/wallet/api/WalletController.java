package com.playtomic.tests.wallet.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class WalletController {

    @RequestMapping("/")
    void log() {
        log.info("Logging from /");
    }
}
