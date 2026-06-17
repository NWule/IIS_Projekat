package com.football_club.Scouting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerRecommendationDTO {
    private Long playerId;
    private String name;
    private String surname;
    private double score;
    private String source;
}
