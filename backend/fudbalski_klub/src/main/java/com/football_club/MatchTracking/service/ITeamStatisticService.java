package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.dto.TeamStatisticDTO;

public interface ITeamStatisticService {

    TeamStatisticDTO saveFinalStatistic(TeamStatisticDTO teamStatisticDTO);

    TeamStatisticDTO getStatisticByGameId(Long gameId);

    void deleteStatistic(Long id);
}