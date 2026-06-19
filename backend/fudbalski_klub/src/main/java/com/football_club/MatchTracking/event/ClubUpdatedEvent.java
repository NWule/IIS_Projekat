package com.football_club.MatchTracking.event;

import lombok.Value;

@Value
public class ClubUpdatedEvent {
    Long id;
    String name;
}
