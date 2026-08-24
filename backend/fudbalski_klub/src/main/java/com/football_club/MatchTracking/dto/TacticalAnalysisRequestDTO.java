package com.football_club.MatchTracking.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class TacticalAnalysisRequestDTO {
    private String matchTitle;
    private Map<String, Double> expectedStats;
    private Map<String, Double> actualStats;
    private List<String> topPerformers;
    private List<String> underperformers;
    private List<MatchTimelineIntervalDTO> matchTimeline;
    private List<String> tacticalAnomalies;
}