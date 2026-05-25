package com.football_club.MatchTracking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamStatisticDTO {
    private Long id;
    private Long gameId;
    private int homeGoals;
    private int awayGoals;
    private int homeShots;
    private int awayShots;
    private double homePossession;
    private double awayPossession;
    private int homeShotsOnTarget;
    private int awayShotsOnTarget;
    private int homeFouls;
    private int awayFouls;
    private int homeCorners;
    private int awayCorners;
    private int homeOffsides;
    private int awayOffsides;
    private double homePassSuccessRate;
    private double awayPassSuccessRate;
}