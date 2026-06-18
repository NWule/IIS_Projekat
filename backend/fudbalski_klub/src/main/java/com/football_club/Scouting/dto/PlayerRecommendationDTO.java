package com.football_club.Scouting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerRecommendationDTO {
    private Long playerId;
    private String name;
    private String surname;
    private double score;
    private String source;
}
