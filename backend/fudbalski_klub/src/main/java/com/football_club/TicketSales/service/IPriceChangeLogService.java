package com.football_club.TicketSales.service;

import com.football_club.TicketSales.dto.PriceChangeLogDTO;

import java.util.List;

public interface IPriceChangeLogService {
    List<PriceChangeLogDTO> getAll();
    List<PriceChangeLogDTO> getByGame(Long gameId);
    List<PriceChangeLogDTO> getByRule(Long ruleId);
}
