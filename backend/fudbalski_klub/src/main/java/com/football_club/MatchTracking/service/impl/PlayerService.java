package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.PlayerDTO;
import com.football_club.MatchTracking.model.Player;
import com.football_club.MatchTracking.model.graph.PlayerGraph;
import com.football_club.MatchTracking.repository.PlayerRepository;
import com.football_club.MatchTracking.repository.graph.PlayerGraphRepository;
import com.football_club.MatchTracking.service.IPlayerService;
import com.football_club.Scouting.dto.SearchParameters;
import com.football_club.Scouting.model.Report;
import com.football_club.Scouting.model.ValuedMetric;
import com.football_club.Scouting.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService implements IPlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerGraphRepository playerGraphRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        Player player = new Player();
        player.setName(playerDTO.getName());
        player.setSurname(playerDTO.getSurname());
        player.setDateOfBirth(playerDTO.getDateOfBirth());
        player.setPosition(playerDTO.getPlayerPosition());

        Player savedPlayer = playerRepository.save(player);

        PlayerGraph graphPlayer = new PlayerGraph();
        graphPlayer.setPlayerId(savedPlayer.getId());
        graphPlayer.setPlayerId(savedPlayer.getId());
        graphPlayer.setName(savedPlayer.getName());
        graphPlayer.setSurname(savedPlayer.getSurname());
        graphPlayer.setPosition(savedPlayer.getPosition());

        playerGraphRepository.save(graphPlayer);
        return mapToDTO(savedPlayer);
    }

    @Override
    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        return mapToDTO(player);
    }

    @Override
    public List<PlayerDTO> getPlayersByIds(List<Long> playerIds) {
        return playerRepository.findByIdIn(playerIds).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));

        player.setName(playerDTO.getName());
        player.setSurname(playerDTO.getSurname());
        player.setDateOfBirth(playerDTO.getDateOfBirth());
        player.setPosition(playerDTO.getPlayerPosition());

        Player updatedPlayer = playerRepository.save(player);

        PlayerGraph graphPlayer = playerGraphRepository.findById(id)
                .orElse(new PlayerGraph());

        graphPlayer.setPlayerId(updatedPlayer.getId());
        graphPlayer.setName(updatedPlayer.getName());
        graphPlayer.setSurname(updatedPlayer.getSurname());
        graphPlayer.setPosition(updatedPlayer.getPosition());

        playerGraphRepository.save(graphPlayer);

        return mapToDTO(updatedPlayer);
    }

    @Override
    @Transactional
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
        playerGraphRepository.deleteById(id);
    }

    @Override
    public List<PlayerDTO> searchPlayers(String keyword) {
        return playerRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
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

    private PlayerDTO mapToDTO(Player player) {
        return PlayerDTO.builder()
                .id(player.getId())
                .name(player.getName())
                .surname(player.getSurname())
                .dateOfBirth(player.getDateOfBirth())
                .playerPosition(player.getPosition())
                .build();
    }
}