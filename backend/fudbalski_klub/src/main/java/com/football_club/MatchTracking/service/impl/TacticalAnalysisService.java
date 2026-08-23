package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.TacticalAnalysisDTO;
import com.football_club.MatchTracking.model.Appearance;
import com.football_club.MatchTracking.model.graph.*;
import com.football_club.MatchTracking.repository.graph.AppearanceGraphRepository;
import com.football_club.MatchTracking.repository.graph.GameGraphRepository;
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
    private final GameGraphRepository gameGraphRepository;

    @Override
    @Transactional
    public void runAnalysis(Long targetTeamId, TeamStatisticGraph stats, GameGraph game) {
        if (game.getExpectedHomeGoals() == null || game.getExpectedAwayGoals() == null) {

            GameGraphRepository.ClubFormAverages homeForm = gameGraphRepository.calculateClubFormForLast5Games(game.getHomeClub().getId());
            GameGraphRepository.ClubFormAverages awayForm = gameGraphRepository.calculateClubFormForLast5Games(game.getAwayClub().getId());

            game.setExpectedHomeGoals(homeForm != null && homeForm.getAvgGoals() != null ? homeForm.getAvgGoals() : 1.5);
            game.setExpectedHomeShots(homeForm != null && homeForm.getAvgShots() != null ? homeForm.getAvgShots() : 10.0);
            game.setExpectedHomeShotsOnTarget(homeForm != null && homeForm.getAvgShotsOnTarget() != null ? homeForm.getAvgShotsOnTarget() : 4.0);
            game.setExpectedHomeFouls(homeForm != null && homeForm.getAvgFouls() != null ? homeForm.getAvgFouls() : 12.0);
            game.setExpectedHomeCorners(homeForm != null && homeForm.getAvgCorners() != null ? homeForm.getAvgCorners() : 4.5);
            game.setExpectedHomeOffsides(homeForm != null && homeForm.getAvgOffsides() != null ? homeForm.getAvgOffsides() : 2.0);
            game.setExpectedHomePassSuccessRate(homeForm != null && homeForm.getAvgPassSuccessRate() != null ? homeForm.getAvgPassSuccessRate() : 75.0);

            game.setExpectedAwayGoals(awayForm != null && awayForm.getAvgGoals() != null ? awayForm.getAvgGoals() : 1.5);
            game.setExpectedAwayShots(awayForm != null && awayForm.getAvgShots() != null ? awayForm.getAvgShots() : 10.0);
            game.setExpectedAwayShotsOnTarget(awayForm != null && awayForm.getAvgShotsOnTarget() != null ? awayForm.getAvgShotsOnTarget() : 4.0);
            game.setExpectedAwayFouls(awayForm != null && awayForm.getAvgFouls() != null ? awayForm.getAvgFouls() : 12.0);
            game.setExpectedAwayCorners(awayForm != null && awayForm.getAvgCorners() != null ? awayForm.getAvgCorners() : 4.5);
            game.setExpectedAwayOffsides(awayForm != null && awayForm.getAvgOffsides() != null ? awayForm.getAvgOffsides() : 2.0);
            game.setExpectedAwayPassSuccessRate(awayForm != null && awayForm.getAvgPassSuccessRate() != null ? awayForm.getAvgPassSuccessRate() : 75.0);

            game = gameGraphRepository.save(game);
        }


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
