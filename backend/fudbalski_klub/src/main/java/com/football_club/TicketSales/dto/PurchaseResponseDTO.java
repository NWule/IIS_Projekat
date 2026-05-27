package com.football_club.TicketSales.dto;

import com.football_club.TicketSales.model.enums.PurchaseStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponseDTO {
    private Long id;
    private String buyerFirstName;
    private String buyerLastName;
    private String buyerEmail;
    private String cardHolderName;
    private String cardLastFour;
    private BigDecimal totalAmount;
    private LocalDateTime transactionDate;
    private PurchaseStatus status;
    private List<TicketDTO> tickets;
}
