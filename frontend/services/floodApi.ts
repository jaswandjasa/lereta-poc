import axios from "axios";
import type {
  BulkFloodSummary,
  CertificateVerificationDto,
  FloodAlertDto,
  FloodResponse,
  FloodZoneDto,
  MonitoringStatusDto,
  NearestZoneResponse,
  PortfolioDashboardDto,
  PropertyComparisonDto,
  PropertyDto,
  RiskHistoryDto,
  ZoneImportResult,
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

export async function verifyCertificate(certNumber: string): Promise<CertificateVerificationDto> {
  const res = await api.get<CertificateVerificationDto>(`/api/certificate/verify/${certNumber}`);
  return res.data;
}

export async function downloadCertificate(propertyId: number): Promise<Blob> {
  const res = await api.get(`/api/certificate/${propertyId}`, {
    responseType: "blob",
  });
  return res.data;
}

export async function bulkFloodCheck(file: File): Promise<BulkFloodSummary> {
  const formData = new FormData();
  formData.append("file", file);
  const res = await api.post<BulkFloodSummary>("/api/flood/bulk-check", formData, {
    headers: { "Content-Type": "multipart/form-data" },
    timeout: 60000,
  });
  return res.data;
}

export async function compareProperties(id1: number, id2: number): Promise<PropertyComparisonDto> {
  const res = await api.get<PropertyComparisonDto>("/api/properties/compare", {
    params: { id1, id2 },
  });
  return res.data;
}

export async function getRiskHistory(propertyId: number): Promise<RiskHistoryDto[]> {
  const res = await api.get<RiskHistoryDto[]>(`/api/history/${propertyId}`);
  return res.data;
}

export async function getFloodBuffer(lat: number, lon: number): Promise<{
  geojson: string | null;
  bufferHigh: string | null;
  bufferMedium: string | null;
  bufferLow: string | null;
}> {
  const res = await api.get("/api/flood/buffer", {
    params: { lat, lon },
  });
  return res.data;
}

export async function getPortfolioDashboard(): Promise<PortfolioDashboardDto> {
  const res = await api.get<PortfolioDashboardDto>("/api/dashboard/portfolio");
  return res.data;
}

export async function importGeoJson(file: File): Promise<ZoneImportResult> {
  const formData = new FormData();
  formData.append("file", file);
  const res = await api.post<ZoneImportResult>("/api/zones/import", formData, {
    headers: { "Content-Type": "multipart/form-data" },
    timeout: 30000,
  });
  return res.data;
}

export async function getAlerts(): Promise<FloodAlertDto[]> {
  const res = await api.get<FloodAlertDto[]>("/api/alerts");
  return res.data;
}

export async function acknowledgeAlert(id: number): Promise<void> {
  await api.patch(`/api/alerts/${id}/acknowledge`);
}

export async function getMonitoringStatus(): Promise<MonitoringStatusDto[]> {
  const res = await api.get<MonitoringStatusDto[]>("/api/monitoring");
  return res.data;
}

export async function triggerMonitoring(): Promise<{ status: string; changesDetected: number }> {
  const res = await api.get<{ status: string; changesDetected: number }>("/api/monitoring/run");
  return res.data;
}
