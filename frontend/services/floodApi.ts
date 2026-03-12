import axios from "axios";
import type {
  FloodResponse,
  FloodZoneDto,
  NearestZoneResponse,
  PropertyDto,
} from "@/types";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080",
  timeout: 10000,
});

export async function getAllProperties(): Promise<PropertyDto[]> {
  const res = await api.get<PropertyDto[]>("/api/properties");
  return res.data;
}

export async function searchProperties(name: string): Promise<PropertyDto[]> {
  const res = await api.get<PropertyDto[]>("/api/properties", {
    params: { search: name },
  });
  return res.data;
}

export async function getAllZones(): Promise<FloodZoneDto[]> {
  const res = await api.get<FloodZoneDto[]>("/api/zones");
  return res.data;
}

export async function checkFloodRisk(
  lat: number,
  lon: number
): Promise<FloodResponse> {
  const res = await api.get<FloodResponse>("/api/flood/check", {
    params: { lat, lon },
  });
  return res.data;
}

export async function findNearestZone(
  lat: number,
  lon: number
): Promise<NearestZoneResponse> {
  const res = await api.get<NearestZoneResponse>("/api/flood/nearest", {
    params: { lat, lon },
  });
  return res.data;
}
