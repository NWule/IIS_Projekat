package com.football_club.TicketSales.controller;

import com.football_club.Auth.model.User;
import com.football_club.TicketSales.dto.PurchaseRequestDTO;
import com.football_club.TicketSales.dto.PurchaseResponseDTO;
import com.football_club.TicketSales.service.IPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final IPurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<PurchaseResponseDTO> purchase(
            @RequestBody PurchaseRequestDTO request,
            @AuthenticationPrincipal User buyer) {
        return new ResponseEntity<>(purchaseService.purchase(request, buyer), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<PurchaseResponseDTO>> getMyPurchases(
            @AuthenticationPrincipal User buyer) {
        return ResponseEntity.ok(purchaseService.getMyPurchases(buyer));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')")
    public ResponseEntity<PurchaseResponseDTO> getPurchaseById(
            @PathVariable Long id,
            @AuthenticationPrincipal User buyer) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id, buyer));
    }
}
