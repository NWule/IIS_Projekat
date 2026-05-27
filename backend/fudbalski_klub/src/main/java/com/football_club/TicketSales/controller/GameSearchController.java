package com.football_club.TicketSales.controller;

import com.football_club.TicketSales.dto.GameSearchDTO;
import com.football_club.TicketSales.service.IGameSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/games/search")
@RequiredArgsConstructor
public class GameSearchController {

    private final IGameSearchService gameSearchService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BUYER')")
    public ResponseEntity<List<GameSearchDTO>> searchGames(
            @RequestParam(required = false) String opponent,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        return ResponseEntity.ok(gameSearchService.searchGames(opponent, from, to, minPrice, maxPrice));
    }
}
