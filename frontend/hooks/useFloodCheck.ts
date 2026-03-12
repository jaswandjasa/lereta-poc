"use client";

import { useState, useCallback } from "react";
import { checkFloodRisk } from "@/services/floodApi";
import type { FloodResponse } from "@/types";

export function useFloodCheck() {
  const [result, setResult] = useState<FloodResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const check = useCallback(async (lat: number, lon: number) => {
    setLoading(true);
    setError(null);
    try {
      const data = await checkFloodRisk(lat, lon);
      setResult(data);
    } catch (err: unknown) {
      const message =
        err instanceof Error ? err.message : "Failed to check flood risk";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setResult(null);
    setError(null);
  }, []);

  return { result, loading, error, check, reset };
}
