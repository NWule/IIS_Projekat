package com.football_club.Scouting.repository;

import com.football_club.Scouting.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByScoutId(Long scoutId);
    List<Report> findByPlayerId(Long playerId);
    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.valuedMetrics WHERE r.player.id = :playerId")
    List<Report> findByPlayerIdWithMetrics(@Param("playerId") Long playerId);
}
