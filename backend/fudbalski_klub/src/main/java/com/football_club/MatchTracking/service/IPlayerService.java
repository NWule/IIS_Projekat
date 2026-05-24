package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.dto.PlayerDTO;

import java.util.List;

public interface IPlayerService {
    PlayerDTO createPlayer(PlayerDTO playerDTO);

    PlayerDTO getPlayerById(Long id);

    List<PlayerDTO> getAllPlayers();

    PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO);

    void deletePlayer(Long id);

    List<PlayerDTO> searchPlayers(String keyword);
}
