import { useState } from "react";
import { Search, Loader2, AlertCircle } from "lucide-react";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useGetTrade } from "@/hooks/useTrades";
import StatusBadge from "@/components/StatusBadge";

export default function TradeLookupPage() {
  const [searchId, setSearchId] = useState("");
  const [submittedId, setSubmittedId] = useState("");

  const { data, isLoading, isError, isSuccess } = useGetTrade(submittedId);

  const handleSearch = () => {
    setSubmittedId(searchId.trim());
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleSearch();
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-zinc-100">Trade Lookup</h1>
        <p className="text-zinc-400 text-sm">Find any trade by its UUID</p>
      </div>

      <Card className="bg-zinc-900 border-zinc-800">
        <CardContent className="pt-6">
          <div className="flex gap-2">
            <Input
              value={searchId}
              onChange={(e) => setSearchId(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Enter trade UUID..."
              className="flex-1 bg-zinc-800 border-zinc-700 text-zinc-100 placeholder:text-zinc-600"
            />
            <Button
              onClick={handleSearch}
              className="bg-violet-600 hover:bg-violet-700"
            >
              <Search className="size-4" />
            </Button>
          </div>
        </CardContent>
      </Card>

      {submittedId && (
        <div>
          {isLoading && (
            <div className="flex justify-center py-12">
              <Loader2 className="size-8 text-violet-500 animate-spin" />
            </div>
          )}

          {(isError || (isSuccess && !data)) && (
            <Card className="bg-zinc-900 border-zinc-800">
              <CardContent className="flex flex-col items-center justify-center py-12 gap-2">
                <AlertCircle className="size-8 text-red-400" />
                <p className="text-zinc-300 font-medium">Trade not found</p>
                <p className="text-zinc-500 text-xs font-mono">{submittedId}</p>
              </CardContent>
            </Card>
          )}

          {isSuccess && data && (
            <Card className="bg-zinc-900 border-zinc-800">
              <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-zinc-100">Trade Details</CardTitle>
                <StatusBadge status={data.status} />
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 gap-x-8 gap-y-4">
                  <div>
                    <p className="text-zinc-500 text-sm">Trade ID</p>
                    <p className="text-zinc-100 text-sm font-medium font-mono text-xs break-all">
                      {data.id}
                    </p>
                  </div>
                  <div>
                    <p className="text-zinc-500 text-sm">Account ID</p>
                    <p className="text-zinc-100 text-sm font-medium">
                      {data.accountId.slice(0, 4)}****
                    </p>
                  </div>
                  <div>
                    <p className="text-zinc-500 text-sm">Instrument</p>
                    <p className="text-zinc-100 text-sm font-medium">
                      {data.instrumentId}
                    </p>
                  </div>
                  <div>
                    <p className="text-zinc-500 text-sm">Quantity</p>
                    <p className="text-zinc-100 text-sm font-medium">
                      {data.quantity}
                    </p>
                  </div>
                  <div>
                    <p className="text-zinc-500 text-sm">Price</p>
                    <p className="text-zinc-100 text-sm font-medium">
                      ${data.price}
                    </p>
                  </div>
                  <div>
                    <p className="text-zinc-500 text-sm">Type</p>
                    <p className="text-sm font-medium">
                      {data.type === "BUY" ? (
                        <span className="text-green-400">BUY</span>
                      ) : (
                        <span className="text-red-400">SELL</span>
                      )}
                    </p>
                  </div>
                  <div>
                    <p className="text-zinc-500 text-sm">Status</p>
                    <div className="mt-0.5">
                      <StatusBadge status={data.status} />
                    </div>
                  </div>
                  <div>
                    <p className="text-zinc-500 text-sm">Executed At</p>
                    <p className="text-zinc-100 text-sm font-medium">
                      {new Date(data.executedAt).toLocaleString()}
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      )}
    </div>
  );
}
