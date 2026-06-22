package com.football_club.TicketSales.service.impl;

import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.TicketSales.dto.GameSearchDTO;
import com.football_club.TicketSales.repository.TicketTypeRepository;
import com.football_club.TicketSales.repository.ZoneRepository;
import com.football_club.TicketSales.service.IGameSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameSearchService implements IGameSearchService {

    private final GameRepository gameRepository;
    private final ZoneRepository zoneRepository;
    private final TicketTypeRepository ticketTypeRepository;

    @Override
    public List<GameSearchDTO> searchGames(String opponent, LocalDateTime from, LocalDateTime to,
                                            BigDecimal minPrice, BigDecimal maxPrice) {
        BigDecimal lowestPrice = calculateGlobalLowestPrice();

        return gameRepository.findUpcomingGames(LocalDateTime.now()).stream()
                .filter(g -> opponent == null || opponent.isBlank() ||
                        g.getHomeClub().getName().toLowerCase().contains(opponent.toLowerCase()) ||
                        g.getAwayClub().getName().toLowerCase().contains(opponent.toLowerCase()))
                .filter(g -> from == null || !g.getMatchDate().isBefore(from))
                .filter(g -> to == null || !g.getMatchDate().isAfter(to))
                .filter(g -> minPrice == null || lowestPrice.compareTo(minPrice) >= 0)
                .filter(g -> maxPrice == null || lowestPrice.compareTo(maxPrice) <= 0)
                .map(g -> mapToDTO(g, lowestPrice))
                .collect(Collectors.toList());
    }

    private BigDecimal calculateGlobalLowestPrice() {
        BigDecimal minZonePrice = zoneRepository.findAll().stream()
                .map(z -> z.getBasePrice())
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal minModifier = ticketTypeRepository.findAll().stream()
                .map(t -> t.getPriceModifier())
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ONE);

        return minZonePrice.multiply(minModifier);
    }

    private GameSearchDTO mapToDTO(Game game, BigDecimal lowestPrice) {
        return GameSearchDTO.builder()
                .id(game.getId())
                .matchDate(game.getMatchDate())
                .homeClubId(game.getHomeClub().getId())
                .homeClubName(game.getHomeClub().getName())
                .awayClubId(game.getAwayClub().getId())
                .awayClubName(game.getAwayClub().getName())
                .lowestPrice(lowestPrice)
                .build();
    }
}
