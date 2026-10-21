import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiBell, FiCheckCircle, FiXCircle, FiShield } from "react-icons/fi";
import { getNotifications, markAllAsRead } from "../../services/notificationService.js";
import Loader from "../common/Loader.jsx";

const iconFor = { request: FiBell, accepted: FiCheckCircle, rejected: FiXCircle, verified: FiShield, filled: FiXCircle };
const tintFor = {
  request: "text-brand-600 bg-brand-50 dark:bg-brand-950/40",
  accepted: "text-emerald-600 bg-emerald-50 dark:bg-emerald-950/40",
  rejected: "text-red-500 bg-red-50 dark:bg-red-950/40",
  verified: "text-blue-600 bg-blue-50 dark:bg-blue-950/40",
  filled: "text-gray-500 bg-gray-100 dark:bg-white/10",
};

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getNotifications()
      .then(({ notifications }) => {
        setNotifications(notifications);
        if (notifications.some((n) => !n.isRead)) markAllAsRead();
      })
      .catch(() => setNotifications([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Loader />;

  if (notifications.length === 0) {
    return (
      <div className="glass-card flex flex-col items-center gap-3 p-12 text-center">
        <FiBell size={28} className="text-brand-300" />
        <p className="text-gray-500 dark:text-gray-400">এখনো কোনো নোটিফিকেশন নেই।</p>
      </div>
    );
  }

  return (
    <div className="max-w-2xl space-y-3">
      {notifications.map((n, i) => {
        const Icon = iconFor[n.type] || FiBell;
        return (
          <motion.div
            key={n.id}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: i * 0.04 }}
            className={`glass-card flex items-start gap-4 p-4 ${!n.isRead ? "ring-1 ring-brand-200 dark:ring-brand-900" : ""}`}
          >
            <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-full ${tintFor[n.type]}`}>
              <Icon size={18} />
            </div>
            <div>
              <p className="text-sm text-gray-700 dark:text-gray-200">{n.message}</p>
              <p className="mt-1 text-xs text-gray-400">{new Date(n.createdAt).toLocaleString()}</p>
            </div>
          </motion.div>
        );
      })}
    </div>
  );
}
