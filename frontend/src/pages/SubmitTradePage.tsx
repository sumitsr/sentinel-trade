import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Send, Loader2 } from "lucide-react";
import { toast } from "sonner";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useAuthStore } from "@/store/authStore";
import { useTradeStore } from "@/store/tradeStore";
import { useCreateTrade } from "@/hooks/useTrades";

const isPositiveNumber = (v: string) => {
  const n = parseFloat(v);
  return !isNaN(n) && n > 0;
};

const schema = z.object({
  accountId: z.string(),
  instrumentId: z.string().min(1, "Instrument ID is required"),
  quantity: z.string().refine(isPositiveNumber, "Quantity must be greater than 0"),
  price: z.string().refine(isPositiveNumber, "Price must be greater than 0"),
  type: z.enum(["BUY", "SELL"]),
});

type FormValues = z.infer<typeof schema>;

export default function SubmitTradePage() {
  const accountId = useAuthStore((s) => s.accountId) ?? "";
  const addTrade = useTradeStore((s) => s.addTrade);
  const { mutate, isPending } = useCreateTrade();

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    reset,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { accountId, type: "BUY" },
  });

  const typeValue = watch("type");

  const onSubmit = (data: FormValues) => {
    mutate(
      {
        accountId: data.accountId,
        instrumentId: data.instrumentId,
        quantity: data.quantity,
        price: data.price,
        type: data.type,
      },
      {
        onSuccess: (result) => {
          addTrade(result);
          toast.success("Trade submitted", {
            description: "ID: " + result.id.slice(0, 8),
          });
          reset({ accountId, type: "BUY" });
        },
        onError: (err) => {
          toast.error("Submission failed", { description: err.message });
        },
      }
    );
  };

  return (
    <div className="max-w-lg mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-zinc-100">Submit Trade</h1>
        <p className="text-zinc-400 text-sm">Execute a new trade order</p>
      </div>

      <Card className="bg-zinc-900 border-zinc-800">
        <CardHeader>
          <CardTitle className="text-zinc-100">Trade Details</CardTitle>
          <CardDescription className="text-zinc-500">
            Fill in the details for your trade order
          </CardDescription>
        </CardHeader>
        <form onSubmit={handleSubmit(onSubmit)}>
          <CardContent className="space-y-4">
            <div className="space-y-1.5">
              <Label className="text-zinc-300">Account ID</Label>
              <Input
                {...register("accountId")}
                readOnly
                className="bg-zinc-800/50 border-zinc-700 text-zinc-400 cursor-not-allowed"
              />
            </div>

            <div className="space-y-1.5">
              <Label className="text-zinc-300">Instrument ID</Label>
              <Input
                {...register("instrumentId")}
                placeholder="e.g. AAPL, BTC-USD"
                className="bg-zinc-800 border-zinc-700 text-zinc-100 placeholder:text-zinc-600"
              />
              {errors.instrumentId && (
                <p className="text-red-400 text-xs">
                  {errors.instrumentId.message}
                </p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="text-zinc-300">Quantity</Label>
              <Input
                {...register("quantity")}
                type="number"
                step="0.00000001"
                min="0.00000001"
                placeholder="0.00000001"
                className="bg-zinc-800 border-zinc-700 text-zinc-100 placeholder:text-zinc-600"
              />
              {errors.quantity && (
                <p className="text-red-400 text-xs">{errors.quantity.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="text-zinc-300">Price ($)</Label>
              <Input
                {...register("price")}
                type="number"
                step="0.01"
                min="0.01"
                placeholder="0.01"
                className="bg-zinc-800 border-zinc-700 text-zinc-100 placeholder:text-zinc-600"
              />
              {errors.price && (
                <p className="text-red-400 text-xs">{errors.price.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label className="text-zinc-300">Type</Label>
              <Select
                value={typeValue}
                onValueChange={(val) =>
                  setValue("type", val as "BUY" | "SELL", {
                    shouldValidate: true,
                  })
                }
              >
                <SelectTrigger className="bg-zinc-800 border-zinc-700 text-zinc-100">
                  <SelectValue placeholder="Select type" />
                </SelectTrigger>
                <SelectContent className="bg-zinc-800 border-zinc-700">
                  <SelectItem value="BUY" className="text-zinc-100">
                    BUY
                  </SelectItem>
                  <SelectItem value="SELL" className="text-zinc-100">
                    SELL
                  </SelectItem>
                </SelectContent>
              </Select>
              {errors.type && (
                <p className="text-red-400 text-xs">{errors.type.message}</p>
              )}
            </div>
          </CardContent>

          <CardFooter>
            <Button
              type="submit"
              disabled={isPending}
              className="w-full bg-violet-600 hover:bg-violet-700"
            >
              {isPending ? (
                <>
                  <Loader2 className="size-4 mr-2 animate-spin" />
                  Submitting...
                </>
              ) : (
                <>
                  <Send className="size-4 mr-2" />
                  Submit Trade
                </>
              )}
            </Button>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
}
