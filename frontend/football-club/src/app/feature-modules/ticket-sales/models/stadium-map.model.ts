export type SeatStatus = 'AVAILABLE' | 'RESERVED' | 'SOLD' | 'OCCUPIED';

export interface SeatMapDTO {
  id: number;
  rowNumber: number;
  seatNumber: number;
  status: SeatStatus;
}

export interface ZoneMapDTO {
  id: number;
  name: string;
  color: string;
  basePrice: number;
  numberOfRows: number;
  seatsPerRow: number;
  seats: SeatMapDTO[];
}

export interface StadiumMapDTO {
  gameId: number;
  homeClubName: string;
  awayClubName: string;
  zones: ZoneMapDTO[];
}

export interface PricePreviewDTO {
  zoneId: number;
  zoneName: string;
  ticketTypeId: number;
  ticketTypeName: string;
  basePrice: number;
  priceModifier: number;
  finalPrice: number;
}

export interface TicketTypeDTO {
  id: number;
  name: string;
  description: string;
  priceModifier: number;
}

export interface SelectedSeat {
  seatId: number;
  rowNumber: number;
  seatNumber: number;
  zoneId: number;
  zoneName: string;
  basePrice: number;
}
