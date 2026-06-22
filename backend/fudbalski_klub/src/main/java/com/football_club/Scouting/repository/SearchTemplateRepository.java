package com.football_club.Scouting.repository;

import com.football_club.Scouting.model.SearchTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchTemplateRepository extends JpaRepository<SearchTemplate, Long> {
    List<SearchTemplate> findByCreatorId(Long creatorId);
    @Query("SELECT st FROM SearchTemplate st LEFT JOIN FETCH st.templateParts JOIN FETCH st.creator WHERE st.id = :id")
    Optional<SearchTemplate> findWithPartsById(@Param("id") Long id);
}