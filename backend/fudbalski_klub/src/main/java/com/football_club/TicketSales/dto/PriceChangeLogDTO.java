package com.football_club.TicketSales.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceChangeLogDTO {
    private Long id;
    private Long gameId;
    private String gameLabel;
    private Long zoneId;
    private String zoneName;
    private Long ruleId;
    private String ruleName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal priceDiff;
    private LocalDateTime changedAt;
}
