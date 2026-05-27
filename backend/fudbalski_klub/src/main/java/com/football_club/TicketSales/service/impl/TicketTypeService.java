package com.football_club.TicketSales.service.impl;

import com.football_club.TicketSales.dto.TicketTypeDTO;
import com.football_club.TicketSales.model.TicketType;
import com.football_club.TicketSales.repository.TicketTypeRepository;
import com.football_club.TicketSales.service.ITicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketTypeService implements ITicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;

    @Override
    @Transactional
    public TicketTypeDTO createTicketType(TicketTypeDTO dto) {
        if (ticketTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Ticket type with name '" + dto.getName() + "' already exists!");
        }
        TicketType ticketType = new TicketType();
        ticketType.setName(dto.getName());
        ticketType.setDescription(dto.getDescription());
        ticketType.setPriceModifier(dto.getPriceModifier());
        return mapToDTO(ticketTypeRepository.save(ticketType));
    }

    @Override
    public TicketTypeDTO getTicketTypeById(Long id) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket type not found with id: " + id));
        return mapToDTO(ticketType);
    }

    @Override
    public List<TicketTypeDTO> getAllTicketTypes() {
        return ticketTypeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TicketTypeDTO updateTicketType(Long id, TicketTypeDTO dto) {
        TicketType ticketType = ticketTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket type not found with id: " + id));

        if (!ticketType.getName().equals(dto.getName()) &&
                ticketTypeRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Ticket type with name '" + dto.getName() + "' already exists!");
        }

        ticketType.setName(dto.getName());
        ticketType.setDescription(dto.getDescription());
        ticketType.setPriceModifier(dto.getPriceModifier());
        return mapToDTO(ticketTypeRepository.save(ticketType));
    }

    @Override
    @Transactional
    public void deleteTicketType(Long id) {
        if (!ticketTypeRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Ticket type not found with id: " + id);
        }
        ticketTypeRepository.deleteById(id);
    }

    private TicketTypeDTO mapToDTO(TicketType ticketType) {
        return TicketTypeDTO.builder()
                .id(ticketType.getId())
                .name(ticketType.getName())
                .description(ticketType.getDescription())
                .priceModifier(ticketType.getPriceModifier())
                .build();
    }
}
