package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.ClubDTO;
import com.football_club.MatchTracking.model.Club;
import com.football_club.MatchTracking.repository.ClubRepository;
import com.football_club.MatchTracking.service.ICLubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubService implements ICLubService {
    private final ClubRepository clubRepository;

    @Override
    @Transactional
    public ClubDTO createClub(ClubDTO clubDTO) {
        if (clubRepository.findByName(clubDTO.getName()).isPresent()) {
            throw new RuntimeException("Club with name '" + clubDTO.getName() + "' already exists!");
        }

        Club club = new Club();
        club.setName(clubDTO.getName());
        club.setLocation(clubDTO.getLocation());
        Club savedClub = clubRepository.save(club);
        return mapToDTO(savedClub);
    }

    @Override
    public ClubDTO getClubById(int id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));
        return mapToDTO(club);
    }

    @Override
    public List<ClubDTO> getAllClubs() {
        return clubRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClubDTO updateClub(int id, ClubDTO clubDTO) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));

        if (!club.getName().equals(clubDTO.getName()) && clubRepository.findByName(clubDTO.getName()).isPresent()) {
            throw new RuntimeException("Club with name '" + clubDTO.getName() + "' already exists!");
        }

        club.setName(clubDTO.getName());
        club.setLocation(clubDTO.getLocation());
        club.setWins(clubDTO.getWins());
        club.setLosses(clubDTO.getLosses());
        club.setDraws(clubDTO.getDraws());
        club.setGoalsScored(clubDTO.getGoalsScored());
        club.setGoalsConceded(clubDTO.getGoalsConceded());

        Club updatedClub = clubRepository.save(club);
        return mapToDTO(updatedClub);
    }

    @Override
    @Transactional
    public void deleteClub(int id) {
        if (!clubRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Club not found with id: " + id);
        }
        clubRepository.deleteById(id);
    }

    @Override
    public List<ClubDTO> getLeagueTable() {
        return clubRepository.findAllByOrderByWinsDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClubDTO> searchClubsByName(String name) {
        return clubRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ClubDTO mapToDTO(Club club) {
        return ClubDTO.builder()
                .id(club.getId())
                .name(club.getName())
                .location(club.getLocation())
                .wins(club.getWins())
                .losses(club.getLosses())
                .draws(club.getDraws())
                .goalsScored(club.getGoalsScored())
                .goalsConceded(club.getGoalsConceded())
                .build();
    }
}
