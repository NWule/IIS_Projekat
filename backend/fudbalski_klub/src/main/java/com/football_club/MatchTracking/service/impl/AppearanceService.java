package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.dto.AppearanceDTO;
import com.football_club.MatchTracking.dto.GameLineupResponseDTO;
import com.football_club.MatchTracking.event.AppearanceCreatedEvent;
import com.football_club.MatchTracking.event.AppearanceDeletedEvent;
import com.football_club.MatchTracking.event.AppearanceUpdatedEvent;
import com.football_club.MatchTracking.model.Appearance;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.PlaysFor;
import com.football_club.MatchTracking.model.enums.GameStatus;
import com.football_club.MatchTracking.model.enums.MatchRole;
import com.football_club.MatchTracking.repository.jpa.AppearanceRepository;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.MatchTracking.repository.jpa.PlaysForRepository;
import com.football_club.MatchTracking.repository.graph.AppearanceGraphRepository;
import com.football_club.MatchTracking.repository.graph.GameGraphRepository;
import com.football_club.MatchTracking.repository.graph.PlayerGraphRepository;
import com.football_club.MatchTracking.service.IAppearanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.text.Normalizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppearanceService implements IAppearanceService {

    private final AppearanceRepository appearanceRepository;
    private final PlaysForRepository playsForRepository;
    private final GameRepository gameRepository;
    private final AppearanceGraphRepository appearanceGraphRepository;
    private final PlayerGraphRepository playerGraphRepository;
    private final GameGraphRepository gameGraphRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(value="transactionManager")
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

        appearanceRepository.flush();

        eventPublisher.publishEvent(new AppearanceCreatedEvent(
                savedAppearance.getId(), savedAppearance.getMatchRole(), savedAppearance.getMinutesPlayed(),
                savedAppearance.getGoals(), savedAppearance.getAssists(), savedAppearance.getFouls(),
                savedAppearance.getYellowCards(), savedAppearance.isRedCard(), savedAppearance.getRating(),
                savedAppearance.getPassingAccuracy(), savedAppearance.getPlaysFor().getPlayer().getId(),
                savedAppearance.getGame().getId()
        ));

        return mapToDTO(savedAppearance);
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public AppearanceDTO getAppearanceById(Long id) {
        Appearance appearance = appearanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appearance not found with id: " + id));
        return mapToDTO(appearance);
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public List<AppearanceDTO> getAppearancesByGame(Long gameId) {
        return appearanceRepository.findAppearancesWithPlayerInfoByGameId(gameId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(value="transactionManager", readOnly = true)
    public List<AppearanceDTO> getAppearancesByPlayer(Long playsForId) {
        return appearanceRepository.findByPlaysForId(playsForId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(value="transactionManager")
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
        appearanceRepository.flush();

        eventPublisher.publishEvent(new AppearanceUpdatedEvent(
                updatedAppearance.getId(), updatedAppearance.getMatchRole(), updatedAppearance.getMinutesPlayed(),
                updatedAppearance.getGoals(), updatedAppearance.getAssists(), updatedAppearance.getFouls(),
                updatedAppearance.getYellowCards(), updatedAppearance.isRedCard(), updatedAppearance.getRating(),
                updatedAppearance.getPassingAccuracy(), updatedAppearance.getPlaysFor().getPlayer().getId(),
                updatedAppearance.getGame().getId()
        ));
        return mapToDTO(updatedAppearance);
    }

    @Override
    @Transactional(value="transactionManager")
    public void deleteAppearance(Long id) {
        if (!appearanceRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Appearance not found with id: " + id);
        }
        appearanceRepository.deleteById(id);
        eventPublisher.publishEvent(new AppearanceDeletedEvent(id));
    }

    @Override
    @Transactional(value="transactionManager")
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
            List<Long> idsToDelete = clubAppearancesToDelete.stream()
                    .map(Appearance::getId)
                    .collect(Collectors.toList());
            appearanceGraphRepository.deleteAllById(idsToDelete);
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
        List<Appearance> savedAppearances = appearanceRepository.saveAll(appearancesToSave);

        appearanceRepository.flush();


        for (Appearance app : savedAppearances) {
            eventPublisher.publishEvent(new AppearanceUpdatedEvent(
                    app.getId(), app.getMatchRole(), app.getMinutesPlayed(),
                    app.getGoals(), app.getAssists(), app.getFouls(),
                    app.getYellowCards(), app.isRedCard(), app.getRating(),
                    app.getPassingAccuracy(), app.getPlaysFor().getPlayer().getId(),
                    app.getGame().getId()
            ));
        }

        List<AppearanceDTO> allAppearances = appearanceRepository.findAppearancesWithPlayerInfoByGameId(gameId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());

        List<AppearanceDTO> startingXi = allAppearances.stream()
                .filter(app -> "STARTING_XI".equals(app.getMatchRole())).collect(Collectors.toList());

        List<AppearanceDTO> bench = allAppearances.stream()
                .filter(app -> "BENCH".equals(app.getMatchRole())).collect(Collectors.toList());

        return new GameLineupResponseDTO(startingXi, bench);
    }

    @Override
    public List<AppearanceDTO> parseLineupFromPdf(MultipartFile file, Integer clubId) throws IOException {

        List<PlaysFor> roster = playsForRepository.findByClubId(clubId);

        String pdfText = "";
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            pdfText = stripper.getText(document);
        }

        String[] lines = pdfText.split("\\r?\\n");
        List<AppearanceDTO> recognizedPlayers = new ArrayList<>();
        int matchedCount = 0;

        for (String line : lines) {
            String normalizedLine = normalizeString(line);

            for (PlaysFor contract : roster) {
                if (recognizedPlayers.stream().anyMatch(p -> p.getPlaysForId().equals(contract.getId()))) {
                    continue;
                }

                String normalizedSurname = normalizeString(contract.getPlayer().getSurname());
                String jerseyStr = String.valueOf(contract.getJerseyNumber());

                if (normalizedLine.contains(normalizedSurname) || normalizedLine.matches(".*\\b" + jerseyStr + "\\b.*")) {

                    AppearanceDTO dto = AppearanceDTO.builder()
                            .playsForId(contract.getId())
                            .playerName(contract.getPlayer().getName())
                            .playerSurname(contract.getPlayer().getSurname())
                            .clubId(clubId)
                            .build();

                    matchedCount++;
                    if (matchedCount <= 11) {
                        dto.setMatchRole("STARTING_XI");
                    } else {
                        dto.setMatchRole("BENCH");
                    }

                    recognizedPlayers.add(dto);
                    break;
                }
            }
        }
        return recognizedPlayers;
    }

    private String normalizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase().trim();
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