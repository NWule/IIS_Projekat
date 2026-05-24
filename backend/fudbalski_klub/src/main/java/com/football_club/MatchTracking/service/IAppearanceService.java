package com.football_club.MatchTracking.service;

import com.football_club.MatchTracking.dto.AppearanceDTO;

import java.util.List;

public interface IAppearanceService {

    AppearanceDTO createAppearance(AppearanceDTO appearanceDTO);

    AppearanceDTO getAppearanceById(Long id);

    List<AppearanceDTO> getAppearancesByGame(Long gameId);

    List<AppearanceDTO> getAppearancesByPlayer(Long playsForId);

    AppearanceDTO updateAppearance(Long id, AppearanceDTO appearanceDTO);

    void deleteAppearance(Long id);
}