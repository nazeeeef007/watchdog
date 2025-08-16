// MonitorCheckPage.tsx (light mode)
import React, { useMemo } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { useMonitorChecks } from "@/hooks/useMonitorChecks";
import type { MonitorCheckDTO } from "@/types/MonitorCheckDTO";
import type { ErrorCategory } from "@/types/ErrorCategory";
import { MainLayout } from "@/components/MainLayout";
import { useAuth } from "@/context/AuthContext";

/* --------------------------
   Internal small components
   -------------------------- */

const PageHeader: React.FC<{ title: string; subtitle?: string }> = ({ title, subtitle }) => (
  <motion.header className="mb-6" initial={{ opacity: 0, y: -6 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.28 }} aria-label="page header">
    <h1 className="text-2xl sm:text-3xl md:text-4xl font-extrabold text-gray-900 leading-tight">{title}</h1>
    {subtitle && <p className="mt-1 text-gray-600">{subtitle}</p>}
  </motion.header>
);

const getStatusLabel = (isUp: boolean | undefined) => (isUp === undefined ? "Unknown" : isUp ? "Up" : "Down");

/* Status badge — light variant */
const StatusBadge: React.FC<{ isUp: boolean | undefined }> = ({ isUp }) => {
  const base = "inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium";
  const color = isUp === undefined ? "bg-gray-200 text-gray-700" : isUp ? "bg-emerald-100 text-emerald-800" : "bg-red-100 text-red-700";
  return <span className={`${base} ${color}`}>{getStatusLabel(isUp)}</span>;
};

/* Error category badge — light variant */
const mapErrorColorLight = (category?: ErrorCategory) => {
  if (!category || category === "NONE") return "bg-gray-100 text-gray-700";
  switch (category) {
    case "HTTP_CLIENT_ERROR": return "bg-yellow-100 text-yellow-800";
    case "HTTP_SERVER_ERROR": return "bg-red-100 text-red-800";
    case "NETWORK_ERROR": return "bg-red-200 text-red-900";
    case "TIMEOUT_ERROR": return "bg-orange-100 text-orange-800";
    case "SSL_ERROR": return "bg-purple-100 text-purple-800";
    case "CONTENT_MISMATCH": return "bg-indigo-100 text-indigo-800";
    default: return "bg-gray-100 text-gray-700";
  }
};
const ErrorCategoryBadge: React.FC<{ category?: ErrorCategory }> = ({ category }) => (
  <span className={`inline-flex items-center px-2 py-0.5 rounded-md text-xs font-medium ${mapErrorColorLight(category)}`}>{category ?? "NONE"}</span>
);

/* Light spinner */
const LoadingSpinner: React.FC<{ size?: number }> = ({ size = 8 }) => (
  <div className="flex justify-center items-center py-10">
    <div
      role="status"
      aria-label="Loading"
      className="animate-spin rounded-full"
      style={{
        width: `${size}rem`,
        height: `${size}rem`,
        borderWidth: "3px",
        borderStyle: "solid",
        borderColor: "rgba(0,0,0,0.06)",
        borderBottomColor: "#059669", // emerald-600
      }}
    />
  </div>
);

const ErrorMessage: React.FC<{ message: string }> = ({ message }) => (
  <div role="alert" className="bg-red-50 border border-red-200 text-red-800 p-3 rounded-md">
    <strong className="font-semibold">Error</strong>
    <div className="mt-1 text-sm">{message}</div>
  </div>
);

