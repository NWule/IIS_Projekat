package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.model.graph.GameGraph;
import com.football_club.MatchTracking.model.graph.TacticalAnalysisGraph;
import com.football_club.MatchTracking.model.graph.TeamStatisticGraph;

import java.util.List;

public interface ITacticalAnalysisService {
    void runAnalysis(Long targetTeamId, TeamStatisticGraph stats, GameGraph game);
    List<TacticalAnalysisGraph> getAnalysisForGame(Long gameId);
}
