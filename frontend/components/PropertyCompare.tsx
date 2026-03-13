"use client";

import { useState, useEffect } from "react";
import { GitCompareArrows, Loader2 } from "lucide-react";
import { getAllProperties, compareProperties } from "@/services/floodApi";
import type { PropertyDto, PropertyComparisonDto, PropertySnapshot } from "@/types";

const riskColor: Record<string, string> = {
  HIGH: "text-red-600 bg-red-100",
  MEDIUM: "text-amber-600 bg-amber-100",
  LOW: "text-green-600 bg-green-100",
};

export default function PropertyCompare() {
  const [properties, setProperties] = useState<PropertyDto[]>([]);
  const [id1, setId1] = useState<number | "">("");
  const [id2, setId2] = useState<number | "">("");
  const [result, setResult] = useState<PropertyComparisonDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getAllProperties().then(setProperties).catch(() => {});
  }, []);

  const handleCompare = async () => {
    if (id1 === "" || id2 === "" || id1 === id2) return;
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const data = await compareProperties(Number(id1), Number(id2));
      setResult(data);
    } catch {
      setError("Comparison failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col gap-2">
      <div className="flex gap-1.5">
        <select
          value={id1}
          onChange={(e) => setId1(e.target.value ? Number(e.target.value) : "")}
          className="flex-1 px-2 py-1.5 text-xs border border-gray-300 rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-500"
        >
          <option value="">Property 1</option>
          {properties.map((p) => (
            <option key={p.id} value={p.id}>{p.propertyName}</option>
          ))}
        </select>
        <select
          value={id2}
          onChange={(e) => setId2(e.target.value ? Number(e.target.value) : "")}
          className="flex-1 px-2 py-1.5 text-xs border border-gray-300 rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-500"
        >
          <option value="">Property 2</option>
          {properties.map((p) => (
            <option key={p.id} value={p.id}>{p.propertyName}</option>
          ))}
        </select>
      </div>
      <button
        onClick={handleCompare}
        disabled={loading || id1 === "" || id2 === "" || id1 === id2}
        className="flex items-center justify-center gap-1.5 w-full py-2 px-4 bg-violet-600 text-white text-xs font-medium rounded-lg hover:bg-violet-700 disabled:opacity-50 transition"
      >
        {loading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <GitCompareArrows className="w-3.5 h-3.5" />}
        {loading ? "Comparing..." : "Compare"}
      </button>

      {id1 !== "" && id2 !== "" && id1 === id2 && (
        <p className="text-[10px] text-amber-600 text-center">Select two different properties</p>
      )}

      {error && <p className="text-xs text-red-600 text-center">{error}</p>}

      {result && (
        <div className="grid grid-cols-2 gap-2 text-[10px]">
          <SnapshotCard snap={result.property1} />
          <SnapshotCard snap={result.property2} />
        </div>
      )}
    </div>
  );
}

function SnapshotCard({ snap }: { snap: PropertySnapshot }) {
  return (
    <div className="p-2 rounded-lg border border-gray-200 bg-gray-50 space-y-1">
      <p className="font-semibold text-gray-800 truncate">{snap.propertyName}</p>
      <p>
        Risk: <span className={`px-1 py-0.5 rounded font-semibold ${riskColor[snap.riskLevel] ?? "text-gray-500 bg-gray-100"}`}>{snap.riskLevel}</span>
      </p>
      <p className="text-gray-600 truncate">Zone: {snap.nearestZone}</p>
      <p className="text-gray-600">Certs: {snap.certificateCount}</p>
      <p className="text-gray-600">Active Alerts: {snap.activeAlerts}</p>
    </div>
  );
}
