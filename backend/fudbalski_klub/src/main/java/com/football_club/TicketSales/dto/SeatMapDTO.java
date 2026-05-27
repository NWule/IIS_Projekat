package com.football_club.TicketSales.dto;

import com.football_club.TicketSales.model.enums.SeatStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatMapDTO {
    private Long id;
    private int rowNumber;
    private int seatNumber;
    private SeatStatus status;
}
