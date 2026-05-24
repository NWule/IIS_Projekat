package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.dto.ClubDTO;

import java.util.List;

public interface ICLubService {
    ClubDTO createClub(ClubDTO clubDTO);

    ClubDTO getClubById(int id);

    List<ClubDTO> getAllClubs();

    ClubDTO updateClub(int id, ClubDTO clubDTO);

    void deleteClub(int id);

    List<ClubDTO> getLeagueTable();

    List<ClubDTO> searchClubsByName(String name);
}
