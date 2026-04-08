import { useMutation, useQuery } from "@tanstack/react-query";
import * as api from "@/lib/api";
import type { CreateTradeRequest } from "@/types/trade";

export const useCreateTrade = () =>
  useMutation({
    mutationFn: (req: CreateTradeRequest) => api.createTrade(req),
  });

export const useGetTrade = (id: string) =>
  useQuery({
    queryKey: ["trade", id],
    queryFn: () => api.getTradeById(id),
    enabled: id.length > 0,
  });
