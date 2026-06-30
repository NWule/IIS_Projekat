package com.football_club.MatchTracking.service;

public interface IReportService {
    byte[] generateMatchReportPdf(Long gameId);
}
