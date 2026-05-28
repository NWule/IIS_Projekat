export interface GameSearchResult {
  id: number;
  matchDate: string;
  homeClubId: number;
  homeClubName: string;
  awayClubId: number;
  awayClubName: string;
  lowestPrice: number | null;
}

export interface GameSearchParams {
  opponent?: string;
  startDate?: string;
  endDate?: string;
  minPrice?: number;
  maxPrice?: number;
}
