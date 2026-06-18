package com.football_club.TicketSales.controller;

import com.football_club.TicketSales.dto.GameZonePriceDTO;
import com.football_club.TicketSales.service.IGameZonePriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/game-zone-prices")
@RequiredArgsConstructor
public class GameZonePriceController {

    private final IGameZonePriceService gameZonePriceService;

    @GetMapping("/{gameId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GameZonePriceDTO>> getZonePrices(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameZonePriceService.getZonePricesForGame(gameId));
    }

    @PutMapping("/{gameId}/{zoneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameZonePriceDTO> setPrice(
            @PathVariable Long gameId,
            @PathVariable Long zoneId,
            @RequestBody BigDecimal price) {
        return ResponseEntity.ok(gameZonePriceService.setPrice(gameId, zoneId, price));
    }

    @DeleteMapping("/{gameId}/{zoneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removePrice(@PathVariable Long gameId, @PathVariable Long zoneId) {
        gameZonePriceService.removePrice(gameId, zoneId);
        return ResponseEntity.noContent().build();
    }
}
