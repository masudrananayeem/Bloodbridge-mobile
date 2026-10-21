import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiMapPin } from "react-icons/fi";
import { getNearbyRequests } from "../../services/requestService.js";
import Loader from "../common/Loader.jsx";

export default function NearbyRequests() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getNearbyRequests()
      .then(({ requests }) => setRequests(requests))
      .catch(() => setRequests([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Loader />;

  if (requests.length === 0) {
    return (
      <div className="glass-card p-12 text-center text-gray-500 dark:text-gray-400">
        আপনার জেলায় এই মুহূর্তে অন্য কোনো রিকোয়েস্ট নেই।
      </div>
    );
  }

  return (
    <div className="max-w-2xl space-y-3">
      {requests.map((r, i) => (
        <motion.div
          key={r.id}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: i * 0.06 }}
          className="glass-card flex items-center justify-between p-4"
        >
          <div className="flex items-center gap-4">
            <div className="flex h-11 w-11 items-center justify-center rounded-full bg-brand-50 text-brand-600 dark:bg-brand-950/50 dark:text-brand-400">
              <FiMapPin size={18} />
            </div>
            <div>
              <p className="font-medium text-gray-900 dark:text-white">{r.hospital}</p>
              <p className="text-xs text-gray-500 dark:text-gray-400">
                {r.upazila}, {r.district}
                {typeof r.distanceKm === "number" && r.distanceKm > 0 ? ` · ~${r.distanceKm} km` : ""}
              </p>
            </div>
          </div>
          <span className="rounded-full bg-brand-50 px-2 py-0.5 text-xs font-semibold text-brand-700 dark:bg-brand-950/50 dark:text-brand-300">
            {r.bloodGroup}
          </span>
        </motion.div>
      ))}
    </div>
  );
}
