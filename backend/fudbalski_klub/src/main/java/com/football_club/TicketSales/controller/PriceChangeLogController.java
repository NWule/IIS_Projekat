package com.football_club.TicketSales.controller;

import com.football_club.TicketSales.dto.PriceChangeLogDTO;
import com.football_club.TicketSales.service.IPriceChangeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-change-logs")
@RequiredArgsConstructor
public class PriceChangeLogController {

    private final IPriceChangeLogService priceChangeLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PriceChangeLogDTO>> getAll() {
        return ResponseEntity.ok(priceChangeLogService.getAll());
    }

    @GetMapping("/game/{gameId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PriceChangeLogDTO>> getByGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(priceChangeLogService.getByGame(gameId));
    }

    @GetMapping("/rule/{ruleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PriceChangeLogDTO>> getByRule(@PathVariable Long ruleId) {
        return ResponseEntity.ok(priceChangeLogService.getByRule(ruleId));
    }
}
