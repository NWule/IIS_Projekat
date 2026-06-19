package com.football_club.MatchTracking.repository.jpa;

import com.football_club.MatchTracking.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByNameAndSurname(String name, String surname);
    List<Player> findByIdIn(Collection<Long> ids);
    List<Player> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String nameQuery, String surnameQuery);
}
