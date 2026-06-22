package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.TacticalAnalysisDTO;
import com.football_club.MatchTracking.model.Appearance;
import com.football_club.MatchTracking.model.graph.*;
import com.football_club.MatchTracking.repository.graph.AppearanceGraphRepository;
import com.football_club.MatchTracking.repository.graph.TacticalAnalysisRepository;
import com.football_club.MatchTracking.service.ITacticalAnalysisService;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TacticalAnalysisService implements ITacticalAnalysisService {
    private final KieContainer kieContainer;
    private final TacticalAnalysisRepository analysisRepository;
    private final AppearanceGraphRepository appearanceGraphRepository;

    @Override
    @Transactional
    public void runAnalysis(Long targetTeamId, TeamStatisticGraph stats, GameGraph game) {
        KieSession kSession = kieContainer.newKieSession("rulesSession");

        List<AppearanceGraph> allAppearances = appearanceGraphRepository.findByGameGraphId(game.getId());

        TacticalAnalysisDTO fact = new TacticalAnalysisDTO();
        fact.setTeamId(targetTeamId);

        kSession.insert(stats);
        kSession.insert(game);

        for (AppearanceGraph app : allAppearances) {
            kSession.insert(app);
        }
        kSession.insert(fact);

        kSession.fireAllRules();

        int brojOkinutihPravila = kSession.fireAllRules();
        System.out.println(">>> [DROOLS] IZVRŠENO PRAVILA ZA TIM " + targetTeamId + ": " + brojOkinutihPravila);

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

    @Override
    public List<TacticalAnalysisGraph> getAnalysisForGame(Long gameId){
        return analysisRepository.findByGameGraphId(gameId);
    }
}
