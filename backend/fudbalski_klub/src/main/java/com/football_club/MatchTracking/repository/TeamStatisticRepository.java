package com.football_club.MatchTracking.repository;

import com.football_club.MatchTracking.model.TeamStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamStatisticRepository extends JpaRepository<TeamStatistic, Long> {
    Optional<TeamStatistic> FindByGameId(Long gameId);
}
