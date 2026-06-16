package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.TeamStatisticDTO;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.TeamStatistic;
import com.football_club.MatchTracking.model.graph.GameGraph;
import com.football_club.MatchTracking.model.graph.TeamStatisticGraph;
import com.football_club.MatchTracking.repository.GameRepository;
import com.football_club.MatchTracking.repository.TeamStatisticRepository;
import com.football_club.MatchTracking.repository.graph.AppearanceGraphRepository;
import com.football_club.MatchTracking.repository.graph.GameGraphRepository;
import com.football_club.MatchTracking.repository.graph.PlayerGraphRepository;
import com.football_club.MatchTracking.repository.graph.TeamStatisticGraphRepository;
import com.football_club.MatchTracking.service.ITeamStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamStatisticService implements ITeamStatisticService {

    private final TeamStatisticRepository teamStatisticRepository;
    private final GameRepository gameRepository;
    private final TeamStatisticGraphRepository teamStatisticGraphRepository;
    private final GameGraphRepository gameGraphRepository;

    @Override
    @Transactional
    public TeamStatisticDTO saveFinalStatistic(TeamStatisticDTO dto) {
        Game game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + dto.getGameId()));

        TeamStatistic statistic = teamStatisticRepository.findByGameId(dto.getGameId())
                .orElse(new TeamStatistic());

        statistic.setGame(game);
        statistic.setHomeGoals(dto.getHomeGoals());
        statistic.setAwayGoals(dto.getAwayGoals());
        statistic.setHomeShots(dto.getHomeShots());
        statistic.setAwayShots(dto.getAwayShots());
        statistic.setHomePossession(dto.getHomePossession());
        statistic.setAwayPossession(dto.getAwayPossession());
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

        TeamStatisticGraph statGraph = teamStatisticGraphRepository.findById(savedStatistic.getId())
                .orElse(new TeamStatisticGraph());

        statGraph.setId(savedStatistic.getId());
        mapFieldsToGraph(savedStatistic, statGraph);

        if (statGraph.getGameGraph() == null) {
            GameGraph gameGraph = gameGraphRepository.findById(savedStatistic.getGame().getId())
                    .orElseThrow(() -> new RuntimeException("GameGraph node not found with id: " + savedStatistic.getGame().getId()));
            statGraph.setGameGraph(gameGraph);
        }

        teamStatisticGraphRepository.save(statGraph);
        return mapToDTO(savedStatistic);
    }

    @Override
    public TeamStatisticDTO getStatisticByGameId(Long gameId) {
        TeamStatistic statistic = teamStatisticRepository.findByGameId(gameId)
                .orElseThrow(() -> new RuntimeException("Statistic not found for game id: " + gameId));
        return mapToDTO(statistic);
    }

    @Override
    @Transactional
    public void deleteStatistic(Long id) {
        if (!teamStatisticRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Statistic not found with id: " + id);
        }
        teamStatisticRepository.deleteById(id);
        teamStatisticGraphRepository.deleteById(id);
    }

    private void mapFieldsToGraph(TeamStatistic source, TeamStatisticGraph target) {
        target.setHomeGoals(source.getHomeGoals());
        target.setAwayGoals(source.getAwayGoals());
        target.setHomeShots(source.getHomeShots());
        target.setAwayShots(source.getAwayShots());
        target.setHomePossession(source.getHomePossession());
        target.setAwayPossession(source.getAwayPossession());
        target.setHomeShotsOnTarget(source.getHomeShotsOnTarget());
        target.setAwayShotsOnTarget(source.getAwayShotsOnTarget());
        target.setHomeFouls(source.getHomeFouls());
        target.setAwayFouls(source.getAwayFouls());
        target.setHomeCorners(source.getHomeCorners());
        target.setAwayCorners(source.getAwayCorners());
        target.setHomeOffsides(source.getHomeOffsides());
        target.setAwayOffsides(source.getAwayOffsides());
        target.setHomePassSuccessRate(source.getHomePassSuccessRate());
        target.setAwayPassSuccessRate(source.getAwayPassSuccessRate());
    }

    private TeamStatisticDTO mapToDTO(TeamStatistic statistic) {
        return TeamStatisticDTO.builder()
                .id(statistic.getId())
                .gameId(statistic.getGame().getId())
                .homeGoals(statistic.getHomeGoals())
                .awayGoals(statistic.getAwayGoals())
                .homeShots(statistic.getHomeShots())
                .awayShots(statistic.getAwayShots())
                .homePossession(statistic.getHomePossession())
                .awayPossession(statistic.getAwayPossession())
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