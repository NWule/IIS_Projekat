package com.football_club.TicketSales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneAnalyticsDTO {
    private Long zoneId;
    private String zoneName;
    private String zoneColor;
    private long totalSeats;
    private long ticketsSold;
    private BigDecimal occupancyPercent;
    private BigDecimal revenue;
}
