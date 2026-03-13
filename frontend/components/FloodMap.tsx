"use client";

import mapboxgl from "mapbox-gl";
import { useEffect, useRef, useCallback } from "react";
import { getAllZones, getFloodBuffer } from "@/services/floodApi";
import type { FloodZoneDto, PropertyDto } from "@/types";
import MapLegend from "@/components/MapLegend";

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
  const bufferCacheRef = useRef<{ key: string; high: string | null; medium: string | null; low: string | null } | null>(null);

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

  const BUFFER_LAYERS = [
    { id: "buffer-low",    color: "#60a5fa", opacity: 0.10, outline: "#3b82f6" },  // 2500m — light blue
    { id: "buffer-medium", color: "#fbbf24", opacity: 0.14, outline: "#d97706" },  // 1500m — light yellow
    { id: "buffer-high",   color: "#f87171", opacity: 0.18, outline: "#dc2626" },  // 500m  — light red
  ];

  const showBuffer = useCallback(async (map: mapboxgl.Map, lat: number, lng: number) => {
    const cacheKey = `${lat.toFixed(6)},${lng.toFixed(6)}`;

    if (bufferCacheRef.current?.key === cacheKey) {
      renderBufferLayers(map, bufferCacheRef.current);
      return;
    }

    try {
      const res = await getFloodBuffer(lat, lng);
      if (res.bufferHigh || res.bufferMedium || res.bufferLow) {
        const cached = { key: cacheKey, high: res.bufferHigh, medium: res.bufferMedium, low: res.bufferLow };
        bufferCacheRef.current = cached;
        renderBufferLayers(map, cached);
      } else {
        removeBufferLayers(map);
      }
    } catch {
      removeBufferLayers(map);
    }
  }, []);

  const renderBufferLayers = (map: mapboxgl.Map, data: { high: string | null; medium: string | null; low: string | null }) => {
    const layers = [
      { geojson: data.low,    ...BUFFER_LAYERS[0] },
      { geojson: data.medium, ...BUFFER_LAYERS[1] },
      { geojson: data.high,   ...BUFFER_LAYERS[2] },
    ];

    for (const layer of layers) {
      const srcId = layer.id;
      const fillId = `${layer.id}-fill`;
      const outlineId = `${layer.id}-outline`;

      if (!layer.geojson) {
        if (map.getLayer(fillId)) map.removeLayer(fillId);
        if (map.getLayer(outlineId)) map.removeLayer(outlineId);
        if (map.getSource(srcId)) map.removeSource(srcId);
        continue;
      }

      const parsed = JSON.parse(layer.geojson);
      const fc: GeoJSON.FeatureCollection = { type: "FeatureCollection", features: [parsed] };

      if (map.getSource(srcId)) {
        (map.getSource(srcId) as mapboxgl.GeoJSONSource).setData(fc);
      } else {
        map.addSource(srcId, { type: "geojson", data: fc });
        map.addLayer({
          id: fillId,
          type: "fill",
          source: srcId,
          paint: { "fill-color": layer.color, "fill-opacity": layer.opacity },
        });
        map.addLayer({
          id: outlineId,
          type: "line",
          source: srcId,
          paint: { "line-color": layer.outline, "line-width": 1.5, "line-dasharray": [3, 2] },
        });
      }
    }
  };

  const removeBufferLayers = (map: mapboxgl.Map) => {
    for (const layer of BUFFER_LAYERS) {
      if (map.getLayer(`${layer.id}-fill`)) map.removeLayer(`${layer.id}-fill`);
      if (map.getLayer(`${layer.id}-outline`)) map.removeLayer(`${layer.id}-outline`);
      if (map.getSource(layer.id)) map.removeSource(layer.id);
    }
  };

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
      showBuffer(map, lat, lng);
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
    <div className="relative w-full h-full">
      <div
        ref={containerRef}
        className="w-full h-full rounded-lg overflow-hidden border border-gray-200"
      />
      <MapLegend />
    </div>
  );
}
