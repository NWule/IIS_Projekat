package com.football_club.TicketSales.repository;

import com.football_club.TicketSales.model.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    List<PricingRule> findByActiveTrueAndGames_Id(Long gameId);
}
