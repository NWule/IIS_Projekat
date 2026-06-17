package com.football_club.Scouting.model;

import com.football_club.MatchTracking.model.enums.PlayerPosition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "metric_context", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"position", "metric_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetricContext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayerPosition position;

    @Column(name = "metric_id", nullable = false)
    private Long metricId;

    @Column(name = "min_value", nullable = false)
    private double minValue;

    @Column(name = "max_value", nullable = false)
    private double maxValue;

    public double normalize(double rawValue) {
        if (maxValue <= minValue) return 0.0;
        double normalized = ((rawValue - minValue) / (maxValue - minValue)) * 100.0;
        return Math.max(0.0, Math.min(100.0, normalized));
    }
}
