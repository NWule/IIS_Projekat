package com.football_club.MatchTracking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubDTO {
    private Integer id;
    private String name;
    private String location;
    private int wins;
    private int losses;
    private int draws;
    private int goalsScored;
    private int goalsConceded;
}