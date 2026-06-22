package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.MatchEventRequestDTO;
import com.football_club.MatchTracking.dto.TeamStatisticDTO;
import com.football_club.MatchTracking.event.LiveMatchSyncEvent;
import com.football_club.MatchTracking.model.Appearance;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.MatchEvent;
import com.football_club.MatchTracking.model.TeamStatistic;
import com.football_club.MatchTracking.model.graph.AppearanceGraph;
import com.football_club.MatchTracking.model.graph.GameGraph;
import com.football_club.MatchTracking.model.graph.PlayerGraph;
import com.football_club.MatchTracking.model.graph.TeamStatisticGraph;
import com.football_club.MatchTracking.repository.jpa.AppearanceRepository;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.MatchTracking.repository.jpa.MatchEventRepository;
import com.football_club.MatchTracking.repository.jpa.TeamStatisticRepository;
import com.football_club.MatchTracking.repository.graph.AppearanceGraphRepository;
import com.football_club.MatchTracking.repository.graph.GameGraphRepository;
import com.football_club.MatchTracking.repository.graph.PlayerGraphRepository;
import com.football_club.MatchTracking.repository.graph.TeamStatisticGraphRepository;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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


    private final ApplicationEventPublisher eventPublisher;

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

        Appearance savedAppearance = null;
        if (dto.getPlaysForId() != null) {
            Appearance appearance = appearanceRepository.findAppearancesWithPlayerInfoByGameId(gameId)
                    .stream()
                    .filter(a -> a.getPlaysFor().getId().equals(dto.getPlaysForId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Nastup igrača nije pronađen"));

            updatePlayerAppearance(appearance, gameId, dto);
            savedAppearance = appearanceRepository.save(appearance);
        }

        TeamStatisticDTO statDTO = mapToDTO(savedTeamStat);
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/team-stats", statDTO);

        Long appId = (savedAppearance != null) ? savedAppearance.getId() : null;
        eventPublisher.publishEvent(new LiveMatchSyncEvent(savedTeamStat.getId(), appId, game.getId()));
    }



    private void updateTeamStatistic(TeamStatistic stats, Game game, MatchEventRequestDTO dto) {
        boolean isHomeTeam = game.getHomeClub().getId() == dto.getClubId().intValue();

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
                    stats.setHomeShots(stats.getHomeShots() + 1);
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
            case "PASS_SUCCESS":
            case "PASS_FAIL":
                recalculatePlayerPassingRate(app, gameId, dto.getPlaysForId());
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
                if (record.getValueByKey("clubId") == null || record.getValue() == null) continue;

                String cId = String.valueOf(record.getValueByKey("clubId"));
                String eventType = String.valueOf(record.getValueByKey("eventType"));

                if (cId.equals(targetClubId)) {
                    long value = Long.parseLong(String.valueOf(record.getValue()));
                    if ("PASS_SUCCESS".equals(eventType)) success = value;
                    if ("PASS_FAIL".equals(eventType)) fail = value;
                }
            }
        }

        long total = success + fail;
        double rate = total > 0 ? ((double) success / total) * 100.0 : 0.0;

        if (game.getHomeClub().getId() == clubId.intValue()) {
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
                if (record.getValue() == null) continue;
                String eventType = String.valueOf(record.getValueByKey("eventType"));
                long value = Long.parseLong(String.valueOf(record.getValue()));
                if ("PASS_SUCCESS".equals(eventType)) success = value;
                if ("PASS_FAIL".equals(eventType)) fail = value;
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
                if (record.getValue() == null) continue;
                String eventType = String.valueOf(record.getValueByKey("eventType"));
                long value = Long.parseLong(String.valueOf(record.getValue()));
                if ("PASS_SUCCESS".equals(eventType)) successPasses = value;
                if ("PASS_FAIL".equals(eventType)) failPasses = value;
            }
        }

        double passingPoints = (successPasses * 0.02) - (failPasses * 0.04);
        double finalRating = baseRating + goalPoints + assistPoints + passingPoints - foulPenalty - yellowCardPenalty - redCardPenalty;

        if (finalRating > 10.0) finalRating = 10.0;
        if (finalRating < 1.0) finalRating = 1.0;

        app.setRating(Math.round(finalRating * 10.0) / 10.0);
    }


    private TeamStatisticDTO mapToDTO(TeamStatistic statistic) {
        return TeamStatisticDTO.builder()
                .id(statistic.getId())
                .gameId(statistic.getGame().getId())
                .homeGoals(statistic.getHomeGoals())
                .awayGoals(statistic.getAwayGoals())
                .homeShots(statistic.getHomeShots())
                .awayShots(statistic.getAwayShots())
                .homeShotsOnTarget(statistic.getHomeShotsOnTarget())
                .awayShotsOnTarget(statistic.getAwayShotsOnTarget())
                .homeFouls(statistic.getHomeFouls())
                .awayFouls(statistic.getAwayFouls())
                .homeCorners(statistic.getHomeCorners())
                .awayCorners(statistic.getAwayCorners())
                .homeOffsides(statistic.getHomeOffsides())
                .awayOffsides(statistic.getAwayOffsides())
                .homePassSuccessRate(statistic.getHomePassSuccessRate())
                .awayPassSuccessRate(statistic.getAwayPassSuccessRate())
                .build();
    }
}