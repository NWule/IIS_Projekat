package com.football_club.TicketSales.service.impl;

import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.TicketSales.dto.*;
import com.football_club.TicketSales.model.TicketType;
import com.football_club.TicketSales.model.Zone;
import com.football_club.TicketSales.model.enums.SeatStatus;
import com.football_club.TicketSales.repository.SeatRepository;
import com.football_club.TicketSales.repository.TicketRepository;
import com.football_club.TicketSales.repository.TicketTypeRepository;
import com.football_club.TicketSales.repository.ZoneRepository;
import com.football_club.TicketSales.service.IStadiumMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StadiumMapService implements IStadiumMapService {

    private final GameRepository gameRepository;
    private final ZoneRepository zoneRepository;
    private final SeatRepository seatRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;

    @Override
    public StadiumMapDTO getStadiumMap(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        // Seat IDs already sold/reserved for this game — populated once Ticket entity exists
        Set<Long> unavailableSeatIds = resolveUnavailableSeatIds(gameId);

        List<ZoneMapDTO> zoneMaps = zoneRepository.findAll().stream()
                .map(zone -> buildZoneMap(zone, unavailableSeatIds))
                .collect(Collectors.toList());

        return StadiumMapDTO.builder()
                .gameId(gameId)
                .homeClubName(game.getHomeClub().getName())
                .awayClubName(game.getAwayClub().getName())
                .zones(zoneMaps)
                .build();
    }

    @Override
    public PricePreviewDTO getPricePreview(Long zoneId, Long ticketTypeId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + zoneId));
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new RuntimeException("Ticket type not found with id: " + ticketTypeId));

        BigDecimal finalPrice = zone.getBasePrice()
                .multiply(ticketType.getPriceModifier())
                .setScale(2, RoundingMode.HALF_UP);

        return PricePreviewDTO.builder()
                .zoneId(zone.getId())
                .zoneName(zone.getName())
                .ticketTypeId(ticketType.getId())
                .ticketTypeName(ticketType.getName())
                .basePrice(zone.getBasePrice())
                .priceModifier(ticketType.getPriceModifier())
                .finalPrice(finalPrice)
                .build();
    }

    private ZoneMapDTO buildZoneMap(Zone zone, Set<Long> unavailableSeatIds) {
        List<SeatMapDTO> seatDTOs = seatRepository.findByZoneId(zone.getId()).stream()
                .map(seat -> SeatMapDTO.builder()
                        .id(seat.getId())
                        .rowNumber(seat.getRowNumber())
                        .seatNumber(seat.getSeatNumber())
                        .status(unavailableSeatIds.contains(seat.getId())
                                ? SeatStatus.SOLD
                                : seat.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ZoneMapDTO.builder()
                .id(zone.getId())
                .name(zone.getName())
                .color(zone.getColor())
                .basePrice(zone.getBasePrice())
                .numberOfRows(zone.getNumberOfRows())
                .seatsPerRow(zone.getSeatsPerRow())
                .seats(seatDTOs)
                .build();
    }

    protected Set<Long> resolveUnavailableSeatIds(Long gameId) {
        return ticketRepository.findSeatIdsByGameId(gameId);
    }
}
