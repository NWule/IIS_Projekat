package com.football_club.Scouting.repository;

import com.football_club.MatchTracking.model.enums.PlayerPosition;
import com.football_club.Scouting.model.MetricContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MetricContextRepository extends JpaRepository<MetricContext, Long> {
    List<MetricContext> findByPositionAndMetricIdIn(PlayerPosition position, Set<Long> metricIds);
}
