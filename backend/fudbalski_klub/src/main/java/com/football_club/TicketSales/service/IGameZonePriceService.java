package com.football_club.TicketSales.service;

import com.football_club.TicketSales.dto.GameZonePriceDTO;

import java.math.BigDecimal;
import java.util.List;

public interface IGameZonePriceService {
    List<GameZonePriceDTO> getZonePricesForGame(Long gameId);
    GameZonePriceDTO setPrice(Long gameId, Long zoneId, BigDecimal price);
    void removePrice(Long gameId, Long zoneId);
    BigDecimal getEffectiveBasePrice(Long gameId, Long zoneId, BigDecimal defaultPrice);
}
