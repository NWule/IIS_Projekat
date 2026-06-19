package com.football_club.MatchTracking.repository.jpa;

import com.football_club.MatchTracking.model.TeamStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamStatisticRepository extends JpaRepository<TeamStatistic, Long> {
    Optional<TeamStatistic> findByGameId(Long gameId);
}
