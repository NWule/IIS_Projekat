export interface ZoneAnalyticsDTO {
  zoneId: number;
  zoneName: string;
  zoneColor: string;
  totalSeats: number;
  ticketsSold: number;
  occupancyPercent: number;
  revenue: number;
}

export interface TicketTypeAnalyticsDTO {
  typeName: string;
  ticketsSold: number;
  revenue: number;
}

export interface GameAnalyticsDTO {
  gameId: number;
  gameLabel: string;
  totalTicketsSold: number;
  totalRevenue: number;
  overallOccupancyPercent: number;
  priceChangeCount: number;
  zoneAnalytics: ZoneAnalyticsDTO[];
  ticketTypeAnalytics: TicketTypeAnalyticsDTO[];
}
