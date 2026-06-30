package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.dto.TeamChartDTO;
import com.football_club.MatchTracking.dto.TeamStatisticDTO;

import java.util.List;

public interface ITeamStatisticService {

    TeamStatisticDTO saveFinalStatistic(TeamStatisticDTO teamStatisticDTO);

    TeamStatisticDTO getStatisticByGameId(Long gameId);

    void deleteStatistic(Long id);

    List<TeamChartDTO> getClubChartStatistics(Long clubId);
}