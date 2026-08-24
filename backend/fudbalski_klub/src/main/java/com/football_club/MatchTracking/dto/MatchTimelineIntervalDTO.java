package com.football_club.MatchTracking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchTimelineIntervalDTO {
    private String interval;
    private String momentumDescription;
    private int keyEventsCount;
}
