package com.football_club.Scouting.service.impl;

import com.football_club.MatchTracking.dto.PlayerDTO;
import com.football_club.MatchTracking.model.Player;
import com.football_club.Scouting.dto.WishlistDTO;
import com.football_club.Scouting.dto.WishlistSaveDTO;
import com.football_club.Scouting.model.Wishlist;
import com.football_club.Scouting.repository.WishlistRepository;
import com.football_club.Scouting.service.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService implements IWishlistService {
    private final WishlistRepository wishlistRepository;

    public WishlistDTO createWishList(WishlistSaveDTO dto) {
        Wishlist wishlist = new Wishlist();
        wishlist.setName(dto.getName());
        return mapToDTO(wishlistRepository.save(wishlist));
    }

    public WishlistDTO getWishListById(Long id) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + id));
        return mapToDTO(wishlist);
    }

    public WishlistDTO updateWishList(Long id, WishlistSaveDTO dto) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + id));
        wishlist.setName(dto.getName());
        return mapToDTO(wishlistRepository.save(wishlist));
    }

    public void deleteWishList(Long id) {
        wishlistRepository.deleteById(id);
    }

    public List<WishlistDTO> getDirectorWishLists(Long directorId) {
        return wishlistRepository.findWishlistByDirectorId(directorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private WishlistDTO mapToDTO(Wishlist wishlist) {
        return WishlistDTO.builder()
                .id(wishlist.getId())
                .name(wishlist.getName())
                .directorId(wishlist.getDirector() != null ? wishlist.getDirector().getId() : null)
                .players(wishlist.getPlayers() != null ?
                        wishlist.getPlayers().stream()
                                .map(this::mapPlayerToDTO)
                                .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    private PlayerDTO mapPlayerToDTO(Player player) {
        return PlayerDTO.builder()
                .id(player.getId())
                .name(player.getName())
                .surname(player.getSurname())
                .dateOfBirth(player.getDateOfBirth())
                .playerPosition(player.getPosition())
                .build();
    }
}
