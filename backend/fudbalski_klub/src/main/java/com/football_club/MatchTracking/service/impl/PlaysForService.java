package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.PlaysForDTO;
import com.football_club.MatchTracking.model.Club;
import com.football_club.MatchTracking.model.Player;
import com.football_club.MatchTracking.model.PlaysFor;
import com.football_club.MatchTracking.model.graph.ClubGraph;
import com.football_club.MatchTracking.model.graph.PlayerGraph;
import com.football_club.MatchTracking.repository.ClubRepository;
import com.football_club.MatchTracking.repository.PlayerRepository;
import com.football_club.MatchTracking.repository.PlaysForRepository;
import com.football_club.MatchTracking.repository.graph.ClubGraphRepository;
import com.football_club.MatchTracking.repository.graph.PlayerGraphRepository;
import com.football_club.MatchTracking.service.IPlaysForService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaysForService implements IPlaysForService {

    private final PlaysForRepository playsForRepository;
    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;
    private final PlayerGraphRepository playerGraphRepository;
    private final ClubGraphRepository clubGraphRepository;

    @Override
    @Transactional
    public PlaysForDTO createContract(PlaysForDTO dto) {
        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + dto.getPlayerId()));

        Club club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + dto.getClubId()));

        PlaysFor contract = new PlaysFor();
        contract.setPlayer(player);
        contract.setClub(club);
        contract.setJerseyNumber(dto.getJerseyNumber());
        contract.setContractStart(dto.getContractStart());
        contract.setContractEnd(dto.getContractEnd());

        PlaysFor savedContract = playsForRepository.save(contract);

        PlayerGraph graphPlayer = playerGraphRepository.findById(savedContract.getPlayer().getId())
                .orElseThrow(() -> new RuntimeException("PlayerGraph node not found"));

        ClubGraph graphClub = clubGraphRepository.findById((long) savedContract.getClub().getId())
                .orElseThrow(() -> new RuntimeException("ClubGraph node not found"));

        graphPlayer.setClubGraph(graphClub);
        playerGraphRepository.save(graphPlayer);

        return mapToDTO(savedContract);
    }

    @Override
    public PlaysForDTO getContractById(Long id) {
        PlaysFor contract = playsForRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + id));
        return mapToDTO(contract);
    }

    @Override
    @Transactional
    public PlaysForDTO updateContract(Long id, PlaysForDTO dto) {
        PlaysFor contract = playsForRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + id));

        contract.setJerseyNumber(dto.getJerseyNumber());
        contract.setContractStart(dto.getContractStart());
        contract.setContractEnd(dto.getContractEnd());

        PlaysFor updatedContract = playsForRepository.save(contract);
        return mapToDTO(updatedContract);
    }

    @Override
    @Transactional
    public void deleteContract(Long id) {
        if (!playsForRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Contract not found with id: " + id);
        }
        playsForRepository.deleteById(id);

        playerGraphRepository.findById(id).ifPresent(graphPlayer -> {
            graphPlayer.setClubGraph(null);
            playerGraphRepository.save(graphPlayer);
        });
    }

    @Override
    public List<PlaysForDTO> getPlayerHistory(Long playerId) {
        return playsForRepository.findByPlayerId(playerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaysForDTO> getClubHistory(int clubId) {
        return playsForRepository.findByClubId(clubId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlaysForDTO getCurrentContract(Long playerId) {
        PlaysFor currentContract = playsForRepository.findCurrentContract(playerId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No active contract found for player id: " + playerId));
        return mapToDTO(currentContract);
    }

    @Override
    public List<PlaysForDTO> getCurrentRoster(int clubId) {
        return playsForRepository.findCurrentRoster(clubId, LocalDate.now()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PlaysForDTO mapToDTO(PlaysFor contract) {
        String positionName = (contract.getPlayer().getPosition() != null)
                ? contract.getPlayer().getPosition().toString()
                : "N/A";

        return PlaysForDTO.builder()
                .id(contract.getId())
                .playerId(contract.getPlayer().getId())
                .playerName(contract.getPlayer().getName())
                .playerSurname(contract.getPlayer().getSurname())
                .clubId(contract.getClub().getId())
                .clubName(contract.getClub().getName())
                .jerseyNumber(contract.getJerseyNumber())
                .contractStart(contract.getContractStart())
                .contractEnd(contract.getContractEnd())
                .position(positionName)
                .build();
    }
}