package com.football_club.MatchTracking.event;

import lombok.Value;

@Value
public class GameUpdatedEvent {
    Long id;
    String status;
    Long homeClubId;
    Long awayClubId;
}
