package com.football_club.Scouting.repository;

import com.football_club.Scouting.model.ValuedMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValuedMetricRepository extends JpaRepository<ValuedMetric, Long> {
    List<ValuedMetric> findByReportId(Long reportId);
    Optional<ValuedMetric> findByReportIdAndMetricId(Long reportId, Long metricId);
    boolean existsByReportIdAndMetricId(Long reportId, Long metricId);
}
