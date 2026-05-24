package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.GameDTO;
import com.football_club.MatchTracking.model.Club;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.repository.ClubRepository;
import com.football_club.MatchTracking.repository.GameRepository;
import com.football_club.MatchTracking.service.IGameService;
import lombok.RequiredArgsConstructor;
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
        game.setHomeClub(homeClub);
        game.setAwayClub(awayClub);

        Game savedGame = gameRepository.save(game);
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

        Game updatedGame = gameRepository.save(game);
        return mapToDTO(updatedGame);
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Game not found with id: " + id);
        }
        gameRepository.deleteById(id);
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

    private GameDTO mapToDTO(Game game) {
        return GameDTO.builder()
                .id(game.getId())
                .matchDate(game.getMatchDate())
                .homeClubId(game.getHomeClub().getId())
                .homeClubName(game.getHomeClub().getName())
                .awayClubId(game.getAwayClub().getId())
                .awayClubName(game.getAwayClub().getName())
                .build();
    }
}