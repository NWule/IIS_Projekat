import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Wishlist, WishlistSave } from '../models/wishlist.model';
import { environment } from 'src/env/environment';

@Injectable({ providedIn: 'root' })
export class WishlistService {
  private baseWishlistUrl = `${environment.apiHost}wishlists`;

  constructor(private http: HttpClient) {}

  getMyWishlists(): Observable<Wishlist[]> {
    return this.http.get<Wishlist[]>(`${this.baseWishlistUrl}/my`);
  }

  createWishlist(dto: WishlistSave): Observable<Wishlist> {
    return this.http.post<Wishlist>(this.baseWishlistUrl, dto);
  }

  updateWishlist(id: number, dto: WishlistSave): Observable<Wishlist> {
    return this.http.put<Wishlist>(`${this.baseWishlistUrl}/${id}`, dto);
  }

  deleteWishlist(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseWishlistUrl}/${id}`);
  }

  removePlayerFromWishlist(wishlistId: number, playerId: number | undefined): Observable<Wishlist> {
    return this.http.delete<Wishlist>(`${this.baseWishlistUrl}/${wishlistId}/players/${playerId}`);
  }

  addPlayerToWishlist(wishlistId: number, playerId: number): Observable<any> {
    return this.http.patch<any>(`${this.baseWishlistUrl}/${wishlistId}/players/${playerId}`, {});
  }
}