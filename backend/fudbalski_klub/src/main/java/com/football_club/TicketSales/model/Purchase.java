package com.football_club.TicketSales.model;

import com.football_club.Auth.model.User;
import com.football_club.TicketSales.model.enums.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(name = "buyer_first_name", nullable = false)
    private String buyerFirstName;

    @Column(name = "buyer_last_name", nullable = false)
    private String buyerLastName;

    @Column(name = "buyer_email", nullable = false)
    private String buyerEmail;

    @Column(name = "card_holder_name", nullable = false)
    private String cardHolderName;

    @Column(name = "card_last_four", nullable = false, length = 4)
    private String cardLastFour;

    @Column(name = "card_expiry", nullable = false, length = 7)
    private String cardExpiry;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PurchaseStatus status;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;
}
