package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.dto.GameDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IGameService {

    GameDTO createGame(GameDTO gameDTO);

    GameDTO getGameById(Long id);

    List<GameDTO> getAllGames();

    GameDTO updateGame(Long id, GameDTO gameDTO);

    void deleteGame(Long id);

    List<GameDTO> getGamesInPeriod(LocalDateTime startDate, LocalDateTime endDate);

    List<GameDTO> getGamesByClub(int clubId);

    List<GameDTO> getHeadToHeadMatches(int club1Id, int club2Id);

    List<GameDTO> getUpcomingGames();

    List<GameDTO> getLiveGames();

    List<GameDTO> getPlayedGames();
}