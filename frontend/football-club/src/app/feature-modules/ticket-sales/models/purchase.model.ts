export interface PurchaseItemDTO {
  seatId: number;
  ticketTypeId: number;
}

export interface PurchaseRequestDTO {
  gameId: number;
  buyerFirstName: string;
  buyerLastName: string;
  buyerEmail: string;
  cardHolderName: string;
  cardNumber: string;
  cardExpiry: string;
  cvv: string;
  items: PurchaseItemDTO[];
}

export interface TicketDTO {
  id: number;
  gameId: number;
  homeClubName: string;
  awayClubName: string;
  matchDate: string;
  seatId: number;
  rowNumber: number;
  seatNumber: number;
  zoneName: string;
  ticketTypeName: string;
  price: number;
  ticketCode: string;
}

export interface PurchaseResponseDTO {
  id: number;
  buyerFirstName: string;
  buyerLastName: string;
  buyerEmail: string;
  cardHolderName: string;
  cardLastFour: string;
  totalAmount: number;
  transactionDate: string;
  status: string;
  tickets: TicketDTO[];
}
