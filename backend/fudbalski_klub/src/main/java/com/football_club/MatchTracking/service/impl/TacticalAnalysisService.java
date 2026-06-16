package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.TacticalAnalysisDTO;
import com.football_club.MatchTracking.model.graph.*;
import com.football_club.MatchTracking.repository.graph.TacticalAnalysisRepository;
import com.football_club.MatchTracking.service.ITacticalAnalysisService;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TacticalAnalysisService implements ITacticalAnalysisService {
    private final KieContainer kieContainer;
    private final TacticalAnalysisRepository analysisRepository;

    @Override
    @Transactional
    public void runAnalysis(TeamStatisticGraph stats, GameGraph game) {
        KieSession kSession = kieContainer.newKieSession("rulesSession");

        TacticalAnalysisDTO fact = new TacticalAnalysisDTO();

        kSession.insert(stats);
        kSession.insert(fact);

        kSession.fireAllRules();
        kSession.dispose();

        if (fact.getAnalysisText() != null) {
            TacticalRecommendationGraph rec = new TacticalRecommendationGraph();
            rec.setRecommendationText(fact.getRecommendationText());

            TacticalAnalysisGraph analysis = new TacticalAnalysisGraph();
            analysis.setDescription(fact.getAnalysisText());
            analysis.setSeverity(fact.getSeverity());
            analysis.setRecommendation(rec);
            analysis.setGameGraph(game);

            analysisRepository.save(analysis);
        }
    }
}
