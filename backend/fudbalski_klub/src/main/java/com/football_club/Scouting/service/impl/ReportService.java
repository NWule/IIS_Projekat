package com.football_club.Scouting.service.impl;

import com.football_club.Auth.model.User;
import com.football_club.Auth.repository.UserRepository;
import com.football_club.MatchTracking.model.Club;
import com.football_club.MatchTracking.model.Player;
import com.football_club.MatchTracking.repository.ClubRepository;
import com.football_club.MatchTracking.repository.PlayerRepository;
import com.football_club.Scouting.dto.ReportDTO;
import com.football_club.Scouting.dto.ReportSaveDTO;
import com.football_club.Scouting.dto.ValuedMetricDTO;
import com.football_club.Scouting.model.Report;
import com.football_club.Scouting.model.ScoutRequest;
import com.football_club.Scouting.model.enums.RequestStatus;
import com.football_club.Scouting.repository.ReportRepository;
import com.football_club.Scouting.repository.ScoutRequestRepository;
import com.football_club.Scouting.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {

    private final ReportRepository reportRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ScoutRequestRepository scoutRequestRepository;

    @Override
    @Transactional
    public ReportDTO createReport(ReportSaveDTO dto, Long scoutId) {
        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new NoSuchElementException("Igrač nije pronađen sa ID-em: " + dto.getPlayerId()));

        User scout = userRepository.findById(scoutId)
                .orElseThrow(() -> new NoSuchElementException("Skaut nije pronađen sa ID-em: " + scoutId));

        Club club = null;
        if (dto.getClubAtTimeId() != null) {
            club = clubRepository.findById(dto.getClubAtTimeId())
                    .orElseThrow(() -> new NoSuchElementException("Klub nije pronađen sa ID-em: " + dto.getClubAtTimeId()));
        }

        scoutRequestRepository.findByScoutIdAndPlayerIdAndStatusIn(
                scoutId, 
                player.getId(), 
                List.of(RequestStatus.IN_PROGRESS)
        ).ifPresent(request -> {
            request.setStatus(RequestStatus.COMPLETED);
            scoutRequestRepository.save(request);
        });

        Report report = new Report();
        report.setPlayer(player);
        report.setScout(scout);
        report.setClubAtTime(club);
        report.setCreatedAt(LocalDateTime.now());
        report.setOverallCommentary(dto.getOverallCommentary());
        report.setLeagueMultiplierAtTime(dto.getLeagueMultiplierAtTime());

        Report savedReport = reportRepository.save(report);
        return mapToDTO(savedReport);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDTO getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Izveštaj sa ID-em " + id + " ne postoji."));
        return mapToDTO(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReportDTO updateReport(Long id, ReportSaveDTO dto, Long scoutId) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Izveštaj sa ID-em " + id + " ne postoji."));

        Player player = playerRepository.findById(dto.getPlayerId())
                .orElseThrow(() -> new NoSuchElementException("Igrač nije pronađen sa ID-em: " + dto.getPlayerId()));

        User scout = userRepository.findById(scoutId)
                .orElseThrow(() -> new NoSuchElementException("Skaut nije pronađen sa ID-em: " + scoutId));

        Club club = null;
        if (dto.getClubAtTimeId() != null) {
            club = clubRepository.findById(dto.getClubAtTimeId())
                    .orElseThrow(() -> new NoSuchElementException("Klub nije pronađen sa ID-em: " + dto.getClubAtTimeId()));
        }

        report.setPlayer(player);
        report.setScout(scout);
        report.setClubAtTime(club);
        report.setOverallCommentary(dto.getOverallCommentary());
        report.setLeagueMultiplierAtTime(dto.getLeagueMultiplierAtTime());

        Report updatedReport = reportRepository.save(report);
        return mapToDTO(updatedReport);
    }

    @Override
    @Transactional
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new NoSuchElementException("Neuspešno brisanje. Izveštaj sa ID-em " + id + " ne postoji.");
        }
        reportRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO> getReportsByScout(Long scoutId) {
        return reportRepository.findByScoutId(scoutId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO> getReportsByPlayer(Long playerId) {
        return reportRepository.findByPlayerIdWithMetrics(playerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReportDTO mapToDTO(Report report) {
        List<ValuedMetricDTO> metrics = report.getValuedMetrics() != null ? 
                report.getValuedMetrics().stream()
                        .map(vm -> ValuedMetricDTO.builder()
                                .id(vm.getId())
                                .reportId(report.getId())
                                .metricId(vm.getMetric().getId())
                                .metricName(vm.getMetric().getName())
                                .value(vm.getValue())
                                .build())
                        .collect(Collectors.toList()) : Collections.emptyList();

        return ReportDTO.builder()
                .id(report.getId())
                .playerId(report.getPlayer().getId())
                .playerName(report.getPlayer().getName())
                .playerSurname(report.getPlayer().getSurname())
                .scoutId(report.getScout().getId())
                .scoutUsername(report.getScout().getUsername())
                .createdAt(report.getCreatedAt())
                .overallCommentary(report.getOverallCommentary())
                .clubAtTimeId(report.getClubAtTime() != null ? report.getClubAtTime().getId() : null)
                .clubAtTimeName(report.getClubAtTime() != null ? report.getClubAtTime().getName() : null)
                .leagueMultiplierAtTime(report.getLeagueMultiplierAtTime())
                .valuedMetrics(metrics)
                .build();
    }
}
