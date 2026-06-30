package com.football_club.TicketSales.repository;

import com.football_club.TicketSales.model.PriceChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceChangeLogRepository extends JpaRepository<PriceChangeLog, Long> {
    List<PriceChangeLog> findAllByOrderByChangedAtDesc();
    List<PriceChangeLog> findByGameIdOrderByChangedAtDesc(Long gameId);
    List<PriceChangeLog> findByRuleIdOrderByChangedAtDesc(Long ruleId);
    long countByGameId(Long gameId);
}
