package com.football_club.MatchTracking.event;

import com.football_club.MatchTracking.model.enums.MatchRole;
import lombok.Value;

@Value
public class AppearanceUpdatedEvent {
    Long id;
    MatchRole matchRole;
    int minutesPlayed;
    int goals;
    int assists;
    int fouls;
    int yellowCards;
    boolean redCard;
    double rating;
    double passingAccuracy;
    Long playerId;
    Long gameId;
}