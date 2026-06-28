package com.football_club.TicketSales.service.impl;

import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.TicketSales.dto.GameAnalyticsDTO;
import com.football_club.TicketSales.dto.TicketTypeAnalyticsDTO;
import com.football_club.TicketSales.dto.ZoneAnalyticsDTO;
import com.football_club.TicketSales.model.Zone;
import com.football_club.TicketSales.repository.PriceChangeLogRepository;
import com.football_club.TicketSales.repository.SeatRepository;
import com.football_club.TicketSales.repository.TicketRepository;
import com.football_club.TicketSales.repository.ZoneRepository;
import com.football_club.TicketSales.service.ITicketAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketAnalyticsService implements ITicketAnalyticsService {

    private final GameRepository gameRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final ZoneRepository zoneRepository;
    private final PriceChangeLogRepository priceChangeLogRepository;

    @Override
    @Transactional(readOnly = true)
    public GameAnalyticsDTO getGameAnalytics(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found: " + gameId));

        String gameLabel = game.getHomeClub().getName() + " – " + game.getAwayClub().getName();
        long totalTicketsSold = ticketRepository.countByGameId(gameId);
        BigDecimal totalRevenue = ticketRepository.sumPriceByGameId(gameId);
        long priceChangeCount = priceChangeLogRepository.countByGameId(gameId);

        List<Zone> zones = zoneRepository.findAll();
        long totalSeatsAll = zones.stream().mapToLong(z -> seatRepository.countByZoneId(z.getId())).sum();
        BigDecimal overallOccupancy = totalSeatsAll == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(totalTicketsSold)
                        .divide(BigDecimal.valueOf(totalSeatsAll), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);

        List<ZoneAnalyticsDTO> zoneAnalytics = zones.stream().map(zone -> {
            long total = seatRepository.countByZoneId(zone.getId());
            long sold = ticketRepository.countByGameIdAndSeat_ZoneId(gameId, zone.getId());
            BigDecimal revenue = ticketRepository.sumPriceByGameIdAndZoneId(gameId, zone.getId());
            BigDecimal occupancy = total == 0 ? BigDecimal.ZERO :
                    BigDecimal.valueOf(sold)
                            .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
            return ZoneAnalyticsDTO.builder()
                    .zoneId(zone.getId())
                    .zoneName(zone.getName())
                    .zoneColor(zone.getColor())
                    .totalSeats(total)
                    .ticketsSold(sold)
                    .occupancyPercent(occupancy)
                    .revenue(revenue)
                    .build();
        }).collect(Collectors.toList());

        List<TicketTypeAnalyticsDTO> ticketTypeAnalytics = ticketRepository.countAndSumByTicketType(gameId)
                .stream().map(row -> TicketTypeAnalyticsDTO.builder()
                        .typeName((String) row[0])
                        .ticketsSold((Long) row[1])
                        .revenue((BigDecimal) row[2])
                        .build())
                .collect(Collectors.toList());

        return GameAnalyticsDTO.builder()
                .gameId(gameId)
                .gameLabel(gameLabel)
                .totalTicketsSold(totalTicketsSold)
                .totalRevenue(totalRevenue)
                .overallOccupancyPercent(overallOccupancy)
                .priceChangeCount(priceChangeCount)
                .zoneAnalytics(zoneAnalytics)
                .ticketTypeAnalytics(ticketTypeAnalytics)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameAnalyticsDTO> getAllGamesAnalytics() {
        return gameRepository.findAll().stream()
                .map(game -> getGameAnalytics(game.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String generateCsvReport(Long gameId) {
        GameAnalyticsDTO analytics = getGameAnalytics(gameId);

        StringBuilder csv = new StringBuilder();
        csv.append("Izvestaj za utakmicu: ").append(analytics.getGameLabel()).append("\n\n");

        csv.append("UKUPNO\n");
        csv.append("Prodatih karata,Prihod (RSD),Popunjenost (%),Promena cena\n");
        csv.append(analytics.getTotalTicketsSold()).append(",")
                .append(analytics.getTotalRevenue()).append(",")
                .append(analytics.getOverallOccupancyPercent()).append(",")
                .append(analytics.getPriceChangeCount()).append("\n\n");

        csv.append("PO ZONAMA\n");
        csv.append("Zona,Ukupno sedista,Prodato,Popunjenost (%),Prihod (RSD)\n");
        for (ZoneAnalyticsDTO z : analytics.getZoneAnalytics()) {
            csv.append(z.getZoneName()).append(",")
                    .append(z.getTotalSeats()).append(",")
                    .append(z.getTicketsSold()).append(",")
                    .append(z.getOccupancyPercent()).append(",")
                    .append(z.getRevenue()).append("\n");
        }

        csv.append("\nPO TIPU KARTE\n");
        csv.append("Tip karte,Prodato,Prihod (RSD)\n");
        for (TicketTypeAnalyticsDTO t : analytics.getTicketTypeAnalytics()) {
            csv.append(t.getTypeName()).append(",")
                    .append(t.getTicketsSold()).append(",")
                    .append(t.getRevenue()).append("\n");
        }

        return csv.toString();
    }
}
