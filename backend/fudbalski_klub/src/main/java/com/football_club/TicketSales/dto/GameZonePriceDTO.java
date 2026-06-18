package com.football_club.TicketSales.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameZonePriceDTO {
    private Long gameId;
    private Long zoneId;
    private String zoneName;
    private String zoneColor;
    private BigDecimal defaultPrice;
    private BigDecimal customPrice;
    private BigDecimal effectivePrice;
}
