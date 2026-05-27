package com.football_club.TicketSales.controller;

import com.football_club.Auth.model.User;
import com.football_club.TicketSales.dto.TicketDTO;
import com.football_club.TicketSales.service.IMyTicketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class MyTicketsController {

    private final IMyTicketsService myTicketsService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<TicketDTO>> getMyTickets(@AuthenticationPrincipal User buyer) {
        return ResponseEntity.ok(myTicketsService.getMyTickets(buyer));
    }

    @GetMapping("/code/{ticketCode}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<TicketDTO> getTicketByCode(
            @PathVariable String ticketCode,
            @AuthenticationPrincipal User buyer) {
        return ResponseEntity.ok(myTicketsService.getTicketByCode(ticketCode, buyer));
    }
}
