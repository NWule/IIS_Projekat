package com.football_club.TicketSales.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingRuleDTO {
    private Long id;
    private String name;
    private BigDecimal occupancyThreshold;
    private boolean occupancyConditionAtLeast;
    private Integer hoursBeforeMatch;
    private boolean hoursConditionAtLeast;
    private BigDecimal changePercent;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private boolean active;
    private List<Long> gameIds;
}
