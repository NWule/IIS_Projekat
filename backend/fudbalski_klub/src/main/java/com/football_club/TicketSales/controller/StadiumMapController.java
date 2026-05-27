package com.football_club.TicketSales.controller;

import com.football_club.TicketSales.dto.PricePreviewDTO;
import com.football_club.TicketSales.dto.StadiumMapDTO;
import com.football_club.TicketSales.service.IStadiumMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stadium")
@RequiredArgsConstructor
public class StadiumMapController {

    private final IStadiumMapService stadiumMapService;

    @GetMapping("/map/{gameId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUYER')")
    public ResponseEntity<StadiumMapDTO> getStadiumMap(@PathVariable Long gameId) {
        return ResponseEntity.ok(stadiumMapService.getStadiumMap(gameId));
    }

    @GetMapping("/price-preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUYER')")
    public ResponseEntity<PricePreviewDTO> getPricePreview(
            @RequestParam Long zoneId,
            @RequestParam Long ticketTypeId) {
        return ResponseEntity.ok(stadiumMapService.getPricePreview(zoneId, ticketTypeId));
    }
}
