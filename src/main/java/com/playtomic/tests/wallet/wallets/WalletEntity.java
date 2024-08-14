package com.playtomic.tests.wallet.wallets;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@ToString
@EqualsAndHashCode
@Entity(name = "Wallet")
@Table(name = "wallets")
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor(access = PROTECTED)
class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal balance;

}
