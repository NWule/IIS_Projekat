package com.football_club.TicketSales.repository;

import com.football_club.TicketSales.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByGameIdAndSeatId(Long gameId, Long seatId);
    List<Ticket> findByPurchaseId(Long purchaseId);
    List<Ticket> findByPurchaseBuyerIdOrderByGameMatchDateDesc(Long buyerId);
    java.util.Optional<Ticket> findByTicketCode(String ticketCode);

    @Query("SELECT t.seat.id FROM Ticket t WHERE t.game.id = :gameId")
    Set<Long> findSeatIdsByGameId(@Param("gameId") Long gameId);
}
