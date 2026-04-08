import axios from "axios";
import type { AuthTokenResponse, CreateTradeRequest, Trade } from "@/types/trade";

const TOKEN_KEY = "sentinel_token";

const apiClient = axios.create({
  baseURL: "/",
  headers: { "Content-Type": "application/json" },
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      localStorage.clear();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export const login = async (
  accountId: string,
  password: string
): Promise<AuthTokenResponse> => {
  const { data } = await apiClient.post<AuthTokenResponse>(
    "/api/v1/auth/token",
    { accountId, password }
  );
  return data;
};

export const createTrade = async (req: CreateTradeRequest): Promise<Trade> => {
  const { data } = await apiClient.post<Trade>("/api/v1/trades", req);
  return data;
};

export const getTradeById = async (id: string): Promise<Trade> => {
  const { data } = await apiClient.get<Trade>(`/api/v1/trades/${id}`);
  return data;
};
