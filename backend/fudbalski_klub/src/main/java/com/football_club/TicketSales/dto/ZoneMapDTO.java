package com.football_club.TicketSales.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneMapDTO {
    private Long id;
    private String name;
    private String color;
    private BigDecimal basePrice;
    private int numberOfRows;
    private int seatsPerRow;
    private List<SeatMapDTO> seats;
}
