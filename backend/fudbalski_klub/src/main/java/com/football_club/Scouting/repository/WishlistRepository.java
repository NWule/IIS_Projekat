package com.football_club.Scouting.repository;

import com.football_club.Scouting.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findWishlistByDirectorId(Long directorId);
}
