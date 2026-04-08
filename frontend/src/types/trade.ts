export type TradeType = "BUY" | "SELL";

export type TradeStatus = "PENDING" | "PROCESSED" | "FLAGGED" | "REJECTED";

export interface Trade {
  id: string;
  accountId: string;
  instrumentId: string;
  quantity: string;
  price: string;
  type: TradeType;
  status: TradeStatus;
  executedAt: string;
}

export interface AuthTokenResponse {
  token: string;
  expiresIn: number;
}

export interface CreateTradeRequest {
  accountId: string;
  instrumentId: string;
  quantity: string;
  price: string;
  type: TradeType;
}
