"use client";

import { useEffect, useState } from "react";
import { Search, MapPin } from "lucide-react";
import { getAllProperties } from "@/services/floodApi";
import type { PropertyDto } from "@/types";

interface PropertySearchProps {
  onSelect: (property: PropertyDto) => void;
}

export default function PropertySearch({ onSelect }: PropertySearchProps) {
  const [properties, setProperties] = useState<PropertyDto[]>([]);
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAllProperties()
      .then(setProperties)
      .catch(() => setProperties([]))
      .finally(() => setLoading(false));
  }, []);

  const filtered = properties.filter((p) =>
    p.propertyName.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <div className="flex flex-col gap-3">
      <div className="relative">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        <input
          type="text"
          placeholder="Search properties..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="w-full pl-10 pr-4 py-2.5 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
        />
      </div>

      <div className="flex flex-col gap-1 max-h-[calc(100vh-360px)] overflow-y-auto">
        {loading && (
          <p className="text-sm text-gray-400 py-4 text-center">
            Loading properties...
          </p>
        )}
        {!loading && filtered.length === 0 && (
          <p className="text-sm text-gray-400 py-4 text-center">
            No properties found
          </p>
        )}
        {filtered.map((property) => (
          <button
            key={property.id}
            onClick={() => onSelect(property)}
            className="flex items-start gap-3 p-3 rounded-lg hover:bg-blue-50 transition text-left group"
          >
            <MapPin className="w-4 h-4 mt-0.5 text-gray-400 group-hover:text-blue-500 shrink-0" />
            <div>
              <p className="text-sm font-medium text-gray-800 group-hover:text-blue-700">
                {property.propertyName}
              </p>
              <p className="text-xs text-gray-400 mt-0.5">
                {property.latitude.toFixed(4)}, {property.longitude.toFixed(4)}
              </p>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
