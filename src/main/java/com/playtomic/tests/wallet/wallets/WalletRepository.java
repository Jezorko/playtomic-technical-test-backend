package com.playtomic.tests.wallet.wallets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface WalletRepository extends JpaRepository<WalletEntity, UUID> {
}
