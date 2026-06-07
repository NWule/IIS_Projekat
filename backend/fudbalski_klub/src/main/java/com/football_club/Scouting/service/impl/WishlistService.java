package com.football_club.Scouting.service.impl;

import com.football_club.Auth.model.User;
import com.football_club.Auth.repository.UserRepository;
import com.football_club.MatchTracking.dto.PlayerDTO;
import com.football_club.MatchTracking.model.Player;
import com.football_club.MatchTracking.repository.PlayerRepository;
import com.football_club.Scouting.dto.WishlistDTO;
import com.football_club.Scouting.dto.WishlistSaveDTO;
import com.football_club.Scouting.model.Wishlist;
import com.football_club.Scouting.repository.WishlistRepository;
import com.football_club.Scouting.service.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService implements IWishlistService {
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;

    @Override
    @Transactional
    public WishlistDTO createWishList(Long directorId, WishlistSaveDTO dto) {
        User director = userRepository.findById(directorId).orElseThrow(() -> new RuntimeException("Director not found with id: " + directorId));
        Wishlist wishlist = new Wishlist();
        wishlist.setDirector(director);
        wishlist.setName(dto.getName());
        return mapToDTO(wishlistRepository.save(wishlist));
    }

    @Override
    @Transactional
    public WishlistDTO getWishListById(Long id) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + id));
        return mapToDTO(wishlist);
    }

    @Override
    @Transactional
    public WishlistDTO updateWishList(Long id, WishlistSaveDTO dto) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + id));
        wishlist.setName(dto.getName());
        return mapToDTO(wishlistRepository.save(wishlist));
    }

    @Override
    @Transactional
    public void deleteWishList(Long id) {
        wishlistRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<WishlistDTO> getDirectorWishLists(Long directorId) {
        return wishlistRepository.findWishlistByDirectorId(directorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WishlistDTO addPlayerToWishlist(Long playerId, Long wishlistId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId).orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + wishlistId));
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new RuntimeException("Player not found with id: " + playerId));
        if (wishlist.getPlayers().contains(player)) {
            throw new RuntimeException("Player is already in the wishlist");
        }
        wishlist.getPlayers().add(player);
        return mapToDTO(wishlistRepository.save(wishlist));
    }

    @Override
    @Transactional
    public WishlistDTO removePlayerFromWishlist(Long playerId, Long wishlistId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId).orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + wishlistId));
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new RuntimeException("Player not found with id: " + playerId));
        wishlist.getPlayers().remove(player);
        return mapToDTO(wishlistRepository.save(wishlist));
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
