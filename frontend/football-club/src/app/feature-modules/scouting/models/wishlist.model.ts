import { Player } from "../../match/models/player.model";

export interface Wishlist {
  id: number;
  name: string;
  directorId: number;
  players: Player[];
}

export interface WishlistSave {
  name: string;
}