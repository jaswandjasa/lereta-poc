export interface FloodZoneDto {
  id: number;
  zoneName: string;
  riskLevel: string;
  geojson: string;
}

export interface PropertyDto {
  id: number;
  propertyName: string;
  latitude: number;
  longitude: number;
}

export interface FloodResponse {
  zoneName: string;
  riskLevel: "LOW" | "MEDIUM" | "HIGH";
  insideFloodZone: boolean;
}

export interface NearestZoneResponse {
  zoneName: string;
  riskLevel: "LOW" | "MEDIUM" | "HIGH";
  insideFloodZone: boolean;
  nearestDistanceMeters: number;
}

export interface BulkFloodResult {
  propertyName: string;
  riskLevel: string | null;
  insideFloodZone: boolean;
  nearestZone: string | null;
  processed: boolean;
  errorMessage: string | null;
}

export interface BulkFloodSummary {
  totalRows: number;
  successRows: number;
  failedRows: number;
  results: BulkFloodResult[];
}

export interface PortfolioDashboardDto {
  highRiskCount: number;
  mediumRiskCount: number;
  lowRiskCount: number;
  alertsToday: number;
  todayCertificates: number;
  totalCertificates: number;
  monitoredChanged: number;
}

export interface ZoneImportResult {
  zonesImported: number;
  errors: string[];
}

export interface CertificateVerificationDto {
  certificateNumber: string;
  propertyName: string | null;
  riskLevel: string | null;
  zoneName: string | null;
  issuedAt: string | null;
  pdfHash: string | null;
  verificationStatus: "VALID" | "NOT_FOUND" | "INVALID";
}

export interface FloodAlertDto {
  id: number;
  propertyId: number;
  propertyName: string;
  oldRisk: string | null;
  newRisk: string | null;
  oldZone: string | null;
  newZone: string | null;
  alertType: string;
  createdAt: string;
  acknowledged: boolean;
  acknowledgedAt: string | null;
}

export interface MonitoringStatusDto {
  id: number;
  propertyId: number;
  propertyName: string;
  lastZone: string | null;
  currentZone: string | null;
  lastRisk: string | null;
  currentRisk: string | null;
  lastChecked: string | null;
  statusChanged: boolean;
  monitoringEnabled: boolean;
}
