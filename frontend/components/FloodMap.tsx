"use client";

import mapboxgl from "mapbox-gl";
import { useEffect, useRef, useCallback } from "react";
import { getAllZones } from "@/services/floodApi";
import type { FloodZoneDto, PropertyDto } from "@/types";

const RISK_COLORS: Record<string, string> = {
  HIGH: "#ef4444",
  MEDIUM: "#f59e0b",
  LOW: "#22c55e",
};

interface FloodMapProps {
  selectedProperty: PropertyDto | null;
  onMapClick: (lat: number, lon: number) => void;
}

export default function FloodMap({
  selectedProperty,
  onMapClick,
}: FloodMapProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<mapboxgl.Map | null>(null);
  const markerRef = useRef<mapboxgl.Marker | null>(null);

  const addFloodZones = useCallback(async (map: mapboxgl.Map) => {
    try {
      const zones: FloodZoneDto[] = await getAllZones();

      const features = zones.map((zone) => {
        const parsed = JSON.parse(zone.geojson);
        return {
          ...parsed,
          properties: {
            ...parsed.properties,
            id: zone.id,
            zone_name: zone.zoneName,
            risk_level: zone.riskLevel,
          },
        };
      });

      const featureCollection: GeoJSON.FeatureCollection = {
        type: "FeatureCollection",
        features,
      };

      map.addSource("flood-zones", {
        type: "geojson",
        data: featureCollection,
      });

      map.addLayer({
        id: "flood-zones-fill",
        type: "fill",
        source: "flood-zones",
        paint: {
          "fill-color": [
            "match",
            ["get", "risk_level"],
            "HIGH",
            RISK_COLORS.HIGH,
            "MEDIUM",
            RISK_COLORS.MEDIUM,
            "LOW",
            RISK_COLORS.LOW,
            "#6b7280",
          ],
          "fill-opacity": 0.35,
        },
      });

      map.addLayer({
        id: "flood-zones-outline",
        type: "line",
        source: "flood-zones",
        paint: {
          "line-color": [
            "match",
            ["get", "risk_level"],
            "HIGH",
            RISK_COLORS.HIGH,
            "MEDIUM",
            RISK_COLORS.MEDIUM,
            "LOW",
            RISK_COLORS.LOW,
            "#6b7280",
          ],
          "line-width": 2,
        },
      });

      // Hover popup for zone name
      const popup = new mapboxgl.Popup({
        closeButton: false,
        closeOnClick: false,
      });

      map.on("mouseenter", "flood-zones-fill", (e) => {
        map.getCanvas().style.cursor = "pointer";
        if (e.features && e.features.length > 0) {
          const props = e.features[0].properties;
          if (props) {
            popup
              .setLngLat(e.lngLat)
              .setHTML(
                `<strong>${props.zone_name}</strong><br/>Risk: ${props.risk_level}`
              )
              .addTo(map);
          }
        }
      });

      map.on("mouseleave", "flood-zones-fill", () => {
        map.getCanvas().style.cursor = "";
        popup.remove();
      });
    } catch {
      console.error("Failed to load flood zones");
    }
  }, []);

  useEffect(() => {
    if (!containerRef.current) return;

    mapboxgl.accessToken =
      process.env.NEXT_PUBLIC_MAPBOX_TOKEN || "YOUR_MAPBOX_TOKEN_HERE";

    const map = new mapboxgl.Map({
      container: containerRef.current,
      style: "mapbox://styles/mapbox/light-v11",
      center: [73.0479, 33.6844],
      zoom: 11,
    });

    map.addControl(new mapboxgl.NavigationControl(), "top-right");

    map.on("load", () => {
      addFloodZones(map);
    });

    map.on("click", (e) => {
      const { lng, lat } = e.lngLat;

      if (markerRef.current) {
        markerRef.current.remove();
      }

      markerRef.current = new mapboxgl.Marker({ color: "#3b82f6" })
        .setLngLat([lng, lat])
        .addTo(map);

      onMapClick(lat, lng);
    });

    mapRef.current = map;

    return () => {
      map.remove();
    };
  }, [addFloodZones, onMapClick]);

  // Fly to selected property
  useEffect(() => {
    if (!selectedProperty || !mapRef.current) return;

    const map = mapRef.current;
    const { longitude, latitude } = selectedProperty;

    map.flyTo({
      center: [longitude, latitude],
      zoom: 14,
      duration: 1500,
    });

    if (markerRef.current) {
      markerRef.current.remove();
    }

    markerRef.current = new mapboxgl.Marker({ color: "#3b82f6" })
      .setLngLat([longitude, latitude])
      .addTo(map);
  }, [selectedProperty]);

  return (
    <div
      ref={containerRef}
      className="w-full h-full rounded-lg overflow-hidden border border-gray-200"
    />
  );
}
