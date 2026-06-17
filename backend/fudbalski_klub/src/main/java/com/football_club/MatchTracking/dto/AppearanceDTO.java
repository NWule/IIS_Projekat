package com.football_club.MatchTracking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppearanceDTO {
    private Long id;
    private Long playsForId;
    private String playerName;
    private String playerSurname;
    private Long gameId;
    private String matchRole;
    private int minutesPlayed;
    private int goals;
    private int assists;
    private int fouls;
    private int yellowCards;
    private boolean redCard;
    private double rating;
    private double passingAccuracy;
    private int clubId;
}