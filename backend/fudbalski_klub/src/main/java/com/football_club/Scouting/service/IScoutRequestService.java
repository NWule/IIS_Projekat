package com.football_club.Scouting.service;

import com.football_club.Scouting.dto.ScoutRequestDTO;
import com.football_club.Scouting.dto.ScoutRequestSaveDTO;

import java.util.List;

public interface IScoutRequestService {
    ScoutRequestDTO createRequest(ScoutRequestSaveDTO dto, Long directorId);
    ScoutRequestDTO getRequestById(Long id);
    List<ScoutRequestDTO> getAllRequests();
    ScoutRequestDTO updateRequest(Long id, ScoutRequestSaveDTO dto);
    void deleteRequest(Long id);
    List<ScoutRequestDTO> getUnclaimedRequests();
    List<ScoutRequestDTO> getRequestsByScout(Long scoutId);
    List<ScoutRequestDTO> getRequestsByDirector(Long directorId);
    ScoutRequestDTO claimRequest(Long id, Long scoutId);
    ScoutRequestDTO completeRequest(Long id);
}
