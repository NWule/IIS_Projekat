package com.football_club.Scouting.service;

import com.football_club.Scouting.dto.GameMetricDTO;
import com.football_club.Scouting.dto.GameMetricSaveDTO;

import java.util.List;

public interface IGameMetricService {
    GameMetricDTO createGameMetric(GameMetricSaveDTO dto);
    List<GameMetricDTO> createGameMetrics(List<GameMetricSaveDTO> dtos);
    GameMetricDTO getGameMetricById(Long id);
    List<GameMetricDTO> getAllGameMetrics();
    GameMetricDTO updateGameMetric(Long id, GameMetricSaveDTO dto);
    void deleteGameMetric(Long id);
    List<GameMetricDTO> getMetricsByGame(Long gameId);
    List<GameMetricDTO> getMetricsByPlayer(Long playerId);
    List<GameMetricDTO> getMetricsByGameAndPlayer(Long gameId, Long playerId);
    List<GameMetricDTO> getLastFiveGamesMetrics(Long playerId);
}
