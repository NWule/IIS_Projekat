package com.football_club.TicketSales.controller;

import com.football_club.TicketSales.dto.PricingRuleDTO;
import com.football_club.TicketSales.service.IPricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing-rules")
@RequiredArgsConstructor
public class PricingRuleController {

    private final IPricingRuleService pricingRuleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PricingRuleDTO>> getAll() {
        return ResponseEntity.ok(pricingRuleService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingRuleDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pricingRuleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingRuleDTO> create(@RequestBody PricingRuleDTO dto) {
        return ResponseEntity.ok(pricingRuleService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingRuleDTO> update(@PathVariable Long id, @RequestBody PricingRuleDTO dto) {
        return ResponseEntity.ok(pricingRuleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pricingRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingRuleDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(pricingRuleService.toggleActive(id));
    }

    @PostMapping("/{ruleId}/games/{gameId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> linkGame(@PathVariable Long ruleId, @PathVariable Long gameId) {
        pricingRuleService.linkGame(ruleId, gameId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ruleId}/games/{gameId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unlinkGame(@PathVariable Long ruleId, @PathVariable Long gameId) {
        pricingRuleService.unlinkGame(ruleId, gameId);
        return ResponseEntity.ok().build();
    }
}
