import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import {
  TrendingUp,
  CheckCircle2,
  AlertTriangle,
  XCircle,
  Search,
} from "lucide-react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { useTradeStore } from "@/store/tradeStore";
import StatusBadge from "@/components/StatusBadge";

export default function DashboardPage() {
  const navigate = useNavigate();
  const recentTrades = useTradeStore((s) => s.recentTrades);

  const stats = useMemo(
    () => ({
      total: recentTrades.length,
      processed: recentTrades.filter((t) => t.status === "PROCESSED").length,
      flagged: recentTrades.filter((t) => t.status === "FLAGGED").length,
      rejected: recentTrades.filter((t) => t.status === "REJECTED").length,
    }),
    [recentTrades]
  );

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-zinc-100">Dashboard</h1>
        <p className="text-zinc-400 text-sm">Trade surveillance overview</p>
      </div>

      <div className="grid grid-cols-4 gap-4">
        <Card className="bg-zinc-900 border-zinc-800">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-zinc-400">
              Total Trades
            </CardTitle>
            <TrendingUp className="size-4 text-zinc-500" />
          </CardHeader>
          <CardContent>
            <p className="text-3xl font-bold text-zinc-100">{stats.total}</p>
          </CardContent>
        </Card>

        <Card className="bg-zinc-900 border-zinc-800">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-zinc-400">
              Processed
            </CardTitle>
            <CheckCircle2 className="size-4 text-zinc-500" />
          </CardHeader>
          <CardContent>
            <p className="text-3xl font-bold text-zinc-100">
              {stats.processed}
            </p>
          </CardContent>
        </Card>

        <Card className="bg-zinc-900 border-zinc-800">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-zinc-400">
              Flagged
            </CardTitle>
            <AlertTriangle className="size-4 text-zinc-500" />
          </CardHeader>
          <CardContent>
            <p className="text-3xl font-bold text-zinc-100">{stats.flagged}</p>
          </CardContent>
        </Card>

        <Card className="bg-zinc-900 border-zinc-800">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-zinc-400">
              Rejected
            </CardTitle>
            <XCircle className="size-4 text-zinc-500" />
          </CardHeader>
          <CardContent>
            <p className="text-3xl font-bold text-zinc-100">{stats.rejected}</p>
          </CardContent>
        </Card>
      </div>

      <Card className="bg-zinc-900 border-zinc-800">
        <CardHeader>
          <CardTitle className="text-zinc-100">Recent Trades</CardTitle>
          <CardDescription className="text-zinc-500">
            Last 50 submitted this session
          </CardDescription>
        </CardHeader>
        <CardContent>
          {recentTrades.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 gap-2">
              <Search className="size-8 text-zinc-600" />
              <p className="text-zinc-500">No trades yet</p>
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow className="border-zinc-800">
                  <TableHead className="text-zinc-500">ID</TableHead>
                  <TableHead className="text-zinc-500">Account</TableHead>
                  <TableHead className="text-zinc-500">Instrument</TableHead>
                  <TableHead className="text-zinc-500 text-right">Qty</TableHead>
                  <TableHead className="text-zinc-500 text-right">Price</TableHead>
                  <TableHead className="text-zinc-500">Type</TableHead>
                  <TableHead className="text-zinc-500">Status</TableHead>
                  <TableHead className="text-zinc-500">Time</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {recentTrades.map((trade) => (
                  <TableRow
                    key={trade.id}
                    className="border-zinc-800 hover:bg-zinc-800/40 transition-colors"
                  >
                    <TableCell className="text-zinc-400 font-mono text-xs">
                      {trade.id.slice(0, 8)}...
                    </TableCell>
                    <TableCell className="text-zinc-300">
                      {trade.accountId}
                    </TableCell>
                    <TableCell className="font-medium text-zinc-100">
                      {trade.instrumentId}
                    </TableCell>
                    <TableCell className="text-right text-zinc-300">
                      {trade.quantity}
                    </TableCell>
                    <TableCell className="text-right text-zinc-300">
                      ${trade.price}
                    </TableCell>
                    <TableCell>
                      {trade.type === "BUY" ? (
                        <span className="text-green-400 font-medium">BUY</span>
                      ) : (
                        <span className="text-red-400 font-medium">SELL</span>
                      )}
                    </TableCell>
                    <TableCell>
                      <StatusBadge status={trade.status} />
                    </TableCell>
                    <TableCell className="text-zinc-500 text-xs">
                      {new Date(trade.executedAt).toLocaleTimeString()}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
        <div className="px-6 pb-6">
          <Button
            onClick={() => navigate("/submit")}
            className="bg-violet-600 hover:bg-violet-700"
          >
            Submit New Trade
          </Button>
        </div>
      </Card>
    </div>
  );
}
