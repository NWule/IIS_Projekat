package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.model.graph.GameGraph;
import com.football_club.MatchTracking.model.graph.TeamStatisticGraph;

public interface ITacticalAnalysisService {
    void runAnalysis(TeamStatisticGraph stats, GameGraph game);
}
