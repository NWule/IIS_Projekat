package com.football_club.TicketSales.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDTO {
    private Long seatId;
    private Long ticketTypeId;
}