/* Table — light theme */
const ChecksTable: React.FC<{ checks: MonitorCheckDTO[] }> = ({ checks }) => {
  const rows = useMemo(() => checks ?? [], [checks]);
  if (!rows.length) return <div className="py-8 text-center text-gray-500">No monitor checks found.</div>;

  return (
    <div className="overflow-auto rounded-lg border border-gray-200 bg-white shadow-sm">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50 sticky top-0">
          <tr>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">ID</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">Monitor</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">Status</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">Resp (ms)</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">Conn (ms)</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">DNS (ms)</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">TTFB (ms)</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">Size (B)</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">HTTP</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">Error</th>
            <th className="px-3 py-2 text-left text-xs text-gray-600 uppercase">Timestamp</th>
          </tr>
        </thead>

        <tbody className="bg-white divide-y divide-gray-100">
          {rows.map((c) => (
            <tr key={c.id} className="hover:bg-gray-50">
              <td className="px-3 py-2 text-sm text-gray-800 whitespace-nowrap">{c.id}</td>
              <td className="px-3 py-2 text-sm text-gray-800 whitespace-nowrap">
                <Link to={`/alert-history/${c.monitorId}`} className="text-sky-600 hover:underline">
                  {c.monitorId}
                </Link>
              </td>
              <td className="px-3 py-2 whitespace-nowrap"><StatusBadge isUp={c.up} /></td>
              <td className="px-3 py-2 text-sm text-gray-700 whitespace-nowrap">{c.responseTimeMs ?? "-"}</td>
              <td className="px-3 py-2 text-sm text-gray-700 whitespace-nowrap">{c.connectTimeMs ?? "-"}</td>
              <td className="px-3 py-2 text-sm text-gray-700 whitespace-nowrap">{c.dnsTimeMs ?? "-"}</td>
              <td className="px-3 py-2 text-sm text-gray-700 whitespace-nowrap">{c.ttfbMs ?? "-"}</td>
              <td className="px-3 py-2 text-sm text-gray-700 whitespace-nowrap">{c.responseBodySize ?? "-"}</td>
              <td className="px-3 py-2 text-sm text-gray-700 whitespace-nowrap">{c.httpStatusCode ?? "-"}</td>
              <td className="px-3 py-2 text-sm text-gray-700 whitespace-nowrap">{c.errorMessage ?? "-"}</td>
              <td className="px-3 py-2 text-sm text-gray-600 whitespace-nowrap">{c.timestamp ? new Date(c.timestamp).toLocaleString() : "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

/* --------------------------
   Main page component (light)
   -------------------------- */
const MonitorCheckPage: React.FC = () => {
  // route param
  const { monitorId: monitorIdParam } = useParams<{ monitorId?: string }>();
  const monitorId = monitorIdParam ? parseInt(monitorIdParam, 10) : null;

  // auth
  const { logout } = useAuth();

  // hook
  const { monitorChecks, isLoading, error, refetchMonitorChecks, selectedMonitorId, selectMonitor } = useMonitorChecks(monitorId);

  // navigation helper
  const navigate = useNavigate();
  const handleViewAll = () => {
    if (selectMonitor) selectMonitor(null);
    if (refetchMonitorChecks) refetchMonitorChecks();
    navigate("/monitor-check", { replace: true });
  };

  const title = selectedMonitorId ? `Checks for Monitor ${selectedMonitorId}` : "All Monitor Checks";
  const subtitle = selectedMonitorId ? `Viewing checks for monitor ${selectedMonitorId}` : "Recent checks across all monitors";

  return (
    <MainLayout onLogout={logout}>
      <div className="min-h-screen bg-gray-50 text-gray-900 p-6 sm:p-8">
        <div className="max-w-6xl mx-auto">
          <PageHeader title={title} subtitle={subtitle} />

          {/* Toolbar */}
          <div className="flex flex-col sm:flex-row items-start sm:items-center sm:justify-between gap-3 mb-6">
            <div className="flex items-center gap-3">
              <button onClick={() => refetchMonitorChecks()} className="inline-flex items-center gap-2 px-3 py-2 bg-white border border-gray-200 hover:bg-gray-100 text-gray-800 rounded-md shadow-sm transition" aria-label="Refresh checks">
                Refresh
              </button>

              {selectedMonitorId && (
                <button
                  onClick={() => { if (selectMonitor) selectMonitor(null); refetchMonitorChecks(); navigate("/monitor-check", { replace: true }); }}
                  className="px-3 py-2 bg-white border border-gray-200 hover:bg-gray-100 text-gray-800 rounded-md"
                >
                  Clear filter
                </button>
              )}
            </div>

            <div className="flex items-center gap-3">
              {selectedMonitorId ? (
                <button onClick={handleViewAll} className="px-3 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-md shadow-sm">
                  View All Checks
                </button>
              ) : (
                <div className="text-sm text-gray-600">Showing all monitors</div>
              )}
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-4 sm:p-6 border border-gray-100">
            {isLoading ? <LoadingSpinner /> : error ? <ErrorMessage message={error} /> : <ChecksTable checks={monitorChecks} />}
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default MonitorCheckPage;
