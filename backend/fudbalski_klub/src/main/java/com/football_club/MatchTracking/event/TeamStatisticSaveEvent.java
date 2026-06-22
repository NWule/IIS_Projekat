package com.football_club.MatchTracking.event;

import lombok.Value;

@Value
public class TeamStatisticSaveEvent {
    Long id;
    Long gameId;
    int homeGoals;
    int awayGoals;
    int homeShots;
    int awayShots;
    int homeShotsOnTarget;
    int awayShotsOnTarget;
    int homeFouls;
    int awayFouls;
    int homeCorners;
    int awayCorners;
    int homeOffsides;
    int awayOffsides;
    double homePassSuccessRate;
    double awayPassSuccessRate;
    boolean isUpdate;
}