package com.football_club.TicketSales.controller;

import com.football_club.TicketSales.dto.TicketTypeDTO;
import com.football_club.TicketSales.service.ITicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-types")
@RequiredArgsConstructor
public class TicketTypeController {

    private final ITicketTypeService ticketTypeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketTypeDTO> createTicketType(@RequestBody TicketTypeDTO ticketTypeDTO) {
        return new ResponseEntity<>(ticketTypeService.createTicketType(ticketTypeDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUYER')")
    public ResponseEntity<TicketTypeDTO> getTicketTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketTypeService.getTicketTypeById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BUYER')")
    public ResponseEntity<List<TicketTypeDTO>> getAllTicketTypes() {
        return ResponseEntity.ok(ticketTypeService.getAllTicketTypes());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketTypeDTO> updateTicketType(@PathVariable Long id, @RequestBody TicketTypeDTO ticketTypeDTO) {
        return ResponseEntity.ok(ticketTypeService.updateTicketType(id, ticketTypeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicketType(@PathVariable Long id) {
        ticketTypeService.deleteTicketType(id);
        return ResponseEntity.noContent().build();
    }
}
