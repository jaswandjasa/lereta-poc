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
