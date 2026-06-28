package com.football_club.TicketSales.controller;

import com.football_club.TicketSales.dto.GameAnalyticsDTO;
import com.football_club.TicketSales.service.ITicketAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/ticket-analytics")
@RequiredArgsConstructor
public class TicketAnalyticsController {

    private final ITicketAnalyticsService ticketAnalyticsService;

    @GetMapping("/game/{gameId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameAnalyticsDTO> getGameAnalytics(@PathVariable Long gameId) {
        return ResponseEntity.ok(ticketAnalyticsService.getGameAnalytics(gameId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GameAnalyticsDTO>> getAllGamesAnalytics() {
        return ResponseEntity.ok(ticketAnalyticsService.getAllGamesAnalytics());
    }

    @GetMapping("/game/{gameId}/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadCsvReport(@PathVariable Long gameId) {
        String csv = ticketAnalyticsService.generateCsvReport(gameId);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"izvestaj-utakmica-" + gameId + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
