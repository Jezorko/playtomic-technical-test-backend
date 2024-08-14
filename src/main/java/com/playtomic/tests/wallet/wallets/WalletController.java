package com.playtomic.tests.wallet.wallets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor(access = PACKAGE)
class WalletController {

    private final WalletService service;

    @GetMapping("/{id}")
    ResponseEntity<Wallet> getById(final @PathVariable UUID id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> notFound().build());
    }


    @PostMapping("/{id}/topUps")
    ResponseEntity<Void> topUp(final @PathVariable UUID id, final @RequestBody WalletTopUpRequest request) {
        try {
            service.topUp(id, request.cardNumber(), request.topUpAmount());
        } catch (final NoWalletToppedUpException noWalletToppedUpException) {
            return notFound().build();
        } catch (final Exception otherException) {
            return internalServerError().build();
        }

        return noContent().build();
    }

}
