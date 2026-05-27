package com.football_club.TicketSales.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    private Long id;
    private Long gameId;
    private String homeClubName;
    private String awayClubName;
    private LocalDateTime matchDate;
    private Long seatId;
    private int rowNumber;
    private int seatNumber;
    private String zoneName;
    private String ticketTypeName;
    private BigDecimal price;
    private String ticketCode;
}
