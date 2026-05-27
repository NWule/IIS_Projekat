package com.football_club.Scouting.repository;

import com.football_club.Scouting.model.ScoutRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.football_club.Scouting.model.enums.RequestStatus;
import java.util.Optional;

@Repository
public interface ScoutRequestRepository extends JpaRepository<ScoutRequest, Long> {
    List<ScoutRequest> findByScoutIdIsNull();
    List<ScoutRequest> findByScoutId(Long scoutId);
    List<ScoutRequest> findByDirectorId(Long directorId);
    Optional<ScoutRequest> findByScoutIdAndPlayerIdAndStatusIn(
            Long scoutId, 
            Long playerId, 
            List<RequestStatus> statuses
    );
}
