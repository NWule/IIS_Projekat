package com.football_club.MatchTracking.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.football_club.MatchTracking.dto.PlayerWithReportDTO;
import com.football_club.MatchTracking.event.PlayerCreatedEvent;
import com.football_club.MatchTracking.event.PlayerDeletedEvent;
import com.football_club.MatchTracking.event.PlayerUpdatedEvent;
import com.football_club.MatchTracking.service.IFileStorageService;
import com.football_club.Scouting.dto.ReportDTO;
import com.football_club.Scouting.dto.ValuedMetricDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.football_club.MatchTracking.dto.PlayerDTO;
import com.football_club.MatchTracking.model.Player;
import com.football_club.MatchTracking.model.graph.PlayerGraph;
import com.football_club.MatchTracking.repository.graph.PlayerGraphRepository;
import com.football_club.MatchTracking.repository.jpa.PlayerRepository;
import com.football_club.MatchTracking.service.IPlayerService;
import com.football_club.Scouting.dto.SearchParameters;
import com.football_club.Scouting.model.Report;
import com.football_club.Scouting.model.ValuedMetric;
import com.football_club.Scouting.repository.ReportRepository;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService implements IPlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerGraphRepository playerGraphRepository;
    private final ReportRepository reportRepository;
    private final org.springframework.transaction.support.TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final IFileStorageService fileStorageService;

    @Override
    @Transactional("transactionManager")
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        Player player = new Player();
        player.setName(playerDTO.getName());
        player.setSurname(playerDTO.getSurname());
        player.setDateOfBirth(playerDTO.getDateOfBirth());
        player.setPosition(playerDTO.getPlayerPosition());
        player.setImagePath(playerDTO.getImagePath());
        System.out.println("DEBUG: Postgres ID: " + player.getId());

        Player savedPlayer = playerRepository.saveAndFlush(player);
        playerRepository.flush();

        eventPublisher.publishEvent(new PlayerCreatedEvent(
                savedPlayer.getId(),
                savedPlayer.getName(),
                savedPlayer.getSurname(),
                savedPlayer.getPosition()
        ));

        return mapToDTO(savedPlayer);
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        return mapToDTO(player);
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public List<PlayerDTO> getPlayersByIds(List<Long> playerIds) {
        return playerRepository.findByIdIn(playerIds).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional("transactionManager")
    public PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));

        player.setName(playerDTO.getName());
        player.setSurname(playerDTO.getSurname());
        player.setDateOfBirth(playerDTO.getDateOfBirth());
        player.setPosition(playerDTO.getPlayerPosition());
        if (playerDTO.getImagePath() != null) {
            player.setImagePath(playerDTO.getImagePath());
        }

        Player updatedPlayer = playerRepository.save(player);

        eventPublisher.publishEvent(new PlayerUpdatedEvent(
                updatedPlayer.getId(),
                updatedPlayer.getName(),
                updatedPlayer.getSurname(),
                updatedPlayer.getPosition()
        ));

        return mapToDTO(updatedPlayer);
    }

    @Override
    @Transactional("transactionManager")
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
        eventPublisher.publishEvent(new PlayerDeletedEvent(id));
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public List<PlayerDTO> searchPlayers(String keyword) {
        return playerRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public List<PlayerDTO> advancedSearch(SearchParameters searchParameters) {
        List<Player> players = playerRepository.findAll()
                .stream()
                .filter(player -> matchesKeywordFilter(player, searchParameters.getSearchTerm()))
                .toList();

        if (searchParameters.getMetrics() == null || searchParameters.getMetrics().isEmpty()) {
            return players.stream().map(this::mapToDTO).collect(Collectors.toList());
        }

        return players.stream()
                .filter(player -> matchesMetricFilters(player, searchParameters.getMetrics()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private boolean matchesKeywordFilter(Player player, String keyword) {
        if (keyword == null || keyword.isBlank()) return true;
        String combinedName = player.getName() + " " + player.getSurname();
        return combinedName.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean matchesMetricFilters(Player player, List<SearchParameters.SearchMetric> filters) {
        Report latestReport = reportRepository.findLatestReportByPlayerId(player.getId());
        if (latestReport == null) return false;

        for (SearchParameters.SearchMetric filter : filters) {
            ValuedMetric vm = latestReport.getValuedMetrics().stream()
                    .filter(m -> m.getMetric().getId().equals(filter.getMetricId()))
                    .findFirst()
                    .orElse(null);

            if (vm == null) return false;

            boolean match = switch (filter.getSearchType()) {
                case EQUAL -> vm.getValue() == filter.getValue();
                case GREATER_THAN -> vm.getValue() > filter.getValue();
                case LESS_THAN -> vm.getValue() < filter.getValue();
            };

            if (!match) return false;
        }
        return true;
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public List<PlayerWithReportDTO> getPlayersForComparison(List<Long> ids) {
        List<Player> players = playerRepository.findAllById(ids);

        List<Report> reports = reportRepository.findLatestReportsForPlayers(ids);

        Map<Long, Report> playerReportMap = reports.stream()
                .collect(Collectors.toMap(r -> r.getPlayer().getId(), r -> r));

        return players.stream()
                .map(player -> {
                    PlayerDTO playerDTO = mapToDTO(player);
                    Report latestReport = playerReportMap.get(player.getId());
                    ReportDTO reportDTO = latestReport != null ? mapToReportDTO(latestReport, player) : null;

                    return PlayerWithReportDTO.builder()
                            .player(playerDTO)
                            .latestReport(reportDTO)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ReportDTO mapToReportDTO(Report report, Player player) {
        List<ValuedMetricDTO> metricDTOs = Collections.emptyList();
        if (report.getValuedMetrics() != null) {
            metricDTOs = report.getValuedMetrics().stream()
                    .filter(Objects::nonNull)
                    .map(vm -> ValuedMetricDTO.builder()
                            .id(vm.getId())
                            .reportId(report.getId())
                            .metricId(vm.getMetric().getId())
                            .metricName(vm.getMetric().getName())
                            .type(vm.getMetric().getType())
                            .value(vm.getValue())
                            .build())
                    .collect(Collectors.toList());
        }

        return ReportDTO.builder()
                .id(report.getId())
                .playerId(player.getId())
                .playerName(player.getName())
                .playerSurname(player.getSurname())
                .scoutId(report.getScout() != null ? report.getScout().getId() : null)
                .scoutUsername(report.getScout() != null ? report.getScout().getUsername() : null)
                .createdAt(report.getCreatedAt())
                .overallCommentary(report.getOverallCommentary())
                .clubAtTimeId(report.getClubAtTime() != null ? report.getClubAtTime().getId() : null)
                .clubAtTimeName(report.getClubAtTime() != null ? report.getClubAtTime().getName() : null)
                .leagueMultiplierAtTime(report.getLeagueMultiplierAtTime())
                .valuedMetrics(metricDTOs)
                .build();
    }

    @Transactional("transactionManager")
    public PlayerDTO uploadPlayerImage(Long id, MultipartFile file) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));

        String imagePath = fileStorageService.storeFile(file, "players");

        player.setImagePath(imagePath);
        Player updatedPlayer = playerRepository.save(player);

        return mapToDTO(updatedPlayer);
    }

    private PlayerDTO mapToDTO(Player player) {
        return PlayerDTO.builder()
                .id(player.getId())
                .name(player.getName())
                .surname(player.getSurname())
                .dateOfBirth(player.getDateOfBirth())
                .playerPosition(player.getPosition())
                .imagePath(player.getImagePath())
                .build();
    }
}