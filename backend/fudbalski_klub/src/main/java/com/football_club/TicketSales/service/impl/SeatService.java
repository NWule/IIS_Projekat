package com.football_club.TicketSales.service.impl;

import com.football_club.TicketSales.dto.SeatDTO;
import com.football_club.TicketSales.model.Seat;
import com.football_club.TicketSales.model.Zone;
import com.football_club.TicketSales.model.enums.SeatStatus;
import com.football_club.TicketSales.repository.SeatRepository;
import com.football_club.TicketSales.repository.ZoneRepository;
import com.football_club.TicketSales.service.ISeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService implements ISeatService {

    private final SeatRepository seatRepository;
    private final ZoneRepository zoneRepository;

    @Override
    @Transactional
    public SeatDTO createSeat(SeatDTO seatDTO) {
        Zone zone = zoneRepository.findById(seatDTO.getZoneId())
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + seatDTO.getZoneId()));

        if (seatRepository.findByZoneIdAndRowNumberAndSeatNumber(
                seatDTO.getZoneId(), seatDTO.getRowNumber(), seatDTO.getSeatNumber()).isPresent()) {
            throw new RuntimeException("Seat at row " + seatDTO.getRowNumber() +
                    ", number " + seatDTO.getSeatNumber() + " already exists in this zone!");
        }

        Seat seat = new Seat();
        seat.setRowNumber(seatDTO.getRowNumber());
        seat.setSeatNumber(seatDTO.getSeatNumber());
        seat.setStatus(seatDTO.getStatus() != null ? seatDTO.getStatus() : SeatStatus.AVAILABLE);
        seat.setZone(zone);
        return mapToDTO(seatRepository.save(seat));
    }

    @Override
    public SeatDTO getSeatById(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + id));
        return mapToDTO(seat);
    }

    @Override
    public List<SeatDTO> getAllSeats() {
        return seatRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatDTO> getSeatsByZone(Long zoneId) {
        if (!zoneRepository.existsById(zoneId)) {
            throw new RuntimeException("Zone not found with id: " + zoneId);
        }
        return seatRepository.findByZoneId(zoneId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatDTO> getAvailableSeatsByZone(Long zoneId) {
        if (!zoneRepository.existsById(zoneId)) {
            throw new RuntimeException("Zone not found with id: " + zoneId);
        }
        return seatRepository.findByZoneIdAndStatus(zoneId, SeatStatus.AVAILABLE).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SeatDTO updateSeat(Long id, SeatDTO seatDTO) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + id));

        Zone zone = zoneRepository.findById(seatDTO.getZoneId())
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + seatDTO.getZoneId()));

        boolean positionChanged = seat.getRowNumber() != seatDTO.getRowNumber()
                || seat.getSeatNumber() != seatDTO.getSeatNumber()
                || !seat.getZone().getId().equals(seatDTO.getZoneId());

        if (positionChanged && seatRepository.findByZoneIdAndRowNumberAndSeatNumber(
                seatDTO.getZoneId(), seatDTO.getRowNumber(), seatDTO.getSeatNumber()).isPresent()) {
            throw new RuntimeException("Seat at row " + seatDTO.getRowNumber() +
                    ", number " + seatDTO.getSeatNumber() + " already exists in this zone!");
        }

        seat.setRowNumber(seatDTO.getRowNumber());
        seat.setSeatNumber(seatDTO.getSeatNumber());
        seat.setStatus(seatDTO.getStatus());
        seat.setZone(zone);
        return mapToDTO(seatRepository.save(seat));
    }

    @Override
    @Transactional
    public void deleteSeat(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Seat not found with id: " + id);
        }
        seatRepository.deleteById(id);
    }

    private SeatDTO mapToDTO(Seat seat) {
        return SeatDTO.builder()
                .id(seat.getId())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .status(seat.getStatus())
                .zoneId(seat.getZone().getId())
                .zoneName(seat.getZone().getName())
                .build();
    }
}
