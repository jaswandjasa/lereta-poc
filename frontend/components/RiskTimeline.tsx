"use client";

import { useState, useEffect } from "react";
import { Clock, TrendingUp, TrendingDown, ArrowRightLeft } from "lucide-react";
import { getRiskHistory } from "@/services/floodApi";
import type { RiskHistoryDto } from "@/types";

const riskColor: Record<string, string> = {
  HIGH: "text-red-600 bg-red-100",
  MEDIUM: "text-amber-600 bg-amber-100",
  LOW: "text-green-600 bg-green-100",
};

function deriveIcon(oldRisk: string | null, newRisk: string | null) {
  const order: Record<string, number> = { LOW: 1, MEDIUM: 2, HIGH: 3 };
  const o = order[oldRisk ?? ""] ?? 0;
  const n = order[newRisk ?? ""] ?? 0;
  if (n > o) return <TrendingUp className="w-3 h-3 text-red-500" />;
  if (n < o) return <TrendingDown className="w-3 h-3 text-green-500" />;
  return <ArrowRightLeft className="w-3 h-3 text-amber-500" />;
}

interface RiskTimelineProps {
  propertyId: number | null;
}

export default function RiskTimeline({ propertyId }: RiskTimelineProps) {
  const [history, setHistory] = useState<RiskHistoryDto[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!propertyId) {
      setHistory([]);
      return;
    }
    setLoading(true);
    getRiskHistory(propertyId)
      .then(setHistory)
      .catch(() => setHistory([]))
      .finally(() => setLoading(false));
  }, [propertyId]);

  if (!propertyId) return null;
  if (loading) return <p className="text-xs text-gray-400">Loading history...</p>;
  if (history.length === 0) return <p className="text-xs text-gray-400">No risk changes recorded</p>;

  return (
    <div className="flex flex-col gap-1.5 max-h-40 overflow-y-auto">
      {history.map((h) => (
        <div
          key={h.id}
          className="flex items-start gap-2 p-1.5 rounded border border-gray-100 bg-gray-50 text-[10px]"
        >
          <div className="mt-0.5">{deriveIcon(h.oldRisk, h.newRisk)}</div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-1">
              <span className={`px-1 py-0.5 rounded font-semibold ${riskColor[h.oldRisk ?? ""] ?? "text-gray-500 bg-gray-100"}`}>
                {h.oldRisk ?? "—"}
              </span>
              <span className="text-gray-400">→</span>
              <span className={`px-1 py-0.5 rounded font-semibold ${riskColor[h.newRisk ?? ""] ?? "text-gray-500 bg-gray-100"}`}>
                {h.newRisk ?? "—"}
              </span>
            </div>
            {h.oldZone !== h.newZone && (
              <p className="text-gray-500 truncate mt-0.5">
                {h.oldZone} → {h.newZone}
              </p>
            )}
            <div className="flex items-center gap-1 text-gray-400 mt-0.5">
              <Clock className="w-2.5 h-2.5" />
              {new Date(h.changedAt).toLocaleString()}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
