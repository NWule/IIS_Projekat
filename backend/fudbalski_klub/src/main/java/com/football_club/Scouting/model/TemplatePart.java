package com.football_club.Scouting.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "template_parts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"search_template_id", "metric_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplatePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_template_id", nullable = false)
    private SearchTemplate searchTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_id", nullable = false)
    private Metric metric;

    @Column(name = "weight", nullable = false)
    private double weight;
}
