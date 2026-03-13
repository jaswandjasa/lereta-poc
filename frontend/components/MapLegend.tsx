"use client";

import { useState } from "react";
import { Layers, ChevronDown, ChevronUp } from "lucide-react";

const legendItems = [
  { label: "HIGH Risk Zone", color: "bg-red-500", border: false },
  { label: "MEDIUM Risk Zone", color: "bg-amber-500", border: false },
  { label: "LOW Risk Zone", color: "bg-green-500", border: false },
  { label: "Buffer Zone (500m)", color: "bg-blue-400", border: true },
  { label: "Selected Property", color: "bg-blue-600", border: false, isPin: true },
];

export default function MapLegend() {
  const [open, setOpen] = useState(true);

  return (
    <div className="absolute top-2 right-12 z-10">
      <button
        onClick={() => setOpen((o) => !o)}
        className="flex items-center gap-1 px-2 py-1 bg-white rounded-lg shadow-md border border-gray-200 text-[10px] font-semibold text-gray-600 hover:bg-gray-50 transition"
      >
        <Layers className="w-3 h-3" />
        Legend
        {open ? <ChevronUp className="w-3 h-3" /> : <ChevronDown className="w-3 h-3" />}
      </button>

      {open && (
        <div className="mt-1 bg-white rounded-lg shadow-md border border-gray-200 p-2 space-y-1.5 min-w-[140px]">
          {legendItems.map((item) => (
            <div key={item.label} className="flex items-center gap-2 text-[10px] text-gray-700">
              {item.isPin ? (
                <div className="w-3 h-3 flex items-center justify-center">
                  <div className="w-2.5 h-2.5 rounded-full bg-blue-600 ring-1 ring-blue-300" />
                </div>
              ) : (
                <div
                  className={`w-3 h-3 rounded-sm ${item.color} ${
                    item.border ? "border border-dashed border-blue-600 opacity-60" : "opacity-70"
                  }`}
                />
              )}
              <span>{item.label}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
