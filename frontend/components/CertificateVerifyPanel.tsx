"use client";

import { useState } from "react";
import { Search, ShieldCheck, ShieldX, ShieldQuestion } from "lucide-react";
import { verifyCertificate } from "@/services/floodApi";
import type { CertificateVerificationDto } from "@/types";

const statusConfig: Record<string, { icon: React.ReactNode; color: string; label: string }> = {
  VALID: {
    icon: <ShieldCheck className="w-5 h-5 text-green-600" />,
    color: "border-green-300 bg-green-50",
    label: "Valid Certificate",
  },
  NOT_FOUND: {
    icon: <ShieldQuestion className="w-5 h-5 text-gray-500" />,
    color: "border-gray-300 bg-gray-50",
    label: "Certificate Not Found",
  },
  INVALID: {
    icon: <ShieldX className="w-5 h-5 text-red-600" />,
    color: "border-red-300 bg-red-50",
    label: "Invalid Certificate",
  },
};

export default function CertificateVerifyPanel() {
  const [certNumber, setCertNumber] = useState("");
  const [result, setResult] = useState<CertificateVerificationDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleVerify = async () => {
    if (!certNumber.trim()) return;
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const data = await verifyCertificate(certNumber.trim());
      setResult(data);
    } catch {
      setError("Verification failed");
    } finally {
      setLoading(false);
    }
  };

  const cfg = result ? statusConfig[result.verificationStatus] : null;

  return (
    <div className="flex flex-col gap-2">
      <div className="flex gap-1.5">
        <input
          type="text"
          value={certNumber}
          onChange={(e) => setCertNumber(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleVerify()}
          placeholder="CERT-20260313-000001"
          className="flex-1 px-2.5 py-1.5 text-xs border border-gray-300 rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-500"
        />
        <button
          onClick={handleVerify}
          disabled={loading || !certNumber.trim()}
          className="px-2.5 py-1.5 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition"
        >
          <Search className="w-3.5 h-3.5" />
        </button>
      </div>

      {error && (
        <p className="text-xs text-red-600 text-center">{error}</p>
      )}

      {result && cfg && (
        <div className={`p-2.5 rounded-lg border ${cfg.color}`}>
          <div className="flex items-center gap-2 mb-1.5">
            {cfg.icon}
            <span className="text-xs font-semibold">{cfg.label}</span>
          </div>
          {result.verificationStatus === "VALID" && (
            <div className="text-[10px] text-gray-600 space-y-0.5">
              <p><span className="font-medium">Property:</span> {result.propertyName}</p>
              <p><span className="font-medium">Risk:</span> {result.riskLevel}</p>
              <p><span className="font-medium">Zone:</span> {result.zoneName}</p>
              <p className="truncate"><span className="font-medium">Hash:</span> {result.pdfHash}</p>
              <p className="truncate text-indigo-600 mt-1">
                <span className="font-medium text-gray-600">QR URL:</span>{" "}
                <a href={`/api/certificate/verify/${result.certificateNumber}`} className="underline">
                  /verify/{result.certificateNumber}
                </a>
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
