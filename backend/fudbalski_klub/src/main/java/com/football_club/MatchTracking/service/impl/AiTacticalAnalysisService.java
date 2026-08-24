package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.MatchTimelineIntervalDTO;
import com.football_club.MatchTracking.dto.TacticalAnalysisRequestDTO;
import com.football_club.MatchTracking.dto.TacticalAnalysisResponseDTO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate;

    @Value("${ai.microservice.url:http://localhost:8000/api/v1/tactical/generate}")
    private String pythonAiUrl;

    public String generateMatchReport(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Utakmica nije pronađena"));

        CompletableFuture<Map<String, Double>> expectedStatsFuture = CompletableFuture.supplyAsync(() -> fetchExpectedStats(gameId));
        CompletableFuture<Map<String, Double>> actualStatsFuture = CompletableFuture.supplyAsync(() -> fetchActualStats(gameId));
        CompletableFuture<List<AppearanceGraph>> appearancesFuture = CompletableFuture.supplyAsync(() -> appearanceGraphRepository.findByGameGraphId(gameId));
        CompletableFuture<List<TacticalAnalysisGraph>> anomaliesFuture = CompletableFuture.supplyAsync(() -> tacticalAnalysisRepository.findByGameGraphId(gameId));
        CompletableFuture<List<MatchTimelineIntervalDTO>> timelineFuture = CompletableFuture.supplyAsync(() -> fetchInfluxTimeline(gameId, game));

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

            TacticalAnalysisRequestDTO payload = TacticalAnalysisRequestDTO.builder()
                    .matchTitle(matchTitle)
                    .expectedStats(expectedStatsFuture.get())
                    .actualStats(actualStatsFuture.get())
                    .topPerformers(topPerformers)
                    .underperformers(underperformers)
                    .matchTimeline(timelineFuture.get())
                    .tacticalAnomalies(formattedAnomalies)
                    .build();

            TacticalAnalysisResponseDTO response = restTemplate.postForObject(pythonAiUrl, payload, TacticalAnalysisResponseDTO.class);

            return response != null ? response.getReport() : "Greška u AI servisu.";

        } catch (Exception e) {
            throw new RuntimeException("Greška prilikom agregacije i slanja podataka AI mikroservisu: " + e.getMessage());
        }
    }

    private Map<String, Double> fetchExpectedStats(Long gameId) {
        GameGraph gameGraph = gameGraphRepository.findById(gameId).orElse(null);
        Map<String, Double> stats = new HashMap<>();
        if (gameGraph != null) {
            stats.put("Expected Home Goals", gameGraph.getExpectedHomeGoals());
            stats.put("Expected Away Goals", gameGraph.getExpectedAwayGoals());
            stats.put("Expected Home Pass Rate", gameGraph.getExpectedHomePassSuccessRate());
            stats.put("Expected Away Pass Rate", gameGraph.getExpectedAwayPassSuccessRate());
            stats.put("Expected Home Shots", gameGraph.getExpectedHomeShots());
            stats.put("Expected Away Shots", gameGraph.getExpectedAwayShots());
        }
        return stats;
    }

    private Map<String, Double> fetchActualStats(Long gameId) {
        TeamStatistic ts = teamStatisticRepository.findByGameId(gameId).orElse(null);
        Map<String, Double> stats = new HashMap<>();
        if (ts != null) {
            stats.put("Actual Home Goals", (double) ts.getHomeGoals());
            stats.put("Actual Away Goals", (double) ts.getAwayGoals());
            stats.put("Actual Home Pass Rate", ts.getHomePassSuccessRate());
            stats.put("Actual Away Pass Rate", ts.getAwayPassSuccessRate());
            stats.put("Actual Home Shots", (double) ts.getHomeShots());
            stats.put("Actual Away Shots", (double) ts.getAwayShots());
        }
        return stats;
    }

    private List<MatchTimelineIntervalDTO> fetchInfluxTimeline(Long gameId, Game game) {
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
        List<MatchTimelineIntervalDTO> timeline = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            int home = homeEvents[i];
            int away = awayEvents[i];

            if (home == 0 && away == 0) continue;

            String momentum;
            if (home > away + 2) momentum = "Totalna ofanziva i dominacija domaćina.";
            else if (away > home + 2) momentum = "Totalna ofanziva i dominacija gosta.";
            else momentum = "Ujednačena borba, igra na sredini terena.";

            String description = String.format("%s (Domaćin ofanzivne akcije: %d | Gost ofanzivne akcije: %d)", momentum, home, away);
            timeline.add(new MatchTimelineIntervalDTO(intervals[i], description, home + away));
        }

        return timeline;
    }
}
