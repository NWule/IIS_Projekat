import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WishlistService } from '../../services/wishlist.service';
import { Wishlist } from '../../models/wishlist.model';

@Component({
  selector: 'app-wishlists',
  templateUrl: './wishlists.component.html',
  styleUrls: ['./wishlists.component.css']
})
export class WishlistsComponent implements OnInit {
  wishlists: Wishlist[] = [];
  isLoading = true;

  isModalOpen = false;
  isSubmitting = false;
  wishlistForm!: FormGroup;
  editingWishlistId: number | null = null;

  constructor(
    private wishlistService: WishlistService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.wishlistForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]]
    });
    this.loadWishlists();
  }

  loadWishlists(): void {
    this.isLoading = true;
    this.wishlistService.getMyWishlists().subscribe({
      next: (data) => {
        this.wishlists = data;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  openCreateModal(): void {
    this.editingWishlistId = null;
    this.wishlistForm.reset();
    this.isModalOpen = true;
  }

  openEditModal(wishlist: Wishlist): void {
    this.editingWishlistId = wishlist.id;
    this.wishlistForm.patchValue({ name: wishlist.name });
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.editingWishlistId = null;
  }

  onSubmit(): void {
    if (this.wishlistForm.invalid) return;
    this.isSubmitting = true;

    const payload = this.wishlistForm.value;
    const request = this.editingWishlistId 
      ? this.wishlistService.updateWishlist(this.editingWishlistId, payload)
      : this.wishlistService.createWishlist(payload);

    request.subscribe({
      next: () => {
        this.isSubmitting = false;
        this.closeModal();
        this.loadWishlists();
      },
      error: () => this.isSubmitting = false
    });
  }

  deleteWishlist(id: number): void {
    if (confirm('Da li ste sigurni da želite da obrišete ovu listu želja?')) {
      this.wishlistService.deleteWishlist(id).subscribe(() => {
        this.loadWishlists();
      });
    }
  }

  removePlayer(wishlistId: number, playerId: number | undefined): void {
    if (confirm('Da li ste sigurni da želite da uklonite ovog igrača iz liste želja?')) {
      this.wishlistService.removePlayerFromWishlist(wishlistId, playerId).subscribe({
        next: (updatedWishlist) => {
          this.wishlists = this.wishlists.map(list => list.id === wishlistId ? updatedWishlist : list);
        },
        error: (err) => {
          console.error('Greška pri uklanjanju igrača', err);
          alert('Došlo je do greške prilikom uklanjanja igrača.');
        }
      });
    }
  }
}