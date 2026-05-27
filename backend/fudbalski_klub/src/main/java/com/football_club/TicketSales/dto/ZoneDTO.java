package com.football_club.TicketSales.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneDTO {
    private Long id;
    private String name;
    private String color;
    private BigDecimal basePrice;
    private int numberOfRows;
    private int seatsPerRow;
}
