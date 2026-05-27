package com.football_club.Scouting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueSaveDTO {
    private String name;
    private double difficultyMultiplier;
}
