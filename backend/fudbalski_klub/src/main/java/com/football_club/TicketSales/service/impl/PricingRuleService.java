package com.football_club.TicketSales.service.impl;

import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.repository.GameRepository;
import com.football_club.TicketSales.dto.PricingRuleDTO;
import com.football_club.TicketSales.model.PricingRule;
import com.football_club.TicketSales.repository.PricingRuleRepository;
import com.football_club.TicketSales.repository.SeatRepository;
import com.football_club.TicketSales.repository.TicketRepository;
import com.football_club.TicketSales.service.IPricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingRuleService implements IPricingRuleService {

    private final PricingRuleRepository pricingRuleRepository;
    private final GameRepository gameRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PricingRuleDTO> getAll() {
        return pricingRuleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PricingRuleDTO getById(Long id) {
        return mapToDTO(pricingRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing rule not found with id: " + id)));
    }

    @Override
    @Transactional
    public PricingRuleDTO create(PricingRuleDTO dto) {
        PricingRule rule = new PricingRule();
        mapFromDTO(rule, dto);
        return mapToDTO(pricingRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public PricingRuleDTO update(Long id, PricingRuleDTO dto) {
        PricingRule rule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing rule not found with id: " + id));
        mapFromDTO(rule, dto);
        return mapToDTO(pricingRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        pricingRuleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PricingRuleDTO toggleActive(Long id) {
        PricingRule rule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing rule not found with id: " + id));
        rule.setActive(!rule.isActive());
        return mapToDTO(pricingRuleRepository.save(rule));
    }

    @Override
    @Transactional
    public void linkGame(Long ruleId, Long gameId) {
        PricingRule rule = pricingRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Pricing rule not found with id: " + ruleId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));
        boolean alreadyLinked = rule.getGames().stream().anyMatch(g -> g.getId().equals(gameId));
        if (!alreadyLinked) {
            rule.getGames().add(game);
            pricingRuleRepository.save(rule);
        }
    }

    @Override
    @Transactional
    public void unlinkGame(Long ruleId, Long gameId) {
        PricingRule rule = pricingRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Pricing rule not found with id: " + ruleId));
        rule.getGames().removeIf(g -> g.getId().equals(gameId));
        pricingRuleRepository.save(rule);
    }

    @Override
    public BigDecimal applyRules(BigDecimal basePrice, Long gameId, Long zoneId) {
        List<PricingRule> rules = pricingRuleRepository.findByActiveTrueAndGames_Id(gameId);
        BigDecimal price = basePrice;

        for (PricingRule rule : rules) {
            if (checkOccupancy(rule, zoneId, gameId) && checkTime(rule, gameId)) {
                if (rule.getChangePercent() != null) {
                    BigDecimal multiplier = BigDecimal.ONE.add(
                            rule.getChangePercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                    price = price.multiply(multiplier);
                }
                if (rule.getMinPrice() != null && price.compareTo(rule.getMinPrice()) < 0) {
                    price = rule.getMinPrice();
                }
                if (rule.getMaxPrice() != null && price.compareTo(rule.getMaxPrice()) > 0) {
                    price = rule.getMaxPrice();
                }
            }
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean checkOccupancy(PricingRule rule, Long zoneId, Long gameId) {
        if (rule.getOccupancyThreshold() == null) return true;
        long total = seatRepository.countByZoneId(zoneId);
        if (total == 0) return false;
        long sold = ticketRepository.countByGameIdAndSeat_ZoneId(gameId, zoneId);
        BigDecimal occupancy = BigDecimal.valueOf(sold)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
        return rule.isOccupancyConditionAtLeast()
                ? occupancy.compareTo(rule.getOccupancyThreshold()) >= 0
                : occupancy.compareTo(rule.getOccupancyThreshold()) <= 0;
    }

    private boolean checkTime(PricingRule rule, Long gameId) {
        if (rule.getHoursBeforeMatch() == null) return true;
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return false;
        long hoursUntil = ChronoUnit.HOURS.between(LocalDateTime.now(), game.getMatchDate());
        return rule.isHoursConditionAtLeast()
                ? hoursUntil >= rule.getHoursBeforeMatch()
                : hoursUntil <= rule.getHoursBeforeMatch();
    }

    private void mapFromDTO(PricingRule rule, PricingRuleDTO dto) {
        rule.setName(dto.getName());
        rule.setOccupancyThreshold(dto.getOccupancyThreshold());
        rule.setOccupancyConditionAtLeast(dto.isOccupancyConditionAtLeast());
        rule.setHoursBeforeMatch(dto.getHoursBeforeMatch());
        rule.setHoursConditionAtLeast(dto.isHoursConditionAtLeast());
        rule.setChangePercent(dto.getChangePercent());
        rule.setMinPrice(dto.getMinPrice());
        rule.setMaxPrice(dto.getMaxPrice());
        rule.setActive(dto.isActive());
    }

    private PricingRuleDTO mapToDTO(PricingRule rule) {
        return PricingRuleDTO.builder()
                .id(rule.getId())
                .name(rule.getName())
                .occupancyThreshold(rule.getOccupancyThreshold())
                .occupancyConditionAtLeast(rule.isOccupancyConditionAtLeast())
                .hoursBeforeMatch(rule.getHoursBeforeMatch())
                .hoursConditionAtLeast(rule.isHoursConditionAtLeast())
                .changePercent(rule.getChangePercent())
                .minPrice(rule.getMinPrice())
                .maxPrice(rule.getMaxPrice())
                .active(rule.isActive())
                .gameIds(rule.getGames().stream()
                        .map(g -> g.getId())
                        .collect(Collectors.toList()))
                .build();
    }
}
