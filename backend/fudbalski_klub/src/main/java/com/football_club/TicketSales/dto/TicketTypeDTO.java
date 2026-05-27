package com.football_club.TicketSales.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketTypeDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal priceModifier;
}
