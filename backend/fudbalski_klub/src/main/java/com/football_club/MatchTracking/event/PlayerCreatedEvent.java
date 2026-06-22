package com.football_club.MatchTracking.event;

import com.football_club.MatchTracking.model.enums.PlayerPosition;
import lombok.Value;

@Value
public class PlayerCreatedEvent {
    Long id;
    String name;
    String surname;
    PlayerPosition position;
}