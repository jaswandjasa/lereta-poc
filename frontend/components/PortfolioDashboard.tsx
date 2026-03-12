"use client";

import { useState, useEffect } from "react";
import { BarChart3, ChevronDown, ChevronUp, AlertTriangle, FileCheck, Activity } from "lucide-react";
import { getPortfolioDashboard } from "@/services/floodApi";
import type { PortfolioDashboardDto } from "@/types";

export default function PortfolioDashboard() {
  const [data, setData] = useState<PortfolioDashboardDto | null>(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const d = await getPortfolioDashboard();
      setData(d);
    } catch {
      setData(null);
    } finally {
      setLoading(false);
    }
  };

  const toggle = () => {
    if (!open && !data) load();
    setOpen((o) => !o);
  };

  return (
    <div className="border-b border-gray-200 bg-white">
      <button
        onClick={toggle}
        className="w-full flex items-center justify-between px-4 py-2 text-xs font-semibold text-gray-600 hover:bg-gray-50 transition"
      >
        <span className="flex items-center gap-1.5">
          <BarChart3 className="w-3.5 h-3.5" />
          Portfolio Dashboard
        </span>
        {open ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
      </button>

      {open && (
        <div className="px-4 pb-3 pt-1">
          {loading && <p className="text-xs text-gray-400 text-center">Loading...</p>}
          {data && (
            <div className="grid grid-cols-7 gap-2">
              <Card label="HIGH" value={data.highRiskCount} color="bg-red-100 text-red-800 border-red-200" />
              <Card label="MEDIUM" value={data.mediumRiskCount} color="bg-amber-100 text-amber-800 border-amber-200" />
              <Card label="LOW" value={data.lowRiskCount} color="bg-green-100 text-green-800 border-green-200" />
              <Card label="Alerts Today" value={data.alertsToday} color="bg-orange-100 text-orange-800 border-orange-200" icon={<AlertTriangle className="w-3 h-3" />} />
              <Card label="Certs Today" value={data.todayCertificates} color="bg-blue-100 text-blue-800 border-blue-200" icon={<FileCheck className="w-3 h-3" />} />
              <Card label="Total Certs" value={data.totalCertificates} color="bg-indigo-100 text-indigo-800 border-indigo-200" icon={<FileCheck className="w-3 h-3" />} />
              <Card label="Changed" value={data.monitoredChanged} color="bg-purple-100 text-purple-800 border-purple-200" icon={<Activity className="w-3 h-3" />} />
            </div>
          )}
        </div>
      )}
    </div>
  );
}

function Card({ label, value, color, icon }: { label: string; value: number; color: string; icon?: React.ReactNode }) {
  return (
    <div className={`flex flex-col items-center justify-center p-2 rounded-lg border text-center ${color}`}>
      <div className="flex items-center gap-1">
        {icon}
        <span className="text-lg font-bold">{value}</span>
      </div>
      <span className="text-[9px] font-medium leading-tight">{label}</span>
    </div>
  );
}
