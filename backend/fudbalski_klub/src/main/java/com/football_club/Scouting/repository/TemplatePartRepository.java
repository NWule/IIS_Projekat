package com.football_club.Scouting.repository;

import com.football_club.Scouting.model.TemplatePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplatePartRepository extends JpaRepository<TemplatePart, Long> {
    List<TemplatePart> findBySearchTemplateId(Long searchTemplateId);
    Optional<TemplatePart> findBySearchTemplateIdAndMetricId(Long searchTemplateId, Long metricId);
    boolean existsBySearchTemplateIdAndMetricId(Long searchTemplateId, Long metricId);
}