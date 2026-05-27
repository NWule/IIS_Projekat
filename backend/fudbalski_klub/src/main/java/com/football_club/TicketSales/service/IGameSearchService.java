package com.football_club.TicketSales.service;

import com.football_club.TicketSales.dto.GameSearchDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IGameSearchService {
    List<GameSearchDTO> searchGames(String opponent, LocalDateTime from, LocalDateTime to,
                                    BigDecimal minPrice, BigDecimal maxPrice);
}
