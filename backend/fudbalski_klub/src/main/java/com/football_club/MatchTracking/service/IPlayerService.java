package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.dto.PlayerDTO;
import com.football_club.MatchTracking.dto.PlayerWithReportDTO;
import com.football_club.Scouting.dto.SearchParameters;

import java.util.List;

public interface IPlayerService {
    PlayerDTO createPlayer(PlayerDTO playerDTO);

    PlayerDTO getPlayerById(Long id);

    List<PlayerDTO> getPlayersByIds(List<Long> playerIds);

    List<PlayerDTO> getAllPlayers();

    PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO);

    void deletePlayer(Long id);

    List<PlayerDTO> searchPlayers(String keyword);

    List<PlayerDTO> advancedSearch(SearchParameters searchParameters);

    List<PlayerWithReportDTO> getPlayersForComparison(List<Long> playerIds);
}
