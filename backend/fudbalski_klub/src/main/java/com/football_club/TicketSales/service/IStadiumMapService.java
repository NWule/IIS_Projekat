package com.football_club.TicketSales.service;

import com.football_club.TicketSales.dto.PricePreviewDTO;
import com.football_club.TicketSales.dto.StadiumMapDTO;

public interface IStadiumMapService {
    StadiumMapDTO getStadiumMap(Long gameId);
    PricePreviewDTO getPricePreview(Long zoneId, Long ticketTypeId);
}
