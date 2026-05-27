package com.football_club.TicketSales.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StadiumMapDTO {
    private Long gameId;
    private String homeClubName;
    private String awayClubName;
    private List<ZoneMapDTO> zones;
}
