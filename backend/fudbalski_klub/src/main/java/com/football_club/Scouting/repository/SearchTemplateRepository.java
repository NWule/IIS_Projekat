package com.football_club.Scouting.repository;

import com.football_club.Scouting.model.SearchTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchTemplateRepository extends JpaRepository<SearchTemplate, Long> {
    List<SearchTemplate> findByCreatorId(Long creatorId);
}