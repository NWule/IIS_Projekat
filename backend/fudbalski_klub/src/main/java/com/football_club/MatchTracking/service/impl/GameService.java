package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.GameDTO;
import com.football_club.MatchTracking.event.GameCreatedEvent;
import com.football_club.MatchTracking.event.GameDeletedEvent;
import com.football_club.MatchTracking.event.GameUpdatedEvent;
import com.football_club.MatchTracking.model.Club;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.TeamStatistic;
import com.football_club.MatchTracking.model.enums.GameStatus;
import com.football_club.MatchTracking.model.graph.ClubGraph;
import com.football_club.MatchTracking.model.graph.GameGraph;
import com.football_club.MatchTracking.repository.jpa.ClubRepository;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.MatchTracking.repository.graph.ClubGraphRepository;
import com.football_club.MatchTracking.repository.graph.GameGraphRepository;
import com.football_club.MatchTracking.repository.jpa.TeamStatisticRepository;
import com.football_club.MatchTracking.service.IGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService implements IGameService {

    private final GameRepository gameRepository;
    private final ClubRepository clubRepository;
    private final GameGraphRepository gameGraphRepository;
    private final ClubGraphRepository clubGraphRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TeamStatisticRepository teamStatisticRepository;

    @Override
    @Transactional
    public GameDTO createGame(GameDTO gameDTO) {
        if (gameDTO.getHomeClubId() == gameDTO.getAwayClubId()) {
            throw new RuntimeException("Home and away clubs cannot be the same!");
        }

        Club homeClub = clubRepository.findById(gameDTO.getHomeClubId())
                .orElseThrow(() -> new RuntimeException("Home club not found with id: " + gameDTO.getHomeClubId()));

        Club awayClub = clubRepository.findById(gameDTO.getAwayClubId())
                .orElseThrow(() -> new RuntimeException("Away club not found with id: " + gameDTO.getAwayClubId()));

        Game game = new Game();
        game.setMatchDate(gameDTO.getMatchDate());
        if (gameDTO.getStatus() != null) {
            game.setStatus(GameStatus.valueOf(gameDTO.getStatus()));
        } else {
            game.setStatus(GameStatus.UPCOMING);
        }
        game.setHomeClub(homeClub);
        game.setAwayClub(awayClub);

        Game savedGame = gameRepository.save(game);
        eventPublisher.publishEvent(new GameCreatedEvent(
                savedGame.getId(),
                savedGame.getStatus().name(),
                (long) savedGame.getHomeClub().getId(),
                (long) savedGame.getAwayClub().getId()
        ));

        TeamStatistic stat = new TeamStatistic();
        stat.setGame(savedGame);
        stat.setHomeGoals(0);
        stat.setAwayGoals(0);
        stat.setHomeShots(0);
        stat.setAwayShots(0);
        stat.setHomeShotsOnTarget(0);
        stat.setAwayShotsOnTarget(0);
        stat.setHomeFouls(0);
        stat.setAwayFouls(0);
        stat.setHomeCorners(0);
        stat.setAwayCorners(0);
        stat.setHomeOffsides(0);
        stat.setAwayOffsides(0);
        stat.setHomePassSuccessRate(0.0);
        stat.setAwayPassSuccessRate(0.0);

        teamStatisticRepository.save(stat);

        return mapToDTO(savedGame);
    }

    @Override
    public GameDTO getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
        return mapToDTO(game);
    }

    @Override
    public List<GameDTO> getAllGames() {
        return gameRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GameDTO updateGame(Long id, GameDTO gameDTO) {
        if (gameDTO.getHomeClubId() == gameDTO.getAwayClubId()) {
            throw new RuntimeException("Home and away clubs cannot be the same!");
        }

        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        Club homeClub = clubRepository.findById(gameDTO.getHomeClubId())
                .orElseThrow(() -> new RuntimeException("Home club not found with id: " + gameDTO.getHomeClubId()));

        Club awayClub = clubRepository.findById(gameDTO.getAwayClubId())
                .orElseThrow(() -> new RuntimeException("Away club not found with id: " + gameDTO.getAwayClubId()));

        game.setMatchDate(gameDTO.getMatchDate());
        game.setHomeClub(homeClub);
        game.setAwayClub(awayClub);

        if (gameDTO.getStatus() != null) {
            game.setStatus(GameStatus.valueOf(gameDTO.getStatus()));
        }

        Game updatedGame = gameRepository.save(game);

        eventPublisher.publishEvent(new GameUpdatedEvent(
                updatedGame.getId(),
                updatedGame.getStatus().name(),
                (long) updatedGame.getHomeClub().getId(),
                (long) updatedGame.getAwayClub().getId()
        ));

        return mapToDTO(updatedGame);
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Game not found with id: " + id);
        }
        gameRepository.deleteById(id);
        eventPublisher.publishEvent(new GameDeletedEvent(id));
    }

    @Override
    public List<GameDTO> getGamesInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return gameRepository.findGamesWithClubsInPeriod(startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getGamesByClub(int clubId) {
        return gameRepository.findByHomeClubIdOrAwayClubId(clubId, clubId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getHeadToHeadMatches(int club1Id, int club2Id) {
        return gameRepository.findHeadToHeadMatches(club1Id, club2Id).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getUpcomingGames() {
        return gameRepository.findUpcomingGames().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getLiveGames() {
        return gameRepository.findLiveGames().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameDTO> getPlayedGames() {
        return gameRepository.findPlayedGames().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    private GameDTO mapToDTO(Game game) {
        return GameDTO.builder()
                .id(game.getId())
                .matchDate(game.getMatchDate())
                .status(String.valueOf(game.getStatus()))
                .homeClubId(game.getHomeClub().getId())
                .homeClubName(game.getHomeClub().getName())
                .awayClubId(game.getAwayClub().getId())
                .awayClubName(game.getAwayClub().getName())
                .build();
    }
}