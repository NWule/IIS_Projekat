package com.football_club.MatchTracking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TacticalAnalysisDTO {
    private Long teamId;
    private String analysisText;
    private String recommendationText;
    private String severity;
    private int matchMinute;
}
