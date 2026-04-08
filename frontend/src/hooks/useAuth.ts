import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import * as api from "@/lib/api";
import { useAuthStore } from "@/store/authStore";

export const useLogin = () => {
  const setAuth = useAuthStore((s) => s.setAuth);

  const mutation = useMutation({
    mutationFn: ({ accountId, password }: { accountId: string; password: string }) =>
      api.login(accountId, password),
    onSuccess: (data, variables) => {
      setAuth(data.token, variables.accountId);
    },
  });

  return {
    login: (accountId: string, password: string) =>
      mutation.mutateAsync({ accountId, password }),
    isPending: mutation.isPending,
    error: mutation.error,
  };
};

export const useLogout = () => {
  const clearAuth = useAuthStore((s) => s.clearAuth);
  const navigate = useNavigate();

  return () => {
    clearAuth();
    navigate("/login");
  };
};
