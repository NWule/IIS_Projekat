export interface PriceChangeLogDTO {
  id: number;
  gameId: number;
  gameLabel: string;
  zoneId: number;
  zoneName: string;
  ruleId: number;
  ruleName: string;
  oldPrice: number;
  newPrice: number;
  priceDiff: number;
  changedAt: string;
}
