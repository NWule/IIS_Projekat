package com.football_club.TicketSales.service;

import com.football_club.TicketSales.dto.SeatDTO;

import java.util.List;

public interface ISeatService {
    SeatDTO createSeat(SeatDTO seatDTO);
    SeatDTO getSeatById(Long id);
    List<SeatDTO> getAllSeats();
    List<SeatDTO> getSeatsByZone(Long zoneId);
    List<SeatDTO> getAvailableSeatsByZone(Long zoneId);
    SeatDTO updateSeat(Long id, SeatDTO seatDTO);
    void deleteSeat(Long id);
}
