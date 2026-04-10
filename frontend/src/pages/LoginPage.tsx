import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Shield, Loader2 } from "lucide-react";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useLogin } from "@/hooks/useAuth";

const schema = z.object({
  accountId: z.string().min(1, "Account ID is required"),
  password: z.string().min(1, "Password is required"),
});

type FormValues = z.infer<typeof schema>;

const LoginPage = () => {
  const navigate = useNavigate();
  const { login, isPending, error } = useLogin();

  const { register, handleSubmit, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (values: FormValues) => {
    try {
      await login(values.accountId, values.password);
      navigate("/");
    } catch {
      // error is surfaced via the `error` from useLogin
    }
  };

  return (
    <div className="bg-zinc-950 min-h-screen flex items-center justify-center">
      <Card className="bg-zinc-900 border-zinc-800 w-full max-w-sm">
        <CardHeader className="flex flex-col items-center gap-2 text-center">
          <Shield className="text-violet-500" size={32} />
          <CardTitle className="text-zinc-100">SentinelTrade</CardTitle>
          <CardDescription className="text-zinc-400">Surveillance Engine</CardDescription>
        </CardHeader>

        <CardContent>
          <form id="login-form" onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="accountId" className="text-zinc-300">Account ID</Label>
              <Input
                id="accountId"
                placeholder="ACC-001"
                className="bg-zinc-800 border-zinc-700 text-zinc-100 placeholder:text-zinc-500"
                {...register("accountId")}
              />
              {errors.accountId && (
                <p className="text-red-400 text-sm">{errors.accountId.message}</p>
              )}
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="password" className="text-zinc-300">Password</Label>
              <Input
                id="password"
                type="password"
                placeholder="••••••••"
                className="bg-zinc-800 border-zinc-700 text-zinc-100 placeholder:text-zinc-500"
                {...register("password")}
              />
              {errors.password && (
                <p className="text-red-400 text-sm">{errors.password.message}</p>
              )}
            </div>

            {error && (
              <p className="text-red-400 text-sm">
                {(error as Error).message || "Invalid credentials"}
              </p>
            )}
          </form>
        </CardContent>

        <CardFooter>
          <Button
            type="submit"
            form="login-form"
            disabled={isPending}
            className="w-full bg-violet-600 hover:bg-violet-700 text-white"
          >
            {isPending ? (
              <>
                <Loader2 className="animate-spin mr-2" size={16} />
                Signing in...
              </>
            ) : (
              "Sign In"
            )}
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
};

export default LoginPage;
