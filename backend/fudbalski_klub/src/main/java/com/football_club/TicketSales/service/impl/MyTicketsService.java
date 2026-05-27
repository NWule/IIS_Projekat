package com.football_club.TicketSales.service.impl;

import com.football_club.Auth.model.User;
import com.football_club.TicketSales.dto.TicketDTO;
import com.football_club.TicketSales.model.Ticket;
import com.football_club.TicketSales.repository.TicketRepository;
import com.football_club.TicketSales.service.IMyTicketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyTicketsService implements IMyTicketsService {

    private final TicketRepository ticketRepository;

    @Override
    public List<TicketDTO> getMyTickets(User buyer) {
        return ticketRepository.findByPurchaseBuyerIdOrderByGameMatchDateDesc(buyer.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TicketDTO getTicketByCode(String ticketCode, User buyer) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found with code: " + ticketCode));

        if (!ticket.getPurchase().getBuyer().getId().equals(buyer.getId())) {
            throw new RuntimeException("Access denied.");
        }

        return mapToDTO(ticket);
    }

    private TicketDTO mapToDTO(Ticket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .gameId(ticket.getGame().getId())
                .homeClubName(ticket.getGame().getHomeClub().getName())
                .awayClubName(ticket.getGame().getAwayClub().getName())
                .matchDate(ticket.getGame().getMatchDate())
                .seatId(ticket.getSeat().getId())
                .rowNumber(ticket.getSeat().getRowNumber())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .zoneName(ticket.getSeat().getZone().getName())
                .ticketTypeName(ticket.getTicketType().getName())
                .price(ticket.getPrice())
                .ticketCode(ticket.getTicketCode())
                .build();
    }
}
