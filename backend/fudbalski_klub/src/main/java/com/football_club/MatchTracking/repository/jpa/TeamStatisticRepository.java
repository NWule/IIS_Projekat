package com.football_club.MatchTracking.repository.jpa;

import com.football_club.MatchTracking.model.TeamStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamStatisticRepository extends JpaRepository<TeamStatistic, Long> {
    Optional<TeamStatistic> findByGameId(Long gameId);

    @Query("SELECT ts FROM TeamStatistic ts JOIN ts.game g WHERE g.homeClub.id = :clubId OR g.awayClub.id = :clubId ORDER BY g.matchDate ASC")
    List<TeamStatistic> findAllByClubIdOrderByMatchDate(@Param("clubId") Long clubId);
}
