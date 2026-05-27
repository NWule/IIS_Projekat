package com.football_club.TicketSales.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {
    private Long gameId;
    private String buyerFirstName;
    private String buyerLastName;
    private String buyerEmail;
    private String cardHolderName;
    private String cardNumber;
    private String cardExpiry;
    private String cvv;
    private List<PurchaseItemDTO> items;
}
