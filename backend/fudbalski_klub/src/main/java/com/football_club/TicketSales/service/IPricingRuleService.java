package com.football_club.TicketSales.service;

import com.football_club.TicketSales.dto.PricingRuleDTO;

import java.math.BigDecimal;
import java.util.List;

public interface IPricingRuleService {
    List<PricingRuleDTO> getAll();
    PricingRuleDTO getById(Long id);
    PricingRuleDTO create(PricingRuleDTO dto);
    PricingRuleDTO update(Long id, PricingRuleDTO dto);
    void delete(Long id);
    PricingRuleDTO toggleActive(Long id);
    void linkGame(Long ruleId, Long gameId);
    void unlinkGame(Long ruleId, Long gameId);
    BigDecimal applyRules(BigDecimal basePrice, Long gameId, Long zoneId);
}
