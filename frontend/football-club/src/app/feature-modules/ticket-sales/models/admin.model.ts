export interface GameZonePriceDTO {
  gameId: number;
  zoneId: number;
  zoneName: string;
  zoneColor: string;
  defaultPrice: number;
  customPrice?: number;
  effectivePrice: number;
}

export interface PricingRuleDTO {
  id?: number;
  name: string;
  occupancyThreshold?: number;
  occupancyConditionAtLeast: boolean;
  hoursBeforeMatch?: number;
  hoursConditionAtLeast: boolean;
  changePercent?: number;
  minPrice?: number;
  maxPrice?: number;
  active: boolean;
  gameIds: number[];
}

export interface ZoneDTO {
  id?: number;
  name: string;
  color: string;
  basePrice: number;
  numberOfRows: number;
  seatsPerRow: number;
}

export type SeatStatus = 'AVAILABLE' | 'RESERVED' | 'SOLD';

export interface SeatAdminDTO {
  id: number;
  rowNumber: number;
  seatNumber: number;
  status: SeatStatus;
  zoneId: number;
  zoneName: string;
}
