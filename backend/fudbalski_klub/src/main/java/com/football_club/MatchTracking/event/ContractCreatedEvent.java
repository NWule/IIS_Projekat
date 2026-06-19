package com.football_club.MatchTracking.event;

import lombok.Value;

@Value
public class ContractCreatedEvent {
    Long playerId;
    Long clubId;
}
