package com.football_club.TicketSales.service;

import com.football_club.TicketSales.dto.GameAnalyticsDTO;

import java.util.List;

public interface ITicketAnalyticsService {
    GameAnalyticsDTO getGameAnalytics(Long gameId);
    List<GameAnalyticsDTO> getAllGamesAnalytics();
    byte[] generatePdfReport(Long gameId);
}
