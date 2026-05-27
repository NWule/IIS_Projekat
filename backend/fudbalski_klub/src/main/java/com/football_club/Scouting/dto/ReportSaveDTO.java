package com.football_club.Scouting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSaveDTO {
    private Long playerId;
    private String overallCommentary;
    private Integer clubAtTimeId;
    private double leagueMultiplierAtTime;
}
