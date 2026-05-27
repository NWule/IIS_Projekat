package com.football_club.TicketSales.service;

import com.football_club.Auth.model.User;
import com.football_club.TicketSales.dto.TicketDTO;

import java.util.List;

public interface IMyTicketsService {
    List<TicketDTO> getMyTickets(User buyer);
    TicketDTO getTicketByCode(String ticketCode, User buyer);
}
