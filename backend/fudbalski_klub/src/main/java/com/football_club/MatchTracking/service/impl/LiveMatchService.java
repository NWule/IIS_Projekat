package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.MatchEventRequestDTO;
import com.football_club.MatchTracking.model.Appearance;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.MatchEvent;
import com.football_club.MatchTracking.model.TeamStatistic;
import com.football_club.MatchTracking.model.graph.AppearanceGraph;
import com.football_club.MatchTracking.model.graph.GameGraph;
import com.football_club.MatchTracking.model.graph.PlayerGraph;
import com.football_club.MatchTracking.model.graph.TeamStatisticGraph;
import com.football_club.MatchTracking.repository.AppearanceRepository;
import com.football_club.MatchTracking.repository.GameRepository;
import com.football_club.MatchTracking.repository.MatchEventRepository;
import com.football_club.MatchTracking.repository.TeamStatisticRepository;
import com.football_club.MatchTracking.repository.graph.AppearanceGraphRepository;
import com.football_club.MatchTracking.repository.graph.GameGraphRepository;
import com.football_club.MatchTracking.repository.graph.PlayerGraphRepository;
import com.football_club.MatchTracking.repository.graph.TeamStatisticGraphRepository;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveMatchService {
    private final MatchEventRepository matchEventRepository;
    private final TeamStatisticRepository teamStatisticRepository;
    private final AppearanceRepository appearanceRepository;
    private final GameRepository gameRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final TeamStatisticGraphRepository teamStatisticGraphRepository;
    private final AppearanceGraphRepository appearanceGraphRepository;
    private final PlayerGraphRepository playerGraphRepository;
    private final GameGraphRepository gameGraphRepository;

    @Transactional
    public void processLiveEvent(Long gameId, MatchEventRequestDTO dto) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Utakmica nije pronađena"));

        MatchEvent event = MatchEvent.builder()
                .gameId(String.valueOf(gameId))
                .clubId(String.valueOf(dto.getClubId()))
                .playsForId(dto.getPlaysForId() != null ? String.valueOf(dto.getPlaysForId()) : "NONE")
                .eventType(dto.getEventType())
                .matchMinute(dto.getMatchMinute())
                .timestamp(Instant.now())
                .build();
        matchEventRepository.save(event);

        TeamStatistic teamStat = teamStatisticRepository.findByGameId(gameId)
                .orElseThrow(() -> new RuntimeException("Statistika utakmice nije inicijalizovana"));

        updateTeamStatistic(teamStat, game, dto);
        TeamStatistic savedTeamStat = teamStatisticRepository.save(teamStat);
        Appearance savedAppearance = new Appearance();
        if (dto.getPlaysForId() != null) {
            Appearance appearance = appearanceRepository.findByPlaysForIdAndGameId(dto.getPlaysForId(), gameId)
                    .orElseThrow(() -> new RuntimeException("Nastup igrača nije pronađen"));

            updatePlayerAppearance(appearance, gameId, dto);
            savedAppearance = appearanceRepository.save(appearance);
        }
        syncLiveEventWithGraph(savedTeamStat, savedAppearance, game);

        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/team-stats", teamStat);
    }

    private void updateTeamStatistic(TeamStatistic stats, Game game, MatchEventRequestDTO dto) {
        boolean isHomeTeam = game.getHomeClub().getId().equals(dto.getClubId());

        switch (dto.getEventType()) {
            case "GOAL":
                if (isHomeTeam) stats.setHomeGoals(stats.getHomeGoals() + 1);
                else stats.setAwayGoals(stats.getAwayGoals() + 1);
                break;
            case "SHOT":
                if (isHomeTeam) stats.setHomeShots(stats.getHomeShots() + 1);
                else stats.setAwayShots(stats.getAwayShots() + 1);
                break;
            case "SHOT_ON_TARGET":
                if (isHomeTeam) {
                    stats.setHomeShots(stats.getHomeShots() + 1); // Šut u okvir je takođe i šut
                    stats.setHomeShotsOnTarget(stats.getHomeShotsOnTarget() + 1);
                } else {
                    stats.setAwayShots(stats.getAwayShots() + 1);
                    stats.setAwayShotsOnTarget(stats.getAwayShotsOnTarget() + 1);
                }
                break;
            case "FOUL":
                if (isHomeTeam) stats.setHomeFouls(stats.getHomeFouls() + 1);
                else stats.setAwayFouls(stats.getAwayFouls() + 1);
                break;
            case "CORNER":
                if (isHomeTeam) stats.setHomeCorners(stats.getHomeCorners() + 1);
                else stats.setAwayCorners(stats.getAwayCorners() + 1);
                break;
            case "OFFSIDE":
                if (isHomeTeam) stats.setHomeOffsides(stats.getHomeOffsides() + 1);
                else stats.setAwayOffsides(stats.getAwayOffsides() + 1);
                break;
            case "PASS_SUCCESS":
            case "PASS_FAIL":
                recalculateTeamPassingRate(stats, game, dto.getClubId());
                break;
            case "TACKLE":
                break;
        }
    }

    private void updatePlayerAppearance(Appearance app, Long gameId, MatchEventRequestDTO dto) {
        switch (dto.getEventType()) {
            case "GOAL":
                app.setGoals(app.getGoals() + 1);
                break;
            case "ASSIST":
                app.setAssists(app.getAssists() + 1);
                break;
            case "FOUL":
                app.setFouls(app.getFouls() + 1);
                break;
            case "YELLOW_CARD":
                app.setYellowCards(app.getYellowCards() + 1);
                break;
            case "RED_CARD":
                app.setRedCard(true);
                break;
            case "PASS_FAIL":
                recalculatePlayerPassingRate(app, gameId, dto.getPlaysForId());
                break;
            case "TACKLE":
                break;
        }

        recalculatePlayerRating(app, gameId, dto.getPlaysForId());
    }

    private void recalculateTeamPassingRate(TeamStatistic stats, Game game, Long clubId) {
        List<FluxTable> tables = matchEventRepository.getStatsForGame(game.getId());
        long success = 0;
        long fail = 0;
        String targetClubId = String.valueOf(clubId);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                String cId = String.valueOf(record.getValueByKey("clubId"));
                String eventType = String.valueOf(record.getValueByKey("eventType"));
                long count = ((Number) record.getValue()).longValue();

                if (cId.equals(targetClubId)) {
                    if ("PASS_SUCCESS".equals(eventType)) success = count;
                    if ("PASS_FAIL".equals(eventType)) fail = count;
                }
            }
        }

        long total = success + fail;
        double rate = total > 0 ? ((double) success / total) * 100.0 : 0.0;

        if (game.getHomeClub().getId().equals(clubId)) {
            stats.setHomePassSuccessRate(rate);
        } else {
            stats.setAwayPassSuccessRate(rate);
        }
    }

    private void recalculatePlayerPassingRate(Appearance app, Long gameId, Long playsForId) {
        List<FluxTable> tables = matchEventRepository.getStatsForPlayer(gameId, playsForId);
        long success = 0;
        long fail = 0;

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                String eventType = String.valueOf(record.getValueByKey("eventType"));
                long count = ((Number) record.getValue()).longValue();

                if ("PASS_SUCCESS".equals(eventType)) success = count;
                if ("PASS_FAIL".equals(eventType)) fail = count;
            }
        }

        long total = success + fail;
        double accuracy = total > 0 ? ((double) success / total) * 100.0 : 0.0;
        app.setPassingAccuracy(accuracy);
    }

    private void recalculatePlayerRating(Appearance app, Long gameId, Long playsForId) {
        double baseRating = 6.0;

        double goalPoints = app.getGoals() * 1.0;
        double assistPoints = app.getAssists() * 0.6;
        double foulPenalty = app.getFouls() * 0.15;
        double yellowCardPenalty = app.getYellowCards() * 0.5;
        double redCardPenalty = app.isRedCard() ? 2.5 : 0.0;

        List<FluxTable> tables = matchEventRepository.getStatsForPlayer(gameId, playsForId);
        long successPasses = 0;
        long failPasses = 0;

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                String eventType = String.valueOf(record.getValueByKey("eventType"));
                long count = ((Number) record.getValue()).longValue();
                if ("PASS_SUCCESS".equals(eventType)) successPasses = count;
                if ("PASS_FAIL".equals(eventType)) failPasses = count;
            }
        }

        double passingPoints = (successPasses * 0.02) - (failPasses * 0.04);

        double finalRating = baseRating + goalPoints + assistPoints + passingPoints - foulPenalty - yellowCardPenalty - redCardPenalty;

        if (finalRating > 10.0) finalRating = 10.0;
        if (finalRating < 1.0) finalRating = 1.0;

        app.setRating(Math.round(finalRating * 10.0) / 10.0);
    }

    private void syncLiveEventWithGraph(TeamStatistic relStat, Appearance relApp, Game jpaGame) {
        TeamStatisticGraph statGraph = teamStatisticGraphRepository.findById(relStat.getId())
                .orElse(new TeamStatisticGraph());

        statGraph.setId(relStat.getId());
        statGraph.setHomeGoals(relStat.getHomeGoals());
        statGraph.setAwayGoals(relStat.getAwayGoals());
        statGraph.setHomeShots(relStat.getHomeShots());
        statGraph.setAwayShots(relStat.getAwayShots());
        statGraph.setHomePossession(relStat.getHomePossession());
        statGraph.setAwayPossession(relStat.getAwayPossession());
        statGraph.setHomeShotsOnTarget(relStat.getHomeShotsOnTarget());
        statGraph.setAwayShotsOnTarget(relStat.getAwayShotsOnTarget());
        statGraph.setHomeFouls(relStat.getHomeFouls());
        statGraph.setAwayFouls(relStat.getAwayFouls());
        statGraph.setHomeCorners(relStat.getHomeCorners());
        statGraph.setAwayCorners(relStat.getAwayCorners());
        statGraph.setHomeOffsides(relStat.getHomeOffsides());
        statGraph.setAwayOffsides(relStat.getAwayOffsides());
        statGraph.setHomePassSuccessRate(relStat.getHomePassSuccessRate());
        statGraph.setAwayPassSuccessRate(relStat.getAwayPassSuccessRate());

        if (statGraph.getGameGraph() == null) {
            GameGraph gameGraph = gameGraphRepository.findById(jpaGame.getId())
                    .orElseThrow(() -> new RuntimeException("GameGraph nije pronađen sa ID: " + jpaGame.getId()));
            statGraph.setGameGraph(gameGraph);
        }
        teamStatisticGraphRepository.save(statGraph);

        if (relApp != null) {
            AppearanceGraph appGraph = appearanceGraphRepository.findById(relApp.getId())
                    .orElse(new AppearanceGraph());

            appGraph.setId(relApp.getId());
            appGraph.setMatchRole(relApp.getMatchRole() != null ? relApp.getMatchRole().name() : null);
            appGraph.setMinutesPlayed(relApp.getMinutesPlayed());
            appGraph.setGoals(relApp.getGoals());
            appGraph.setAssists(relApp.getAssists());
            appGraph.setFouls(relApp.getFouls());
            appGraph.setYellowCards(relApp.getYellowCards());
            appGraph.setRedCard(relApp.isRedCard());
            appGraph.setRating(relApp.getRating());
            appGraph.setPassingAccuracy(relApp.getPassingAccuracy());

            if (appGraph.getPlayerGraph() == null) {
                PlayerGraph playerGraph = playerGraphRepository.findById(relApp.getPlaysFor().getPlayer().getId())
                        .orElseThrow(() -> new RuntimeException("PlayerGraph nije pronađen sa ID: " + relApp.getPlaysFor().getPlayer().getId()));
                appGraph.setPlayerGraph(playerGraph);
            }
            if (appGraph.getGameGraph() == null) {
                GameGraph gameGraph = gameGraphRepository.findById(jpaGame.getId())
                        .orElseThrow(() -> new RuntimeException("GameGraph nije pronađen sa ID: " + jpaGame.getId()));
                appGraph.setGameGraph(gameGraph);
            }
            appearanceGraphRepository.save(appGraph);
        }
    }
}
