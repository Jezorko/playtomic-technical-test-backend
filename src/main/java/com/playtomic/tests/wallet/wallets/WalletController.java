package com.playtomic.tests.wallet.wallets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;

@Slf4j
@RestController("/wallets")
@RequiredArgsConstructor(access = PACKAGE)
class WalletController {

    private final WalletService service;

    @RequestMapping("/{id}")
    void getById(final UUID id) {
        service.getById(id);
    }
}
