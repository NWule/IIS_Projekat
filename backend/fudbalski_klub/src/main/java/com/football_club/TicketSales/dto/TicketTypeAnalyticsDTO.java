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
public class TicketTypeAnalyticsDTO {
    private String typeName;
    private long ticketsSold;
    private BigDecimal revenue;
}
