package com.football_club.MatchTracking.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LiveMatchSyncEvent {
    private final Long teamStatisticId;
    private final Long appearanceId;
    private final Long gameId;
}