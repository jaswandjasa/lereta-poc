"use client";

import { useState } from "react";
import { Bell, Check, AlertTriangle, TrendingUp, TrendingDown, ArrowRightLeft } from "lucide-react";
import { getAlerts, acknowledgeAlert } from "@/services/floodApi";
import type { FloodAlertDto } from "@/types";

const typeIcon: Record<string, React.ReactNode> = {
  RISK_INCREASE: <TrendingUp className="w-3.5 h-3.5 text-red-500" />,
  RISK_DECREASE: <TrendingDown className="w-3.5 h-3.5 text-green-500" />,
  ZONE_CHANGED: <ArrowRightLeft className="w-3.5 h-3.5 text-amber-500" />,
};

const typeBorder: Record<string, string> = {
  RISK_INCREASE: "border-red-300 bg-red-50",
  RISK_DECREASE: "border-green-300 bg-green-50",
  ZONE_CHANGED: "border-amber-300 bg-amber-50",
};

export default function AlertPanel() {
  const [alerts, setAlerts] = useState<FloodAlertDto[]>([]);
  const [loaded, setLoaded] = useState(false);
  const [loading, setLoading] = useState(false);

  const loadAlerts = async () => {
    setLoading(true);
    try {
      const data = await getAlerts();
      setAlerts(data);
      setLoaded(true);
    } catch {
      setAlerts([]);
    } finally {
      setLoading(false);
    }
  };

  const handleAcknowledge = async (id: number) => {
    try {
      await acknowledgeAlert(id);
      setAlerts((prev) =>
        prev.map((a) =>
          a.id === id ? { ...a, acknowledged: true, acknowledgedAt: new Date().toISOString() } : a
        )
      );
    } catch {
      // silent
    }
  };

  if (!loaded) {
    return (
      <button
        onClick={loadAlerts}
        disabled={loading}
        className="flex items-center justify-center gap-2 w-full py-2.5 px-4 bg-red-600 text-white text-sm font-medium rounded-lg hover:bg-red-700 disabled:opacity-50 transition"
      >
        <Bell className="w-4 h-4" />
        {loading ? "Loading..." : "Load Alerts"}
      </button>
    );
  }

  const active = alerts.filter((a) => !a.acknowledged);
  const acked = alerts.filter((a) => a.acknowledged);

  return (
    <div className="flex flex-col gap-2">
      {active.length > 0 && (
        <p className="text-xs text-red-600 font-semibold">
          {active.length} active alert{active.length !== 1 ? "s" : ""}
        </p>
      )}

      <div className="flex flex-col gap-1.5 max-h-48 overflow-y-auto">
        {alerts.length === 0 && (
          <p className="text-xs text-gray-400 text-center py-2">No alerts</p>
        )}
        {alerts.map((a) => (
          <div
            key={a.id}
            className={`p-2 rounded-lg border text-xs ${
              a.acknowledged
                ? "border-gray-200 bg-gray-50 opacity-60"
                : typeBorder[a.alertType] || "border-gray-200 bg-white"
            }`}
          >
            <div className="flex items-center justify-between mb-1">
              <div className="flex items-center gap-1.5">
                {typeIcon[a.alertType] || (
                  <AlertTriangle className="w-3.5 h-3.5 text-gray-400" />
                )}
                <span className="font-medium text-gray-800 truncate max-w-[130px]">
                  {a.propertyName}
                </span>
              </div>
              {!a.acknowledged && (
                <button
                  onClick={() => handleAcknowledge(a.id)}
                  className="flex items-center gap-0.5 text-[10px] text-indigo-600 hover:text-indigo-800"
                >
                  <Check className="w-3 h-3" />
                  Ack
                </button>
              )}
            </div>
            <div className="text-gray-500">
              {a.oldRisk} → {a.newRisk}
              {a.oldZone !== a.newZone && (
                <span className="ml-1">
                  ({a.oldZone} → {a.newZone})
                </span>
              )}
            </div>
          </div>
        ))}
      </div>

      <button
        onClick={loadAlerts}
        disabled={loading}
        className="text-xs text-indigo-600 hover:text-indigo-800 disabled:opacity-50"
      >
        {loading ? "Refreshing..." : "Refresh"}
      </button>
    </div>
  );
}
