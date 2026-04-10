import { create } from "zustand";
import type { Trade } from "@/types/trade";

interface TradeState {
  recentTrades: Trade[];
  addTrade: (trade: Trade) => void;
}

export const useTradeStore = create<TradeState>((set) => ({
  recentTrades: [],

  addTrade: (trade) =>
    set((state) => ({
      recentTrades: [trade, ...state.recentTrades].slice(0, 50),
    })),
}));
