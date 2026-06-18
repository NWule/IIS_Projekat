package com.football_club.TicketSales.scheduler;

import com.football_club.TicketSales.model.PricingRule;
import com.football_club.TicketSales.model.Zone;
import com.football_club.TicketSales.repository.GameZonePriceRepository;
import com.football_club.TicketSales.repository.PricingRuleRepository;
import com.football_club.TicketSales.repository.SeatRepository;
import com.football_club.TicketSales.repository.TicketRepository;
import com.football_club.TicketSales.repository.ZoneRepository;
import com.football_club.MatchTracking.model.Game;
import com.football_club.TicketSales.model.GameZonePrice;
import com.football_club.TicketSales.model.PriceChangeLog;
import com.football_club.TicketSales.repository.PriceChangeLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PricingScheduler {

    private final PricingRuleRepository pricingRuleRepository;
    private final ZoneRepository zoneRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final GameZonePriceRepository gameZonePriceRepository;
    private final PriceChangeLogRepository priceChangeLogRepository;

    @Scheduled(fixedRateString = "${pricing.scheduler.interval-ms:900000}")
    @Transactional
    public void updatePrices() {
        log.info("Pricing scheduler started.");

        List<PricingRule> activeRules = pricingRuleRepository.findAll().stream()
                .filter(PricingRule::isActive)
                .toList();

        List<Zone> zones = zoneRepository.findAll();

        for (PricingRule rule : activeRules) {
            for (Game game : rule.getGames()) {
                if (!game.getMatchDate().isAfter(LocalDateTime.now())) continue;

                for (Zone zone : zones) {
                    if (!checkOccupancy(rule, zone, game.getId()) || !checkTime(rule, game)) continue;

                    BigDecimal newPrice = calculatePrice(zone.getBasePrice(), rule);

                    GameZonePrice entry = gameZonePriceRepository
                            .findByGameIdAndZoneId(game.getId(), zone.getId())
                            .orElse(new GameZonePrice(null, game.getId(), zone.getId(), zone.getBasePrice()));

                    BigDecimal oldPrice = entry.getBasePrice();

                    if (oldPrice.compareTo(newPrice) != 0) {
                        entry.setBasePrice(newPrice);
                        gameZonePriceRepository.save(entry);

                        priceChangeLogRepository.save(new PriceChangeLog(
                                null, game.getId(), zone.getId(), zone.getName(),
                                rule.getId(), rule.getName(), oldPrice, newPrice, LocalDateTime.now()
                        ));

                        log.info("Price changed: game={} zone={} {} -> {}", game.getId(), zone.getName(), oldPrice, newPrice);
                    }
                }
            }
        }

        log.info("Pricing scheduler finished.");
    }

    private boolean checkOccupancy(PricingRule rule, Zone zone, Long gameId) {
        if (rule.getOccupancyThreshold() == null) return true;
        long total = seatRepository.countByZoneId(zone.getId());
        if (total == 0) return false;
        long sold = ticketRepository.countByGameIdAndSeat_ZoneId(gameId, zone.getId());
        BigDecimal occupancy = BigDecimal.valueOf(sold)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
        return rule.isOccupancyConditionAtLeast()
                ? occupancy.compareTo(rule.getOccupancyThreshold()) >= 0
                : occupancy.compareTo(rule.getOccupancyThreshold()) <= 0;
    }

    private boolean checkTime(PricingRule rule, Game game) {
        if (rule.getHoursBeforeMatch() == null) return true;
        long hoursUntil = ChronoUnit.HOURS.between(LocalDateTime.now(), game.getMatchDate());
        return rule.isHoursConditionAtLeast()
                ? hoursUntil >= rule.getHoursBeforeMatch()
                : hoursUntil <= rule.getHoursBeforeMatch();
    }

    private BigDecimal calculatePrice(BigDecimal basePrice, PricingRule rule) {
        BigDecimal price = basePrice;
        if (rule.getChangePercent() != null) {
            BigDecimal multiplier = BigDecimal.ONE.add(
                    rule.getChangePercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            price = price.multiply(multiplier);
        }
        if (rule.getMinPrice() != null && price.compareTo(rule.getMinPrice()) < 0) price = rule.getMinPrice();
        if (rule.getMaxPrice() != null && price.compareTo(rule.getMaxPrice()) > 0) price = rule.getMaxPrice();
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
