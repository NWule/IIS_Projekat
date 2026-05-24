package com.football_club.MatchTracking.repository;

import com.football_club.MatchTracking.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Integer> {

    Optional<Club> findByName(String name);

    List<Club> findByNameContainingIgnoreCase(String name);

    List<Club> findByLocationIgnoreCase(String location);

    List<Club> findAllByOrderByWinsDesc();

    List<Club> findAllByOrderByGoalsScoredDesc();
}