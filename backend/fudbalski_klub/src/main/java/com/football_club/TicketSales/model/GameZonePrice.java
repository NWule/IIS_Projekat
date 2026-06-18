package com.football_club.TicketSales.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "game_zone_prices",
        uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "zone_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameZonePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "zone_id", nullable = false)
    private Long zoneId;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
}
