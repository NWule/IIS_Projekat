package com.football_club.TicketSales.repository;

import com.football_club.TicketSales.model.GameZonePrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameZonePriceRepository extends JpaRepository<GameZonePrice, Long> {
    Optional<GameZonePrice> findByGameIdAndZoneId(Long gameId, Long zoneId);
    List<GameZonePrice> findByGameId(Long gameId);
    void deleteByGameIdAndZoneId(Long gameId, Long zoneId);
}
