import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import toast from "react-hot-toast";
import { FiMapPin, FiCalendar } from "react-icons/fi";
import { getAllRequests, updateRequestStatus } from "../../services/adminService.js";
import Loader from "../../components/common/Loader.jsx";

const statusStyle = {
  pending: "bg-amber-50 text-amber-600 dark:bg-amber-950/40",
  accepted: "bg-blue-50 text-blue-600 dark:bg-blue-950/40",
  completed: "bg-emerald-50 text-emerald-600 dark:bg-emerald-950/40",
  cancelled: "bg-gray-100 text-gray-500 dark:bg-white/10",
  rejected: "bg-red-50 text-red-500 dark:bg-red-950/40",
};

const tabs = ["", "pending", "accepted", "completed", "cancelled", "rejected"];

export default function AdminRequests() {
  const [searchParams, setSearchParams] = useSearchParams();
  const status = searchParams.get("status") || "";

  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    getAllRequests({ status: status || undefined })
      .then(({ requests }) => setRequests(requests))
      .catch(() => setRequests([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, [status]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleStatusChange = async (id, newStatus) => {
    try {
      await updateRequestStatus(id, newStatus);
      toast.success(`Request marked as ${newStatus}`);
      load();
    } catch {
      toast.error("আপডেট করা যায়নি");
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Manage Blood Requests</h1>
        <div className="flex flex-wrap gap-2">
          {tabs.map((t) => (
            <button
              key={t || "all"}
              onClick={() => setSearchParams(t ? { status: t } : {})}
              className={`rounded-full px-4 py-1.5 text-sm font-medium capitalize transition-colors ${
                status === t
                  ? "bg-brand-gradient text-white"
                  : "border border-gray-200 text-gray-600 dark:border-white/10 dark:text-gray-300"
              }`}
            >
              {t || "All"}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <Loader />
      ) : requests.length === 0 ? (
        <div className="glass-card p-12 text-center text-gray-500 dark:text-gray-400">
          কোনো রিকোয়েস্ট পাওয়া যায়নি।
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
          {requests.map((r) => (
            <div key={r.id} className="glass-card p-5">
              <div className="mb-3 flex items-start justify-between">
                <div>
                  <span className="rounded-full bg-brand-50 px-2.5 py-1 text-xs font-semibold text-brand-700 dark:bg-brand-950/50 dark:text-brand-300">
                    {r.bloodGroup} · {r.units} unit(s)
                  </span>
                  <p className="mt-1 text-sm font-medium text-gray-900 dark:text-white">
                    By {r.seekerName || "Unknown"}
                  </p>
                </div>
                <span className={`rounded-full px-2.5 py-1 text-xs font-semibold capitalize ${statusStyle[r.status]}`}>
                  {r.status}
                </span>
              </div>

              <p className="mb-1 flex items-center gap-1 text-sm text-gray-600 dark:text-gray-300">
                <FiMapPin size={14} /> {r.hospital}, {r.upazila}, {r.district}
              </p>
              <p className="mb-3 flex items-center gap-1 text-xs text-gray-400">
                <FiCalendar size={12} /> Needed by {new Date(r.neededByDate).toLocaleDateString()}
              </p>

              {r.acceptedDonorUid && (
                <p className="mb-3 text-xs text-emerald-600">
                  ✓ Accepted by {r.acceptedDonorName} ({r.acceptedDonorPhone})
                </p>
              )}
              {r.isEmergency && (
                <span className="mb-2 mr-2 inline-block rounded-full bg-red-50 px-2.5 py-1 text-xs font-semibold text-red-600 dark:bg-red-950/40">
                  🚨 Emergency
                </span>
              )}
              {r.targetDonorUid && (
                <span className="mb-2 inline-block rounded-full bg-purple-50 px-2.5 py-1 text-xs font-semibold text-purple-600 dark:bg-purple-950/40">
                  Direct → {r.targetDonorName}
                </span>
              )}

              <div className="flex flex-wrap gap-2">
                {r.status === "pending" && (
                  <button
                    onClick={() => handleStatusChange(r.id, "accepted")}
                    className="rounded-full bg-brand-gradient px-4 py-1.5 text-xs font-semibold text-white"
                  >
                    Approve
                  </button>
                )}
                {r.status === "accepted" && (
                  <button
                    onClick={() => handleStatusChange(r.id, "completed")}
                    className="rounded-full bg-emerald-600 px-4 py-1.5 text-xs font-semibold text-white"
                  >
                    Mark Completed
                  </button>
                )}
                {!["cancelled", "completed", "rejected"].includes(r.status) && (
                  <button
                    onClick={() => handleStatusChange(r.id, "cancelled")}
                    className="rounded-full border border-gray-200 px-4 py-1.5 text-xs font-semibold text-gray-600 dark:border-white/10 dark:text-gray-300"
                  >
                    Cancel
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
