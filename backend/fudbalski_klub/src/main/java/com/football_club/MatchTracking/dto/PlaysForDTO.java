package com.football_club.MatchTracking.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaysForDTO {
    private Long id;
    private Long playerId;
    private String playerName;
    private String playerSurname;
    private int clubId;
    private String clubName;
    private int jerseyNumber;
    private LocalDate contractStart;
    private LocalDate contractEnd;
    private String position;
}