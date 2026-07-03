package com.football_club.Scouting.service;

public interface IPlayerReportService {
    byte[] generatePlayerPdfReport(Long playerId) throws Exception;
}
