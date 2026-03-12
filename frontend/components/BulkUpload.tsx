"use client";

import { useState, useRef } from "react";
import { Upload, FileSpreadsheet, Loader2, CheckCircle, XCircle } from "lucide-react";
import { bulkFloodCheck } from "@/services/floodApi";
import type { BulkFloodSummary } from "@/types";

const riskBadge: Record<string, string> = {
  HIGH: "bg-red-100 text-red-800",
  MEDIUM: "bg-amber-100 text-amber-800",
  LOW: "bg-green-100 text-green-800",
};

export default function BulkUpload() {
  const [summary, setSummary] = useState<BulkFloodSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const fileRef = useRef<HTMLInputElement>(null);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setLoading(true);
    setError(null);
    setSummary(null);

    try {
      const result = await bulkFloodCheck(file);
      setSummary(result);
    } catch {
      setError("Failed to process CSV. Check format: property_name,lat,lon");
    } finally {
      setLoading(false);
      if (fileRef.current) fileRef.current.value = "";
    }
  };

  return (
    <div className="flex flex-col gap-3">
      <label className="flex items-center justify-center gap-2 w-full py-2.5 px-4 bg-violet-600 text-white text-sm font-medium rounded-lg hover:bg-violet-700 cursor-pointer transition">
        {loading ? (
          <Loader2 className="w-4 h-4 animate-spin" />
        ) : (
          <Upload className="w-4 h-4" />
        )}
        {loading ? "Processing..." : "Upload CSV"}
        <input
          ref={fileRef}
          type="file"
          accept=".csv"
          onChange={handleUpload}
          disabled={loading}
          className="hidden"
        />
      </label>

      <p className="text-[10px] text-gray-400 text-center">
        Format: property_name, lat, lon (max 2 MB)
      </p>

      {error && (
        <p className="text-xs text-red-600 bg-red-50 rounded px-2 py-1 text-center">
          {error}
        </p>
      )}

      {summary && (
        <div className="flex flex-col gap-2">
          <div className="flex items-center justify-between text-xs bg-gray-50 rounded-lg p-2">
            <div className="flex items-center gap-1">
              <FileSpreadsheet className="w-3.5 h-3.5 text-gray-500" />
              <span className="text-gray-600">
                {summary.totalRows} row{summary.totalRows !== 1 ? "s" : ""}
              </span>
            </div>
            <div className="flex gap-2">
              <span className="text-green-700">{summary.successRows} ok</span>
              {summary.failedRows > 0 && (
                <span className="text-red-700">{summary.failedRows} failed</span>
              )}
            </div>
          </div>

          <div className="flex flex-col gap-1 max-h-48 overflow-y-auto">
            {summary.results.map((r, i) => (
              <div
                key={i}
                className={`flex items-center justify-between p-2 rounded border text-xs ${
                  r.processed
                    ? "border-gray-200 bg-white"
                    : "border-red-200 bg-red-50"
                }`}
              >
                <div className="flex items-center gap-1.5 min-w-0">
                  {r.processed ? (
                    <CheckCircle className="w-3 h-3 text-green-500 shrink-0" />
                  ) : (
                    <XCircle className="w-3 h-3 text-red-500 shrink-0" />
                  )}
                  <span className="truncate text-gray-700 max-w-[120px]">
                    {r.propertyName}
                  </span>
                </div>
                {r.processed && r.riskLevel ? (
                  <span
                    className={`px-1.5 py-0.5 rounded-full text-[10px] font-semibold ${
                      riskBadge[r.riskLevel] || "bg-gray-100 text-gray-600"
                    }`}
                  >
                    {r.riskLevel}
                  </span>
                ) : (
                  <span className="text-[10px] text-red-600 truncate max-w-[100px]">
                    {r.errorMessage}
                  </span>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
