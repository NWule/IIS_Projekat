package com.football_club.Scouting.controller;

import com.football_club.Auth.model.User;
import com.football_club.Scouting.dto.WishlistDTO;
import com.football_club.Scouting.dto.WishlistSaveDTO;
import com.football_club.Scouting.service.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final IWishlistService wishlistService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<WishlistDTO> createWishlist(@AuthenticationPrincipal User user, @RequestBody WishlistSaveDTO dto) {
        WishlistDTO created = wishlistService.createWishList(user.getId(), dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<WishlistDTO> getWishlistById(@PathVariable Long id) {
        WishlistDTO wishlist = wishlistService.getWishListById(id);
        return ResponseEntity.ok(wishlist);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<WishlistDTO> updateWishlist(@PathVariable Long id, @RequestBody WishlistSaveDTO dto) {
        WishlistDTO updated = wishlistService.updateWishList(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Long id) {
        wishlistService.deleteWishList(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{wishlistId}/players/{playerId}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<WishlistDTO> addPlayerToWishlist(
            @PathVariable Long wishlistId,
            @PathVariable Long playerId) {
        WishlistDTO updated = wishlistService.addPlayerToWishlist(playerId, wishlistId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{wishlistId}/players/{playerId}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<WishlistDTO> removePlayerFromWishlist(
            @PathVariable Long wishlistId,
            @PathVariable Long playerId) {
        WishlistDTO updated = wishlistService.removePlayerFromWishlist(playerId, wishlistId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<WishlistDTO>> getMyWishlists(@AuthenticationPrincipal User user) {
        List<WishlistDTO> wishlists = wishlistService.getDirectorWishLists(user.getId());
        return ResponseEntity.ok(wishlists);
    }

    @GetMapping("/director/{directorId}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<WishlistDTO>> getWishlistsByDirector(@PathVariable Long directorId) {
        List<WishlistDTO> wishlists = wishlistService.getDirectorWishLists(directorId);
        return ResponseEntity.ok(wishlists);
    }
}