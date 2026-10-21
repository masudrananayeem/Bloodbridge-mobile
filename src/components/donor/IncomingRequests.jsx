import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import toast from "react-hot-toast";
import { FiCheck, FiX, FiMapPin, FiClock } from "react-icons/fi";
import { getIncomingRequests, respondToRequest } from "../../services/requestService.js";
import Loader from "../common/Loader.jsx";

const urgencyColor = {
  High: "bg-red-50 text-red-600 dark:bg-red-950/40",
  Medium: "bg-amber-50 text-amber-600 dark:bg-amber-950/40",
  Low: "bg-gray-100 text-gray-600 dark:bg-white/10",
};

export default function IncomingRequests() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [respondingId, setRespondingId] = useState(null);

  useEffect(() => {
    getIncomingRequests()
      .then(({ requests }) => setRequests(requests))
      .catch(() => setRequests([]))
      .finally(() => setLoading(false));
  }, []);

  const respond = async (id, action) => {
    setRespondingId(id);
    try {
      await respondToRequest(id, action);
      setRequests((prev) => prev.filter((r) => r.id !== id));
      toast.success(action === "accept" ? "রিকোয়েস্ট গ্রহণ করা হয়েছে" : "রিকোয়েস্ট প্রত্যাখ্যান করা হয়েছে");
    } catch {
      toast.error("কিছু একটা সমস্যা হয়েছে, আবার চেষ্টা করুন");
    } finally {
      setRespondingId(null);
    }
  };

  if (loading) return <Loader />;

  if (requests.length === 0) {
    return (
      <div className="glass-card p-12 text-center text-gray-500 dark:text-gray-400">
        এই মুহূর্তে আপনার ব্লাড গ্রুপ ও এলাকায় কোনো নতুন রিকোয়েস্ট নেই।
      </div>
    );
  }

  return (
    <div className="max-w-2xl space-y-4">
      <AnimatePresence>
        {requests.map((r) => (
          <motion.div
            key={r.id}
            layout
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, x: -50 }}
            className="glass-card p-5"
          >
            <div className="mb-3 flex items-start justify-between">
              <div>
                <h4 className="font-semibold text-gray-900 dark:text-white">{r.seekerName}</h4>
                <p className="text-xs text-gray-500 dark:text-gray-400">{r.seekerPhone}</p>
                <p className="flex items-center gap-1 text-xs text-gray-400">
                  <FiClock size={12} /> {new Date(r.createdAt).toLocaleString()}
                </p>
              </div>
              <div className="flex flex-col items-end gap-1">
                <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${urgencyColor[r.urgency]}`}>
                  {r.urgency} Urgency
                </span>
                {r.isEmergency && (
                  <span className="rounded-full bg-red-50 px-2.5 py-1 text-xs font-semibold text-red-600 dark:bg-red-950/40">
                    🚨 Emergency
                  </span>
                )}
                {r.targetDonorUid && (
                  <span className="rounded-full bg-purple-50 px-2.5 py-1 text-xs font-semibold text-purple-600 dark:bg-purple-950/40">
                    Direct request
                  </span>
                )}
              </div>
            </div>

            <div className="mb-4 grid grid-cols-1 gap-x-4 gap-y-2 rounded-xl bg-gray-50 p-3 text-sm sm:grid-cols-2 dark:bg-white/5">
              <p className="text-gray-600 dark:text-gray-300">
                <span className="font-semibold text-gray-800 dark:text-gray-100">Blood Group:</span> {r.bloodGroup}
              </p>
              <p className="text-gray-600 dark:text-gray-300">
                <span className="font-semibold text-gray-800 dark:text-gray-100">Units Needed:</span> {r.units}
              </p>
              <p className="text-gray-600 dark:text-gray-300">
                <span className="font-semibold text-gray-800 dark:text-gray-100">Hospital Name:</span> {r.hospital}
              </p>
              <p className="flex items-center gap-1 text-gray-600 dark:text-gray-300">
                <span className="font-semibold text-gray-800 dark:text-gray-100">Place:</span>
                <FiMapPin size={12} className="shrink-0" /> {r.upazila}, {r.district}
              </p>
              {r.neededByDate && (
                <p className="text-gray-600 dark:text-gray-300 sm:col-span-2">
                  <span className="font-semibold text-gray-800 dark:text-gray-100">Needed By:</span>{" "}
                  {new Date(r.neededByDate).toLocaleDateString()}
                </p>
              )}
            </div>

            {r.message && (
              <p className="mb-4 rounded-lg bg-gray-50 p-3 text-sm italic text-gray-600 dark:bg-white/5 dark:text-gray-300">
                "{r.message}"
              </p>
            )}

            <div className="flex gap-3">
              <button
                onClick={() => respond(r.id, "accept")}
                disabled={respondingId === r.id}
                className="flex flex-1 items-center justify-center gap-2 rounded-full bg-brand-gradient py-2.5 text-sm font-semibold text-white disabled:opacity-50"
              >
                <FiCheck /> Accept
              </button>
              <button
                onClick={() => respond(r.id, "reject")}
                disabled={respondingId === r.id}
                className="flex flex-1 items-center justify-center gap-2 rounded-full border border-gray-200 py-2.5 text-sm font-semibold text-gray-600 disabled:opacity-50 dark:border-white/10 dark:text-gray-300"
              >
                <FiX /> Reject
              </button>
            </div>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  );
}
