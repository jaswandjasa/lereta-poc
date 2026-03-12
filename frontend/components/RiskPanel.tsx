"use client";

import { ShieldAlert, ShieldCheck, ShieldQuestion, Loader2 } from "lucide-react";
import type { FloodResponse } from "@/types";

interface RiskPanelProps {
  result: FloodResponse | null;
  loading: boolean;
  error: string | null;
  coordinates: { lat: number; lon: number } | null;
}

const riskConfig = {
  HIGH: {
    bg: "bg-red-50 border-red-200",
    text: "text-red-700",
    badge: "bg-red-100 text-red-800",
    icon: ShieldAlert,
    label: "High Risk",
  },
  MEDIUM: {
    bg: "bg-amber-50 border-amber-200",
    text: "text-amber-700",
    badge: "bg-amber-100 text-amber-800",
    icon: ShieldQuestion,
    label: "Medium Risk",
  },
  LOW: {
    bg: "bg-green-50 border-green-200",
    text: "text-green-700",
    badge: "bg-green-100 text-green-800",
    icon: ShieldCheck,
    label: "Low Risk",
  },
};

export default function RiskPanel({
  result,
  loading,
  error,
  coordinates,
}: RiskPanelProps) {
  if (loading) {
    return (
      <div className="flex items-center gap-2 p-4 bg-gray-50 border border-gray-200 rounded-lg">
        <Loader2 className="w-5 h-5 animate-spin text-blue-500" />
        <span className="text-sm text-gray-600">Evaluating flood risk...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
        <p className="text-sm text-red-700">{error}</p>
      </div>
    );
  }

  if (!result || !coordinates) {
    return (
      <div className="p-4 bg-gray-50 border border-gray-200 rounded-lg">
        <p className="text-sm text-gray-500">
          Click on the map or select a property to check flood risk.
        </p>
      </div>
    );
  }

  const config = riskConfig[result.riskLevel];
  const Icon = config.icon;

  return (
    <div className={`p-4 border rounded-lg ${config.bg}`}>
      <div className="flex items-center gap-2 mb-3">
        <Icon className={`w-5 h-5 ${config.text}`} />
        <span
          className={`text-xs font-semibold uppercase tracking-wider px-2 py-0.5 rounded-full ${config.badge}`}
        >
          {config.label}
        </span>
      </div>

      <div className="space-y-1.5">
        <div className="flex justify-between text-sm">
          <span className="text-gray-500">Zone</span>
          <span className={`font-medium ${config.text}`}>
            {result.zoneName}
          </span>
        </div>
        <div className="flex justify-between text-sm">
          <span className="text-gray-500">Inside Flood Zone</span>
          <span className={`font-medium ${config.text}`}>
            {result.insideFloodZone ? "Yes" : "No"}
          </span>
        </div>
        <div className="flex justify-between text-sm">
          <span className="text-gray-500">Coordinates</span>
          <span className="font-mono text-xs text-gray-600">
            {coordinates.lat.toFixed(5)}, {coordinates.lon.toFixed(5)}
          </span>
        </div>
      </div>
    </div>
  );
}
