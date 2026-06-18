package com.football_club.TicketSales.service.impl;

import com.football_club.TicketSales.dto.GameZonePriceDTO;
import com.football_club.TicketSales.model.GameZonePrice;
import com.football_club.TicketSales.model.Zone;
import com.football_club.TicketSales.repository.GameZonePriceRepository;
import com.football_club.TicketSales.repository.ZoneRepository;
import com.football_club.TicketSales.service.IGameZonePriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameZonePriceService implements IGameZonePriceService {

    private final GameZonePriceRepository gameZonePriceRepository;
    private final ZoneRepository zoneRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GameZonePriceDTO> getZonePricesForGame(Long gameId) {
        Map<Long, BigDecimal> customPrices = gameZonePriceRepository.findByGameId(gameId)
                .stream()
                .collect(Collectors.toMap(GameZonePrice::getZoneId, GameZonePrice::getBasePrice));

        return zoneRepository.findAll().stream()
                .map(zone -> {
                    BigDecimal custom = customPrices.get(zone.getId());
                    return GameZonePriceDTO.builder()
                            .gameId(gameId)
                            .zoneId(zone.getId())
                            .zoneName(zone.getName())
                            .zoneColor(zone.getColor())
                            .defaultPrice(zone.getBasePrice())
                            .customPrice(custom)
                            .effectivePrice(custom != null ? custom : zone.getBasePrice())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GameZonePriceDTO setPrice(Long gameId, Long zoneId, BigDecimal price) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found: " + zoneId));

        GameZonePrice entry = gameZonePriceRepository.findByGameIdAndZoneId(gameId, zoneId)
                .orElse(new GameZonePrice(null, gameId, zoneId, price));
        entry.setBasePrice(price);
        gameZonePriceRepository.save(entry);

        return GameZonePriceDTO.builder()
                .gameId(gameId)
                .zoneId(zoneId)
                .zoneName(zone.getName())
                .zoneColor(zone.getColor())
                .defaultPrice(zone.getBasePrice())
                .customPrice(price)
                .effectivePrice(price)
                .build();
    }

    @Override
    @Transactional
    public void removePrice(Long gameId, Long zoneId) {
        gameZonePriceRepository.deleteByGameIdAndZoneId(gameId, zoneId);
    }

    @Override
    public BigDecimal getEffectiveBasePrice(Long gameId, Long zoneId, BigDecimal defaultPrice) {
        return gameZonePriceRepository.findByGameIdAndZoneId(gameId, zoneId)
                .map(GameZonePrice::getBasePrice)
                .orElse(defaultPrice);
    }
}
