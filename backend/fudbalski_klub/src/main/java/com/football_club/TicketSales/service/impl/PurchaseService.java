package com.football_club.TicketSales.service.impl;

import com.football_club.Auth.model.User;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.repository.GameRepository;
import com.football_club.TicketSales.dto.*;
import com.football_club.TicketSales.model.*;
import com.football_club.TicketSales.model.enums.PurchaseStatus;
import com.football_club.TicketSales.model.enums.SeatStatus;
import com.football_club.TicketSales.repository.*;
import com.football_club.TicketSales.service.IPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService implements IPurchaseService {

    private final GameRepository gameRepository;
    private final SeatRepository seatRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final PurchaseRepository purchaseRepository;

    @Override
    @Transactional
    public PurchaseResponseDTO purchase(PurchaseRequestDTO request, User buyer) {
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + request.getGameId()));

        if (!game.getMatchDate().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cannot purchase tickets for a past game.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("No seats selected.");
        }

        validateCard(request.getCardNumber(), request.getCardExpiry(), request.getCvv());

        List<Seat> seats = new ArrayList<>();
        List<TicketType> ticketTypes = new ArrayList<>();
        List<BigDecimal> prices = new ArrayList<>();

        for (PurchaseItemDTO item : request.getItems()) {
            Seat seat = seatRepository.findById(item.getSeatId())
                    .orElseThrow(() -> new RuntimeException("Seat not found with id: " + item.getSeatId()));

            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat " + seat.getRowNumber() + "/" + seat.getSeatNumber() + " is not available.");
            }

            if (ticketRepository.existsByGameIdAndSeatId(game.getId(), seat.getId())) {
                throw new RuntimeException("Seat " + seat.getRowNumber() + "/" + seat.getSeatNumber() + " is already sold for this game.");
            }

            TicketType ticketType = ticketTypeRepository.findById(item.getTicketTypeId())
                    .orElseThrow(() -> new RuntimeException("Ticket type not found with id: " + item.getTicketTypeId()));

            BigDecimal price = seat.getZone().getBasePrice()
                    .multiply(ticketType.getPriceModifier())
                    .setScale(2, RoundingMode.HALF_UP);

            seats.add(seat);
            ticketTypes.add(ticketType);
            prices.add(price);
        }

        BigDecimal totalAmount = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        Purchase purchase = new Purchase();
        purchase.setBuyer(buyer);
        purchase.setBuyerFirstName(request.getBuyerFirstName());
        purchase.setBuyerLastName(request.getBuyerLastName());
        purchase.setBuyerEmail(request.getBuyerEmail());
        purchase.setCardHolderName(request.getCardHolderName());
        purchase.setCardLastFour(request.getCardNumber().replaceAll("\\s", "").substring(
                request.getCardNumber().replaceAll("\\s", "").length() - 4));
        purchase.setCardExpiry(request.getCardExpiry());
        purchase.setTotalAmount(totalAmount);
        purchase.setTransactionDate(LocalDateTime.now());
        purchase.setStatus(PurchaseStatus.COMPLETED);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < seats.size(); i++) {
            Ticket ticket = new Ticket();
            ticket.setGame(game);
            ticket.setSeat(seats.get(i));
            ticket.setTicketType(ticketTypes.get(i));
            ticket.setPurchase(savedPurchase);
            ticket.setPrice(prices.get(i));
            ticket.setTicketCode(UUID.randomUUID().toString());
            tickets.add(ticketRepository.save(ticket));
        }

        return mapToResponse(savedPurchase, tickets);
    }

    @Override
    public List<PurchaseResponseDTO> getMyPurchases(User buyer) {
        return purchaseRepository.findByBuyerIdOrderByTransactionDateDesc(buyer.getId()).stream()
                .map(p -> mapToResponse(p, p.getTickets()))
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseResponseDTO getPurchaseById(Long id, User buyer) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found with id: " + id));

        if (!purchase.getBuyer().getId().equals(buyer.getId())) {
            throw new RuntimeException("Access denied.");
        }

        return mapToResponse(purchase, purchase.getTickets());
    }

    private void validateCard(String cardNumber, String cardExpiry, String cvv) {
        String digits = cardNumber.replaceAll("\\s", "");
        if (!digits.matches("\\d{13,19}")) {
            throw new RuntimeException("Invalid card number.");
        }
        if (!cardExpiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new RuntimeException("Invalid card expiry. Use MM/YY format.");
        }
        if (!cvv.matches("\\d{3,4}")) {
            throw new RuntimeException("Invalid CVV.");
        }
    }

    private PurchaseResponseDTO mapToResponse(Purchase purchase, List<Ticket> tickets) {
        List<TicketDTO> ticketDTOs = tickets.stream().map(t -> TicketDTO.builder()
                .id(t.getId())
                .gameId(t.getGame().getId())
                .homeClubName(t.getGame().getHomeClub().getName())
                .awayClubName(t.getGame().getAwayClub().getName())
                .matchDate(t.getGame().getMatchDate())
                .seatId(t.getSeat().getId())
                .rowNumber(t.getSeat().getRowNumber())
                .seatNumber(t.getSeat().getSeatNumber())
                .zoneName(t.getSeat().getZone().getName())
                .ticketTypeName(t.getTicketType().getName())
                .price(t.getPrice())
                .ticketCode(t.getTicketCode())
                .build()).collect(Collectors.toList());

        return PurchaseResponseDTO.builder()
                .id(purchase.getId())
                .buyerFirstName(purchase.getBuyerFirstName())
                .buyerLastName(purchase.getBuyerLastName())
                .buyerEmail(purchase.getBuyerEmail())
                .cardHolderName(purchase.getCardHolderName())
                .cardLastFour(purchase.getCardLastFour())
                .totalAmount(purchase.getTotalAmount())
                .transactionDate(purchase.getTransactionDate())
                .status(purchase.getStatus())
                .tickets(ticketDTOs)
                .build();
    }
}
