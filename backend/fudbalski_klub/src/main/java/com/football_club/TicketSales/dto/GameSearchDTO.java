package com.football_club.TicketSales.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSearchDTO {
    private Long id;
    private LocalDateTime matchDate;
    private int homeClubId;
    private String homeClubName;
    private int awayClubId;
    private String awayClubName;
    private BigDecimal lowestPrice;
}
