package com.football_club.TicketSales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameAnalyticsDTO {
    private Long gameId;
    private String gameLabel;
    private long totalTicketsSold;
    private BigDecimal totalRevenue;
    private BigDecimal overallOccupancyPercent;
    private long priceChangeCount;
    private List<ZoneAnalyticsDTO> zoneAnalytics;
    private List<TicketTypeAnalyticsDTO> ticketTypeAnalytics;
}
