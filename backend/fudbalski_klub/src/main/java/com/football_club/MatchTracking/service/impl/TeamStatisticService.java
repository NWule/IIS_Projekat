package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.TeamChartDTO;
import com.football_club.MatchTracking.dto.TeamStatisticDTO;
import com.football_club.MatchTracking.event.TeamStatisticDeletedEvent;
import com.football_club.MatchTracking.event.TeamStatisticSaveEvent;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.TeamStatistic;
import com.football_club.MatchTracking.model.graph.GameGraph;
import com.football_club.MatchTracking.model.graph.TeamStatisticGraph;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.MatchTracking.repository.jpa.TeamStatisticRepository;
import com.football_club.MatchTracking.repository.graph.GameGraphRepository;
import com.football_club.MatchTracking.repository.graph.TeamStatisticGraphRepository;
import com.football_club.MatchTracking.service.ITeamStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamStatisticService implements ITeamStatisticService {

    private final TeamStatisticRepository teamStatisticRepository;
    private final GameRepository gameRepository;
    private final TeamStatisticGraphRepository teamStatisticGraphRepository;
    private final GameGraphRepository gameGraphRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(value="transactionManager")
    public TeamStatisticDTO saveFinalStatistic(TeamStatisticDTO dto) {
        Game game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + dto.getGameId()));

        TeamStatistic statistic = teamStatisticRepository.findByGameId(dto.getGameId())
                .orElse(new TeamStatistic());
        boolean exists = teamStatisticRepository.findByGameId(dto.getGameId()).isPresent();

        statistic.setGame(game);
        statistic.setHomeGoals(dto.getHomeGoals());
        statistic.setAwayGoals(dto.getAwayGoals());
        statistic.setHomeShots(dto.getHomeShots());
        statistic.setAwayShots(dto.getAwayShots());
        //statistic.setHomePossession(dto.getHomePossession());
        //statistic.setAwayPossession(dto.getAwayPossession());
        statistic.setHomeShotsOnTarget(dto.getHomeShotsOnTarget());
        statistic.setAwayShotsOnTarget(dto.getAwayShotsOnTarget());
        statistic.setHomeFouls(dto.getHomeFouls());
        statistic.setAwayFouls(dto.getAwayFouls());
        statistic.setHomeCorners(dto.getHomeCorners());
        statistic.setAwayCorners(dto.getAwayCorners());
        statistic.setHomeOffsides(dto.getHomeOffsides());
        statistic.setAwayOffsides(dto.getAwayOffsides());
        statistic.setHomePassSuccessRate(dto.getHomePassSuccessRate());
        statistic.setAwayPassSuccessRate(dto.getAwayPassSuccessRate());

        TeamStatistic savedStatistic = teamStatisticRepository.save(statistic);

        teamStatisticRepository.flush();

        eventPublisher.publishEvent(new TeamStatisticSaveEvent(
                savedStatistic.getId(), savedStatistic.getGame().getId(),
                savedStatistic.getHomeGoals(), savedStatistic.getAwayGoals(),
                savedStatistic.getHomeShots(), savedStatistic.getAwayShots(),
                savedStatistic.getHomeShotsOnTarget(), savedStatistic.getAwayShotsOnTarget(),
                savedStatistic.getHomeFouls(), savedStatistic.getAwayFouls(),
                savedStatistic.getHomeCorners(), savedStatistic.getAwayCorners(),
                savedStatistic.getHomeOffsides(), savedStatistic.getAwayOffsides(),
                savedStatistic.getHomePassSuccessRate(), savedStatistic.getAwayPassSuccessRate(),
                exists
        ));
        return mapToDTO(savedStatistic);
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public TeamStatisticDTO getStatisticByGameId(Long gameId) {
        TeamStatistic statistic = teamStatisticRepository.findByGameId(gameId)
                .orElseThrow(() -> new RuntimeException("Statistic not found for game id: " + gameId));
        return mapToDTO(statistic);
    }

    @Override
    @Transactional(value="transactionManager")
    public void deleteStatistic(Long id) {
        if (!teamStatisticRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Statistic not found with id: " + id);
        }
        teamStatisticRepository.deleteById(id);
        eventPublisher.publishEvent(new TeamStatisticDeletedEvent(id));
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public List<TeamChartDTO> getClubChartStatistics(Long clubId) {
        List<TeamStatistic> stats = teamStatisticRepository.findAllByClubIdOrderByMatchDate(clubId);

        return stats.stream().map(stat -> {
            boolean isHome = stat.getGame().getHomeClub().getId().longValue() == clubId.longValue();

            return TeamChartDTO.builder()
                    .matchDate(stat.getGame().getMatchDate())
                    .goals(isHome ? stat.getHomeGoals() : stat.getAwayGoals())
                    .passSuccessRate(isHome ? stat.getHomePassSuccessRate() : stat.getAwayPassSuccessRate())
                    .shots(isHome ? stat.getHomeShots() : stat.getAwayShots())
                    .shotsOnTarget(isHome ? stat.getHomeShotsOnTarget() : stat.getAwayShotsOnTarget())
                    .fouls(isHome ? stat.getHomeFouls() : stat.getAwayFouls())
                    .corners(isHome ? stat.getHomeCorners() : stat.getAwayCorners())
                    .offsides(isHome ? stat.getHomeOffsides() : stat.getAwayOffsides())
                    .build();
        }).toList();
    }



    private TeamStatisticDTO mapToDTO(TeamStatistic statistic) {
        return TeamStatisticDTO.builder()
                .id(statistic.getId())
                .gameId(statistic.getGame().getId())
                .homeGoals(statistic.getHomeGoals())
                .awayGoals(statistic.getAwayGoals())
                .homeShots(statistic.getHomeShots())
                .awayShots(statistic.getAwayShots())
                //.homePossession(statistic.getHomePossession())
                //.awayPossession(statistic.getAwayPossession())
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