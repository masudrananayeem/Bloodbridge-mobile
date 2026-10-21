import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import toast from "react-hot-toast";
import { FiMapPin, FiCalendar, FiX } from "react-icons/fi";
import { getMyRequests, cancelRequest } from "../../services/requestService.js";
import Loader from "../common/Loader.jsx";

const statusStyle = {
  pending: "bg-amber-50 text-amber-600 dark:bg-amber-950/40",
  accepted: "bg-blue-50 text-blue-600 dark:bg-blue-950/40",
  completed: "bg-emerald-50 text-emerald-600 dark:bg-emerald-950/40",
  cancelled: "bg-gray-100 text-gray-500 dark:bg-white/10",
  rejected: "bg-red-50 text-red-500 dark:bg-red-950/40",
};

// statuses: array of statuses to include, e.g. ["pending","accepted"] for
// "My Requests" or ["completed","cancelled"] for "Request History"
export default function RequestsList({ statuses, emptyText }) {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    getMyRequests()
      .then(({ requests }) => setRequests(requests.filter((r) => statuses.includes(r.status))))
      .catch(() => setRequests([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, []); // eslint-disable-line react-hooks/exhaustive-deps

  const handleCancel = async (id) => {
    try {
      await cancelRequest(id);
      toast.success("রিকোয়েস্ট বাতিল করা হয়েছে");
      load();
    } catch {
      toast.error("বাতিল করা যায়নি");
    }
  };

  if (loading) return <Loader />;

  if (requests.length === 0) {
    return (
      <div className="glass-card p-12 text-center text-gray-500 dark:text-gray-400">{emptyText}</div>
    );
  }

  return (
    <div className="max-w-2xl space-y-4">
      {requests.map((r, i) => (
        <motion.div
          key={r.id}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: i * 0.05 }}
          className="glass-card p-5"
        >
          <div className="mb-3 flex flex-wrap items-start justify-between gap-2">
            <div className="flex flex-wrap items-center gap-2">
              <span className="rounded-full bg-brand-50 px-2.5 py-1 text-xs font-semibold text-brand-700 dark:bg-brand-950/50 dark:text-brand-300">
                {r.bloodGroup} · {r.units} unit(s)
              </span>
              {r.isEmergency && (
                <span className="rounded-full bg-red-50 px-2.5 py-1 text-xs font-semibold text-red-600 dark:bg-red-950/40">
                  🚨 Emergency
                </span>
              )}
              {r.targetDonorUid && (
                <span className="rounded-full bg-purple-50 px-2.5 py-1 text-xs font-semibold text-purple-600 dark:bg-purple-950/40">
                  Direct → {r.targetDonorName}
                </span>
              )}
            </div>
            <span className={`rounded-full px-2.5 py-1 text-xs font-semibold capitalize ${statusStyle[r.status]}`}>
              {r.status}
            </span>
          </div>

          <p className="mb-1 flex items-center gap-1 text-sm text-gray-700 dark:text-gray-200">
            <FiMapPin size={14} /> {r.hospital}, {r.upazila}, {r.district}
          </p>
          <p className="flex items-center gap-1 text-xs text-gray-400">
            <FiCalendar size={12} /> Needed by {new Date(r.neededByDate).toLocaleDateString()}
          </p>

          {r.message && (
            <p className="mt-2 rounded-lg bg-gray-50 p-2 text-xs italic text-gray-500 dark:bg-white/5 dark:text-gray-400">
              "{r.message}"
            </p>
          )}

          {r.acceptedDonorUid && (
            <p className="mt-2 text-xs font-medium text-emerald-600">
              ✓ {r.acceptedDonorName} ({r.acceptedDonorPhone}
              {r.acceptedDonorEmail ? `, ${r.acceptedDonorEmail}` : ""}) accepted this request — contact them directly.
            </p>
          )}

          {r.status === "rejected" && (
            <p className="mt-2 text-xs font-medium text-red-500">
              ✕ কোনো ডোনার এই রিকোয়েস্ট গ্রহণ করেননি।
            </p>
          )}

          {r.status === "pending" && (
            <button
              onClick={() => handleCancel(r.id)}
              className="mt-3 flex items-center gap-1 text-xs font-semibold text-red-500 hover:underline"
            >
              <FiX size={14} /> Cancel Request
            </button>
          )}
        </motion.div>
      ))}
    </div>
  );
}
