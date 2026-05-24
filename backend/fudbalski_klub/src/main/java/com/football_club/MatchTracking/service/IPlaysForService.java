package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.dto.PlaysForDTO;
import java.util.List;

public interface IPlaysForService {

    PlaysForDTO createContract(PlaysForDTO playsForDTO);

    PlaysForDTO getContractById(Long id);

    PlaysForDTO updateContract(Long id, PlaysForDTO playsForDTO);

    void deleteContract(Long id);

    List<PlaysForDTO> getPlayerHistory(Long playerId);

    List<PlaysForDTO> getClubHistory(int clubId);

    PlaysForDTO getCurrentContract(Long playerId);

    List<PlaysForDTO> getCurrentRoster(int clubId);
}