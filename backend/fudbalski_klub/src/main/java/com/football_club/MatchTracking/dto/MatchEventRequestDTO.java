package com.football_club.MatchTracking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchEventRequestDTO {
    private String eventType;
    private Long clubId;
    private Long playsForId;
    private Integer matchMinute;
}