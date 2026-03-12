"use client";

import { useState, useCallback } from "react";
import Header from "@/components/Header";
import PropertySearch from "@/components/PropertySearch";
import FloodMap from "@/components/FloodMap";
import RiskPanel from "@/components/RiskPanel";
import CertificateButton from "@/components/CertificateButton";
import CertificateVerifyPanel from "@/components/CertificateVerifyPanel";
import MonitoringPanel from "@/components/MonitoringPanel";
import BulkUpload from "@/components/BulkUpload";
import AlertPanel from "@/components/AlertPanel";
import ZoneImportPanel from "@/components/ZoneImportPanel";
import PortfolioDashboard from "@/components/PortfolioDashboard";
import { useFloodCheck } from "@/hooks/useFloodCheck";
import type { PropertyDto } from "@/types";

export default function Home() {
  const [selectedProperty, setSelectedProperty] = useState<PropertyDto | null>(
    null
  );
  const [coordinates, setCoordinates] = useState<{
    lat: number;
    lon: number;
  } | null>(null);

  const { result, loading, error, check } = useFloodCheck();

  const handleMapClick = useCallback(
    (lat: number, lon: number) => {
      setCoordinates({ lat, lon });
      setSelectedProperty(null);
      check(lat, lon);
    },
    [check]
  );

  const handlePropertySelect = useCallback(
    (property: PropertyDto) => {
      setSelectedProperty(property);
      setCoordinates({ lat: property.latitude, lon: property.longitude });
      check(property.latitude, property.longitude);
    },
    [check]
  );

  return (
    <div className="flex flex-col h-screen">
      <Header />

      <PortfolioDashboard />

      <main className="flex-1 flex overflow-hidden">
        {/* Sidebar */}
        <aside className="w-80 border-r border-gray-200 bg-white flex flex-col p-4 gap-4 overflow-y-auto">
          <div>
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
              Properties
            </h2>
            <PropertySearch onSelect={handlePropertySelect} />
          </div>

          <div>
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
              Flood Risk Assessment
            </h2>
            <RiskPanel
              result={result}
              loading={loading}
              error={error}
              coordinates={coordinates}
            />
          </div>

          <div>
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
              Flood Certificate
            </h2>
            <CertificateButton
              propertyId={selectedProperty?.id ?? null}
              propertyName={selectedProperty?.propertyName ?? null}
            />
          </div>

          <div>
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
              Verify Certificate
            </h2>
            <CertificateVerifyPanel />
          </div>

          <div>
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
              Import Flood Zones
            </h2>
            <ZoneImportPanel />
          </div>

          <div>
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
              Bulk Flood Check
            </h2>
            <BulkUpload />
          </div>

          <div>
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
              Flood Alerts
            </h2>
            <AlertPanel />
          </div>

          <div>
            <h2 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-3">
              Loan Monitoring
            </h2>
            <MonitoringPanel />
          </div>
        </aside>

        {/* Map */}
        <div className="flex-1">
          <FloodMap
            selectedProperty={selectedProperty}
            onMapClick={handleMapClick}
          />
        </div>
      </main>
    </div>
  );
}
