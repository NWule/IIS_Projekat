package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.dto.AppearanceDTO;
import com.football_club.MatchTracking.dto.GameLineupResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IAppearanceService {

    AppearanceDTO createAppearance(AppearanceDTO appearanceDTO);

    AppearanceDTO getAppearanceById(Long id);

    List<AppearanceDTO> getAppearancesByGame(Long gameId);

    List<AppearanceDTO> getAppearancesByPlayer(Long playsForId);

    AppearanceDTO updateAppearance(Long id, AppearanceDTO appearanceDTO);

    void deleteAppearance(Long id);

    GameLineupResponseDTO saveLineup(Long gameId, Long clubId, List<AppearanceDTO> lineupDTOs);

    List<AppearanceDTO> parseLineupFromPdf(MultipartFile file, Integer clubId) throws IOException;
}