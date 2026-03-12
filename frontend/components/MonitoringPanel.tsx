"use client";

import { useState } from "react";
import { Activity, RefreshCw, AlertTriangle, CheckCircle } from "lucide-react";
import { getMonitoringStatus, triggerMonitoring } from "@/services/floodApi";
import type { MonitoringStatusDto } from "@/types";

const riskBadge: Record<string, string> = {
  HIGH: "bg-red-100 text-red-800",
  MEDIUM: "bg-amber-100 text-amber-800",
  LOW: "bg-green-100 text-green-800",
};

export default function MonitoringPanel() {
  const [records, setRecords] = useState<MonitoringStatusDto[]>([]);
  const [loaded, setLoaded] = useState(false);
  const [loading, setLoading] = useState(false);
  const [running, setRunning] = useState(false);
  const [runResult, setRunResult] = useState<string | null>(null);

  const loadData = async () => {
    setLoading(true);
    try {
      const data = await getMonitoringStatus();
      setRecords(data);
      setLoaded(true);
    } catch {
      setRecords([]);
    } finally {
      setLoading(false);
    }
  };

  const handleRunCycle = async () => {
    setRunning(true);
    setRunResult(null);
    try {
      const res = await triggerMonitoring();
      setRunResult(`Cycle complete — ${res.changesDetected} change(s) detected`);
      await loadData();
    } catch {
      setRunResult("Failed to run monitoring cycle");
    } finally {
      setRunning(false);
    }
  };

  if (!loaded) {
    return (
      <div className="flex flex-col gap-2">
        <button
          onClick={loadData}
          disabled={loading}
          className="flex items-center justify-center gap-2 w-full py-2.5 px-4 bg-indigo-600 text-white text-sm font-medium rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition"
        >
          <Activity className="w-4 h-4" />
          {loading ? "Loading..." : "Load Monitoring Data"}
        </button>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-3">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold text-gray-700">
          Monitored Properties
        </h3>
        <button
          onClick={handleRunCycle}
          disabled={running}
          className="flex items-center gap-1 text-xs text-indigo-600 hover:text-indigo-800 disabled:opacity-50"
        >
          <RefreshCw className={`w-3 h-3 ${running ? "animate-spin" : ""}`} />
          {running ? "Running..." : "Run Cycle"}
        </button>
      </div>

      {runResult && (
        <p className="text-xs text-indigo-600 bg-indigo-50 rounded px-2 py-1">
          {runResult}
        </p>
      )}

      <div className="flex flex-col gap-1.5 max-h-60 overflow-y-auto">
        {records.length === 0 && (
          <p className="text-xs text-gray-400 text-center py-2">
            No monitoring records
          </p>
        )}
        {records.map((r) => (
          <div
            key={r.id}
            className={`p-2.5 rounded-lg border text-xs ${
              r.statusChanged
                ? "border-amber-300 bg-amber-50"
                : "border-gray-200 bg-white"
            }`}
          >
            <div className="flex items-center justify-between mb-1">
              <span className="font-medium text-gray-800 truncate max-w-[160px]">
                {r.propertyName}
              </span>
              {r.statusChanged ? (
                <AlertTriangle className="w-3.5 h-3.5 text-amber-500 shrink-0" />
              ) : (
                <CheckCircle className="w-3.5 h-3.5 text-green-500 shrink-0" />
              )}
            </div>
            <div className="flex items-center gap-1.5">
              {r.currentRisk && (
                <span
                  className={`px-1.5 py-0.5 rounded-full text-[10px] font-semibold ${
                    riskBadge[r.currentRisk] || "bg-gray-100 text-gray-600"
                  }`}
                >
                  {r.currentRisk}
                </span>
              )}
              <span className="text-gray-500 truncate">
                {r.currentZone || "Not checked yet"}
              </span>
            </div>
            {r.statusChanged && r.lastZone && (
              <p className="text-[10px] text-amber-600 mt-1">
                Changed from: {r.lastZone} ({r.lastRisk})
              </p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
