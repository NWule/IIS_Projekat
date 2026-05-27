package com.football_club.TicketSales.service;

import com.football_club.TicketSales.dto.ZoneDTO;

import java.util.List;

public interface IZoneService {
    ZoneDTO createZone(ZoneDTO zoneDTO);
    ZoneDTO getZoneById(Long id);
    List<ZoneDTO> getAllZones();
    ZoneDTO updateZone(Long id, ZoneDTO zoneDTO);
    void deleteZone(Long id);
}
