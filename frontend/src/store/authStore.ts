import { create } from "zustand";

const TOKEN_KEY = "sentinel_token";
const ACCOUNT_KEY = "sentinel_account";

interface AuthState {
  token: string | null;
  accountId: string | null;
  isAuthenticated: boolean;
  setAuth: (token: string, accountId: string) => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem(TOKEN_KEY),
  accountId: localStorage.getItem(ACCOUNT_KEY),
  isAuthenticated: localStorage.getItem(TOKEN_KEY) !== null,

  setAuth: (token, accountId) => {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(ACCOUNT_KEY, accountId);
    set({ token, accountId, isAuthenticated: true });
  },

  clearAuth: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ACCOUNT_KEY);
    set({ token: null, accountId: null, isAuthenticated: false });
  },
}));
