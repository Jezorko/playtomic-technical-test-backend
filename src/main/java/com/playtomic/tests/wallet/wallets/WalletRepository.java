package com.playtomic.tests.wallet.wallets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
interface WalletRepository extends JpaRepository<WalletEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("update Wallet wallet" +
            " set wallet.balance = wallet.balance+?2" +
            " where wallet.id = ?1")
    int topUp(final UUID id, final BigDecimal amount);

}
