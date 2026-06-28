package com.football_club.TicketSales.service.impl;

import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.TicketSales.dto.PriceChangeLogDTO;
import com.football_club.TicketSales.model.PriceChangeLog;
import com.football_club.TicketSales.repository.PriceChangeLogRepository;
import com.football_club.TicketSales.service.IPriceChangeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceChangeLogService implements IPriceChangeLogService {

    private final PriceChangeLogRepository priceChangeLogRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PriceChangeLogDTO> getAll() {
        return priceChangeLogRepository.findAllByOrderByChangedAtDesc()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceChangeLogDTO> getByGame(Long gameId) {
        return priceChangeLogRepository.findByGameIdOrderByChangedAtDesc(gameId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceChangeLogDTO> getByRule(Long ruleId) {
        return priceChangeLogRepository.findByRuleIdOrderByChangedAtDesc(ruleId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private PriceChangeLogDTO mapToDTO(PriceChangeLog log) {
        String gameLabel = gameRepository.findById(log.getGameId())
                .map(g -> g.getHomeClub().getName() + " – " + g.getAwayClub().getName())
                .orElse("Nepoznata utakmica");

        return PriceChangeLogDTO.builder()
                .id(log.getId())
                .gameId(log.getGameId())
                .gameLabel(gameLabel)
                .zoneId(log.getZoneId())
                .zoneName(log.getZoneName())
                .ruleId(log.getRuleId())
                .ruleName(log.getRuleName())
                .oldPrice(log.getOldPrice())
                .newPrice(log.getNewPrice())
                .priceDiff(log.getNewPrice().subtract(log.getOldPrice()))
                .changedAt(log.getChangedAt())
                .build();
    }
}
