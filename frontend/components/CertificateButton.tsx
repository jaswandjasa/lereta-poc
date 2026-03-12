"use client";

import { useState } from "react";
import { FileDown, Loader2 } from "lucide-react";
import { downloadCertificate } from "@/services/floodApi";

interface CertificateButtonProps {
  propertyId: number | null;
  propertyName: string | null;
}

export default function CertificateButton({
  propertyId,
  propertyName,
}: CertificateButtonProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleDownload = async () => {
    if (!propertyId) return;
    setLoading(true);
    setError(null);
    try {
      const blob = await downloadCertificate(propertyId);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `flood-certificate-property-${propertyId}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      a.remove();
    } catch {
      setError("Failed to generate certificate");
    } finally {
      setLoading(false);
    }
  };

  if (!propertyId) {
    return (
      <p className="text-xs text-gray-400 text-center py-2">
        Select a property to generate a certificate
      </p>
    );
  }

  return (
    <div className="flex flex-col gap-2">
      <button
        onClick={handleDownload}
        disabled={loading}
        className="flex items-center justify-center gap-2 w-full py-2.5 px-4 bg-emerald-600 text-white text-sm font-medium rounded-lg hover:bg-emerald-700 disabled:opacity-50 transition"
      >
        {loading ? (
          <Loader2 className="w-4 h-4 animate-spin" />
        ) : (
          <FileDown className="w-4 h-4" />
        )}
        {loading ? "Generating..." : "Download Certificate"}
      </button>
      {propertyName && (
        <p className="text-[10px] text-gray-400 text-center truncate">
          For: {propertyName}
        </p>
      )}
      {error && (
        <p className="text-xs text-red-600 text-center">{error}</p>
      )}
    </div>
  );
}
