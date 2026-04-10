import { Badge } from "@/components/ui/badge";
import type { TradeStatus } from "@/types/trade";

const statusStyles: Record<TradeStatus, string> = {
  PENDING: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30",
  PROCESSED: "bg-green-500/20 text-green-400 border-green-500/30",
  FLAGGED: "bg-red-500/20 text-red-400 border-red-500/30",
  REJECTED: "bg-zinc-500/20 text-zinc-400 border-zinc-600",
};

const StatusBadge = ({ status }: { status: TradeStatus }) => (
  <Badge variant="outline" className={statusStyles[status]}>
    {status}
  </Badge>
);

export default StatusBadge;
