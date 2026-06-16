package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.AppearanceDTO;
import com.football_club.MatchTracking.dto.GameLineupResponseDTO;
import com.football_club.MatchTracking.model.Appearance;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.PlaysFor;
import com.football_club.MatchTracking.model.enums.GameStatus;
import com.football_club.MatchTracking.model.enums.MatchRole;
import com.football_club.MatchTracking.repository.AppearanceRepository;
import com.football_club.MatchTracking.repository.GameRepository;
import com.football_club.MatchTracking.repository.PlaysForRepository;
import com.football_club.MatchTracking.service.IAppearanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppearanceService implements IAppearanceService {

    private final AppearanceRepository appearanceRepository;
    private final PlaysForRepository playsForRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional
    public AppearanceDTO createAppearance(AppearanceDTO dto) {
        PlaysFor playsFor = playsForRepository.findById(dto.getPlaysForId())
                .orElseThrow(() -> new RuntimeException("PlaysFor record not found with id: " + dto.getPlaysForId()));

        Game game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + dto.getGameId()));

        Appearance appearance = new Appearance();
        appearance.setPlaysFor(playsFor);
        appearance.setGame(game);
        appearance.setMinutesPlayed(dto.getMinutesPlayed());
        appearance.setGoals(dto.getGoals());
        appearance.setAssists(dto.getAssists());
        appearance.setFouls(dto.getFouls());
        appearance.setYellowCards(dto.getYellowCards());
        appearance.setRedCard(dto.isRedCard());
        appearance.setRating(dto.getRating());
        appearance.setPassingAccuracy(dto.getPassingAccuracy());
        appearance.setMatchRole(MatchRole.valueOf(dto.getMatchRole()));

        Appearance savedAppearance = appearanceRepository.save(appearance);
        return mapToDTO(savedAppearance);
    }

    @Override
    public AppearanceDTO getAppearanceById(Long id) {
        Appearance appearance = appearanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appearance not found with id: " + id));
        return mapToDTO(appearance);
    }

    @Override
    public List<AppearanceDTO> getAppearancesByGame(Long gameId) {
        return appearanceRepository.findAppearancesWithPlayerInfoByGameId(gameId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppearanceDTO> getAppearancesByPlayer(Long playsForId) {
        return appearanceRepository.findByPlaysForId(playsForId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppearanceDTO updateAppearance(Long id, AppearanceDTO dto) {
        Appearance appearance = appearanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appearance not found with id: " + id));

        appearance.setMinutesPlayed(dto.getMinutesPlayed());
        appearance.setGoals(dto.getGoals());
        appearance.setAssists(dto.getAssists());
        appearance.setFouls(dto.getFouls());
        appearance.setYellowCards(dto.getYellowCards());
        appearance.setRedCard(dto.isRedCard());
        appearance.setRating(dto.getRating());
        appearance.setPassingAccuracy(dto.getPassingAccuracy());
        appearance.setMatchRole(MatchRole.valueOf(dto.getMatchRole()));

        Appearance updatedAppearance = appearanceRepository.save(appearance);
        return mapToDTO(updatedAppearance);
    }

    @Override
    @Transactional
    public void deleteAppearance(Long id) {
        if (!appearanceRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Appearance not found with id: " + id);
        }
        appearanceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public GameLineupResponseDTO saveLineup(Long gameId, Long clubId, List<AppearanceDTO> lineupDTOs){
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new RuntimeException("Game not found with id: " + gameId));
        if (game.getStatus() != GameStatus.UPCOMING) {
            throw new RuntimeException("Error: You cannot change the lineup if the match has started or finished.");
        }

        List<Appearance> existingAppearances = appearanceRepository.findAppearancesWithPlayerInfoByGameId(gameId);

        List<Appearance> clubAppearancesToDelete = existingAppearances.stream()
                .filter(app -> app.getPlaysFor().getClub().getId().equals(clubId))
                .collect(Collectors.toList());

        if (!clubAppearancesToDelete.isEmpty()) {
            appearanceRepository.deleteAll(clubAppearancesToDelete);
        }

        List<Appearance> appearancesToSave = new ArrayList<>();
        for (AppearanceDTO dto : lineupDTOs){
            Appearance appearance = appearanceRepository.findByPlaysForIdAndGameId(dto.getPlaysForId(), gameId).orElse(new Appearance());
            if(appearance.getId() == null){
                PlaysFor playsFor = playsForRepository.findById(dto.getPlaysForId())
                        .orElseThrow(() -> new RuntimeException("PlaysFor record not found with id: " + dto.getPlaysForId()));
                appearance.setGame(game);
                appearance.setPlaysFor(playsFor);
            }
            appearance.setMatchRole(MatchRole.valueOf(dto.getMatchRole()));
            appearancesToSave.add(appearance);
        }

        appearanceRepository.saveAll(appearancesToSave);

        List<AppearanceDTO> allAppearances = appearanceRepository.findAppearancesWithPlayerInfoByGameId(gameId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());

        List<AppearanceDTO> startingXi = allAppearances.stream()
                .filter(app -> "STARTING_XI".equals(app.getMatchRole())).collect(Collectors.toList());

        List<AppearanceDTO> bench = allAppearances.stream()
                .filter(app -> "BENCH".equals(app.getMatchRole())).collect(Collectors.toList());

        return new GameLineupResponseDTO(startingXi, bench);
    }



    private AppearanceDTO mapToDTO(Appearance appearance) {
        return AppearanceDTO.builder()
                .id(appearance.getId())
                .playsForId(appearance.getPlaysFor().getId())
                .playerName(appearance.getPlaysFor().getPlayer().getName())
                .playerSurname(appearance.getPlaysFor().getPlayer().getSurname())
                .gameId(appearance.getGame().getId())
                .matchRole(String.valueOf(appearance.getMatchRole()))
                .minutesPlayed(appearance.getMinutesPlayed())
                .goals(appearance.getGoals())
                .assists(appearance.getAssists())
                .fouls(appearance.getFouls())
                .yellowCards(appearance.getYellowCards())
                .redCard(appearance.isRedCard())
                .rating(appearance.getRating())
                .passingAccuracy(appearance.getPassingAccuracy())
                .clubId(appearance.getPlaysFor().getClub().getId())
                .build();
    }
}