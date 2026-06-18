package com.football_club.TicketSales.model;

import com.football_club.MatchTracking.model.Game;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "occupancy_threshold", precision = 5, scale = 2)
    private BigDecimal occupancyThreshold;

    @Column(name = "occupancy_condition_at_least", nullable = false)
    private boolean occupancyConditionAtLeast = true;

    @Column(name = "hours_before_match")
    private Integer hoursBeforeMatch;

    @Column(name = "hours_condition_at_least", nullable = false)
    private boolean hoursConditionAtLeast = false;

    @Column(name = "change_percent", precision = 6, scale = 2)
    private BigDecimal changePercent;

    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice;

    @Column(name = "max_price", precision = 10, scale = 2)
    private BigDecimal maxPrice;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "pricing_rule_games",
        joinColumns = @JoinColumn(name = "rule_id"),
        inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> games = new ArrayList<>();
}
