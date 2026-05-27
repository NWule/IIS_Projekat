package com.football_club.TicketSales.service.impl;

import com.football_club.TicketSales.dto.ZoneDTO;
import com.football_club.TicketSales.model.Zone;
import com.football_club.TicketSales.repository.ZoneRepository;
import com.football_club.TicketSales.service.IZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneService implements IZoneService {

    private final ZoneRepository zoneRepository;

    @Override
    @Transactional
    public ZoneDTO createZone(ZoneDTO zoneDTO) {
        if (zoneRepository.findByName(zoneDTO.getName()).isPresent()) {
            throw new RuntimeException("Zone with name '" + zoneDTO.getName() + "' already exists!");
        }
        Zone zone = new Zone();
        zone.setName(zoneDTO.getName());
        zone.setColor(zoneDTO.getColor());
        zone.setBasePrice(zoneDTO.getBasePrice());
        zone.setNumberOfRows(zoneDTO.getNumberOfRows());
        zone.setSeatsPerRow(zoneDTO.getSeatsPerRow());
        return mapToDTO(zoneRepository.save(zone));
    }

    @Override
    public ZoneDTO getZoneById(Long id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + id));
        return mapToDTO(zone);
    }

    @Override
    public List<ZoneDTO> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ZoneDTO updateZone(Long id, ZoneDTO zoneDTO) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + id));

        if (!zone.getName().equals(zoneDTO.getName()) && zoneRepository.findByName(zoneDTO.getName()).isPresent()) {
            throw new RuntimeException("Zone with name '" + zoneDTO.getName() + "' already exists!");
        }

        zone.setName(zoneDTO.getName());
        zone.setColor(zoneDTO.getColor());
        zone.setBasePrice(zoneDTO.getBasePrice());
        zone.setNumberOfRows(zoneDTO.getNumberOfRows());
        zone.setSeatsPerRow(zoneDTO.getSeatsPerRow());
        return mapToDTO(zoneRepository.save(zone));
    }

    @Override
    @Transactional
    public void deleteZone(Long id) {
        if (!zoneRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Zone not found with id: " + id);
        }
        zoneRepository.deleteById(id);
    }

    private ZoneDTO mapToDTO(Zone zone) {
        return ZoneDTO.builder()
                .id(zone.getId())
                .name(zone.getName())
                .color(zone.getColor())
                .basePrice(zone.getBasePrice())
                .numberOfRows(zone.getNumberOfRows())
                .seatsPerRow(zone.getSeatsPerRow())
                .build();
    }
}
