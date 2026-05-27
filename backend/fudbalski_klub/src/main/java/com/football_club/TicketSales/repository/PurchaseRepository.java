package com.football_club.TicketSales.repository;

import com.football_club.TicketSales.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByBuyerIdOrderByTransactionDateDesc(Long buyerId);
}
