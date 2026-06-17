package com.football_club.Scouting.service;

import com.football_club.MatchTracking.model.enums.PlayerPosition;
import com.football_club.Scouting.dto.PlayerRecommendationDTO;

import java.util.List;
import java.util.Map;

public interface IRecommendationService {
    List<PlayerRecommendationDTO> getRecommendations(PlayerPosition position, Map<Long, Double> metricWeights);
}
