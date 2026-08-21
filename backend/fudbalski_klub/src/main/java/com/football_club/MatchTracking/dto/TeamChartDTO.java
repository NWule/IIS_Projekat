package com.football_club.MatchTracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamChartDTO {
    private LocalDateTime matchDate;
    private int goals;
    private double passSuccessRate;

    private int shots;
    private int shotsOnTarget;
    private int fouls;
    private int corners;
    private int offsides;
}
