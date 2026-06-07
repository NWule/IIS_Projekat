package com.football_club.Scouting.service;

import com.football_club.Scouting.dto.WishlistDTO;
import com.football_club.Scouting.dto.WishlistSaveDTO;

import java.util.List;

public interface IWishlistService {
    WishlistDTO createWishList(Long directorId, WishlistSaveDTO dto);
    WishlistDTO getWishListById(Long id);
    WishlistDTO updateWishList(Long id, WishlistSaveDTO dto);
    void deleteWishList(Long id);
    List<WishlistDTO> getDirectorWishLists(Long directorId);
    WishlistDTO addPlayerToWishlist(Long playerId, Long wishlistId);
    WishlistDTO removePlayerFromWishlist(Long playerId, Long wishlistId);
}
