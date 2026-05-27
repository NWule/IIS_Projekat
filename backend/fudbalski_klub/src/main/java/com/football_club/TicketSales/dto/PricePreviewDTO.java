package com.football_club.TicketSales.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricePreviewDTO {
    private Long zoneId;
    private String zoneName;
    private Long ticketTypeId;
    private String ticketTypeName;
    private BigDecimal basePrice;
    private BigDecimal priceModifier;
    private BigDecimal finalPrice;
}
