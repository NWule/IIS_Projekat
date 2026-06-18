package com.football_club.TicketSales.repository;

import com.football_club.TicketSales.model.Seat;
import com.football_club.TicketSales.model.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByZoneId(Long zoneId);
    List<Seat> findByZoneIdAndStatus(Long zoneId, SeatStatus status);
    Optional<Seat> findByZoneIdAndRowNumberAndSeatNumber(Long zoneId, int rowNumber, int seatNumber);
    long countByZoneId(Long zoneId);
}
