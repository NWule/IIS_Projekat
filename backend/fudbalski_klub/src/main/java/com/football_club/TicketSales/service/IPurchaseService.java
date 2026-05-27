package com.football_club.TicketSales.service;

import com.football_club.Auth.model.User;
import com.football_club.TicketSales.dto.PurchaseRequestDTO;
import com.football_club.TicketSales.dto.PurchaseResponseDTO;

import java.util.List;

public interface IPurchaseService {
    PurchaseResponseDTO purchase(PurchaseRequestDTO request, User buyer);
    List<PurchaseResponseDTO> getMyPurchases(User buyer);
    PurchaseResponseDTO getPurchaseById(Long id, User buyer);
}
