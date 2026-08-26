package com.football_club.MatchTracking.service.impl;

import com.football_club.Clients.LLMClient;
import com.football_club.client.model.MatchTimelineInterval;
import com.football_club.client.model.TacticalAnalysisRequest;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.TeamStatistic;
import com.football_club.MatchTracking.model.graph.AppearanceGraph;
import com.football_club.MatchTracking.model.graph.GameGraph;
import com.football_club.MatchTracking.model.graph.TacticalAnalysisGraph;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.MatchTracking.repository.jpa.MatchEventRepository;
import com.football_club.MatchTracking.repository.jpa.TeamStatisticRepository;
import com.football_club.MatchTracking.repository.graph.AppearanceGraphRepository;
import com.football_club.MatchTracking.repository.graph.GameGraphRepository;
import com.football_club.MatchTracking.repository.graph.TacticalAnalysisRepository;
import com.football_club.MatchTracking.service.IAiTacticalAnalysisService;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiTacticalAnalysisService implements IAiTacticalAnalysisService {

    private final GameRepository gameRepository;
    private final GameGraphRepository gameGraphRepository;
    private final TeamStatisticRepository teamStatisticRepository;
    private final AppearanceGraphRepository appearanceGraphRepository;
    private final TacticalAnalysisRepository tacticalAnalysisRepository;
    private final MatchEventRepository matchEventRepository;

    private final LLMClient llmClient;

    @Override
    public String generateMatchReport(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Utakmica nije pronađena"));

        CompletableFuture<Map<String, BigDecimal>> expectedStatsFuture = CompletableFuture.supplyAsync(() -> fetchExpectedStats(gameId));
        CompletableFuture<Map<String, BigDecimal>> actualStatsFuture = CompletableFuture.supplyAsync(() -> fetchActualStats(gameId));
        CompletableFuture<List<AppearanceGraph>> appearancesFuture = CompletableFuture.supplyAsync(() -> appearanceGraphRepository.findByGameGraphId(gameId));
        CompletableFuture<List<TacticalAnalysisGraph>> anomaliesFuture = CompletableFuture.supplyAsync(() -> tacticalAnalysisRepository.findByGameGraphId(gameId));
        CompletableFuture<List<MatchTimelineInterval>> timelineFuture = CompletableFuture.supplyAsync(() -> fetchInfluxTimeline(gameId, game));

        CompletableFuture.allOf(expectedStatsFuture, actualStatsFuture, appearancesFuture, anomaliesFuture, timelineFuture).join();

        try {
            List<AppearanceGraph> apps = appearancesFuture.get();
            List<String> topPerformers = apps.stream()
                    .sorted(Comparator.comparing(AppearanceGraph::getRating).reversed())
                    .limit(3)
                    .map(a -> "Rating " + a.getRating() + " (Golovi: " + a.getGoals() + ", Asist: " + a.getAssists() + ")")
                    .collect(Collectors.toList());

            List<String> underperformers = apps.stream()
                    .sorted(Comparator.comparing(AppearanceGraph::getRating))
                    .limit(3)
                    .map(a -> "Rating " + a.getRating() + " (Dodavanja: " + a.getPassingAccuracy() + "%)")
                    .collect(Collectors.toList());

            List<String> formattedAnomalies = anomaliesFuture.get().stream()
                    .map(an -> "[Minut " + an.getMatchMinute() + " | " + an.getSeverity() + "] " +
                            an.getDescription() + " -> Rešenje: " +
                            (an.getRecommendation() != null ? an.getRecommendation().getRecommendationText() : "Nema"))
                    .collect(Collectors.toList());

            String matchTitle = game.getHomeClub().getName() + " vs " + game.getAwayClub().getName();

            TacticalAnalysisRequest payload = new TacticalAnalysisRequest();
            payload.setMatchTitle(matchTitle);
            payload.setExpectedStats(expectedStatsFuture.get());
            payload.setActualStats(actualStatsFuture.get());
            payload.setTopPerformers(topPerformers);
            payload.setUnderperformers(underperformers);
            payload.setMatchTimeline(timelineFuture.get());
            payload.setTacticalAnomalies(formattedAnomalies);

            return llmClient.generateTacticalReport(payload).getReport();

        } catch (Exception e) {
            throw new RuntimeException("Greška prilikom agregacije i slanja podataka AI mikroservisu: " + e.getMessage());
        }
    }

    private Map<String, BigDecimal> fetchExpectedStats(Long gameId) {
        GameGraph gameGraph = gameGraphRepository.findById(gameId).orElse(null);
        Map<String, BigDecimal> stats = new HashMap<>();
        if (gameGraph != null) {
            stats.put("Expected Home Goals", BigDecimal.valueOf(gameGraph.getExpectedHomeGoals() != null ? gameGraph.getExpectedHomeGoals() : 0.0));
            stats.put("Expected Away Goals", BigDecimal.valueOf(gameGraph.getExpectedAwayGoals() != null ? gameGraph.getExpectedAwayGoals() : 0.0));
            stats.put("Expected Home Pass Rate", BigDecimal.valueOf(gameGraph.getExpectedHomePassSuccessRate() != null ? gameGraph.getExpectedHomePassSuccessRate() : 0.0));
            stats.put("Expected Away Pass Rate", BigDecimal.valueOf(gameGraph.getExpectedAwayPassSuccessRate() != null ? gameGraph.getExpectedAwayPassSuccessRate() : 0.0));
        }
        return stats;
    }

    private Map<String, BigDecimal> fetchActualStats(Long gameId) {
        TeamStatistic ts = teamStatisticRepository.findByGameId(gameId).orElse(null);
        Map<String, BigDecimal> stats = new HashMap<>();
        if (ts != null) {
            stats.put("Actual Home Goals", BigDecimal.valueOf(ts.getHomeGoals()));
            stats.put("Actual Away Goals", BigDecimal.valueOf(ts.getAwayGoals()));
            stats.put("Actual Home Pass Rate", BigDecimal.valueOf(ts.getHomePassSuccessRate()));
            stats.put("Actual Away Pass Rate", BigDecimal.valueOf(ts.getAwayPassSuccessRate()));
        }
        return stats;
    }

    private List<MatchTimelineInterval> fetchInfluxTimeline(Long gameId, Game game) {
        List<FluxTable> tables = matchEventRepository.getTimelineEventsForGame(gameId);
        int[] homeEvents = new int[6];
        int[] awayEvents = new int[6];
        String homeClubId = String.valueOf(game.getHomeClub().getId());

        for (FluxTable table : tables) {
            for (var record : table.getRecords()) {
                if (record.getValueByKey("matchMinute") == null || record.getValueByKey("eventType") == null) continue;
                int minute = Integer.parseInt(record.getValueByKey("matchMinute").toString());
                String clubId = String.valueOf(record.getValueByKey("clubId"));
                String eventType = String.valueOf(record.getValueByKey("eventType"));

                if (eventType.equals("SHOT") || eventType.equals("SHOT_ON_TARGET") || eventType.equals("CORNER") || eventType.equals("GOAL")) {
                    int bucketIndex = Math.min(minute / 15, 5);
                    if (clubId.equals(homeClubId)) {
                        homeEvents[bucketIndex]++;
                    } else {
                        awayEvents[bucketIndex]++;
                    }
                }
            }
        }

        String[] intervals = {"0-15 min", "16-30 min", "31-45+ min", "46-60 min", "61-75 min", "76-90+ min"};
        List<MatchTimelineInterval> timeline = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            int home = homeEvents[i];
            int away = awayEvents[i];
            if (home == 0 && away == 0) continue;

            String momentum;
            if (home > away + 2) momentum = "Totalna ofanziva i dominacija domaćina.";
            else if (away > home + 2) momentum = "Totalna ofanziva i dominacija gosta.";
            else momentum = "Ujednačena borba, igra na sredini terena.";

            String description = String.format("%s (Domaćin ofanzivne akcije: %d | Gost ofanzivne akcije: %d)", momentum, home, away);

            MatchTimelineInterval intervalData = new MatchTimelineInterval();
            intervalData.setInterval(intervals[i]);
            intervalData.setMomentumDescription(description);
            intervalData.setKeyEventsCount(home + away);
            timeline.add(intervalData);
        }
        return timeline;
    }
}