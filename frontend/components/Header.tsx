"use client";

import { Waves } from "lucide-react";

export default function Header() {
  return (
    <header className="bg-white border-b border-gray-200 shadow-sm">
      <div className="max-w-screen-2xl mx-auto px-4 py-3 flex items-center gap-3">
        <div className="flex items-center gap-2 text-blue-600">
          <Waves className="w-6 h-6" />
          <h1 className="text-xl font-bold tracking-tight">
            Flood Zoning for eStates
          </h1>
        </div>
        <span className="ml-auto text-xs text-gray-400 font-medium uppercase tracking-wider">
          PoC v1.0
        </span>
      </div>
    </header>
  );
}
