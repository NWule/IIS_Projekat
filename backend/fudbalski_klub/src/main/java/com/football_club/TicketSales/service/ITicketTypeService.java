package com.football_club.TicketSales.service;

import com.football_club.TicketSales.dto.TicketTypeDTO;

import java.util.List;

public interface ITicketTypeService {
    TicketTypeDTO createTicketType(TicketTypeDTO ticketTypeDTO);
    TicketTypeDTO getTicketTypeById(Long id);
    List<TicketTypeDTO> getAllTicketTypes();
    TicketTypeDTO updateTicketType(Long id, TicketTypeDTO ticketTypeDTO);
    void deleteTicketType(Long id);
}
