"use client";

import { useState, useRef } from "react";
import { MapPin, Upload, Loader2, CheckCircle } from "lucide-react";
import { importGeoJson } from "@/services/floodApi";
import type { ZoneImportResult } from "@/types";

export default function ZoneImportPanel() {
  const [result, setResult] = useState<ZoneImportResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const fileRef = useRef<HTMLInputElement>(null);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const data = await importGeoJson(file);
      setResult(data);
    } catch (err: any) {
      const msg = err?.response?.data?.message || "Import failed. Check GeoJSON format.";
      setError(msg);
    } finally {
      setLoading(false);
      if (fileRef.current) fileRef.current.value = "";
    }
  };

  return (
    <div className="flex flex-col gap-2">
      <label className="flex items-center justify-center gap-2 w-full py-2.5 px-4 bg-teal-600 text-white text-sm font-medium rounded-lg hover:bg-teal-700 cursor-pointer transition">
        {loading ? (
          <Loader2 className="w-4 h-4 animate-spin" />
        ) : (
          <Upload className="w-4 h-4" />
        )}
        {loading ? "Importing..." : "Import GeoJSON"}
        <input
          ref={fileRef}
          type="file"
          accept=".geojson,.json"
          onChange={handleUpload}
          disabled={loading}
          className="hidden"
        />
      </label>

      <p className="text-[10px] text-gray-400 text-center">
        Polygon features with zone_name + risk_level
      </p>

      {error && (
        <p className="text-xs text-red-600 bg-red-50 rounded px-2 py-1 text-center">
          {error}
        </p>
      )}

      {result && (
        <div className="flex items-center gap-2 p-2 rounded-lg border border-green-300 bg-green-50 text-xs">
          <CheckCircle className="w-4 h-4 text-green-600 shrink-0" />
          <div>
            <p className="font-medium text-green-800">
              {result.zonesImported} zone{result.zonesImported !== 1 ? "s" : ""} imported
            </p>
            <p className="text-[10px] text-green-600">
              Reload map to see new zones
            </p>
          </div>
        </div>
      )}
    </div>
  );
}
