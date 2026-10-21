import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from "recharts";
import {
  FiUsers,
  FiDroplet,
  FiSearch,
  FiClock,
  FiCheckCircle,
  FiHeart,
  FiArrowRight,
  FiClipboard,
  FiBarChart2,
} from "react-icons/fi";
import { getDashboardStats, getAnalytics } from "../../services/adminService.js";
import { useAuth } from "../../context/AuthContext.jsx";
import { useLanguage } from "../../context/LanguageContext.jsx";
import StatCard from "../../components/dashboard/StatCard.jsx";
import WelcomeBanner from "../../components/dashboard/WelcomeBanner.jsx";
import Loader from "../../components/common/Loader.jsx";

const quickLinks = [
  { to: "/admin/users", label: "Manage Users", desc: "সব ডোনার ও সিকার দেখুন, ভেরিফাই করুন", icon: FiUsers },
  { to: "/admin/requests?status=pending", label: "Approve Requests", desc: "পেন্ডিং রিকোয়েস্টগুলো পর্যালোচনা করুন", icon: FiClipboard },
  { to: "/admin/organizations", label: "Organizations", desc: "রক্তদান সংগঠন/গ্রুপ পরিচালনা করুন", icon: FiHeart },
  { to: "/admin/analytics", label: "Reports & Analytics", desc: "বিস্তারিত পরিসংখ্যান দেখুন", icon: FiBarChart2 },
];

export default function AdminOverview() {
  const { user } = useAuth();
  const { t } = useLanguage();
  const [stats, setStats] = useState(null);
  const [bloodGroupData, setBloodGroupData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getDashboardStats(), getAnalytics()])
      .then(([statsRes, analyticsRes]) => {
        setStats(statsRes.stats);
        setBloodGroupData(analyticsRes.bloodGroupDistribution || []);
      })
      .catch(() => {
        setStats(null);
        setBloodGroupData([]);
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="space-y-6">
      <WelcomeBanner
        title={`${t("common.welcome")}, ${user?.fullName?.split(" ")[0] || "Admin"} 👋`}
        subtitle={t("overview.adminSubtitle")}
        photoURL={user?.photoURL}
        initial={user?.fullName?.charAt(0) || "A"}
        badges={[{ label: "Admin" }]}
        tone="dark"
      />

      {loading ? (
        <Loader />
      ) : (
        <>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5">
            <StatCard icon={FiUsers} label="Total Users" value={stats?.totalUsers ?? 0} />
            <StatCard icon={FiDroplet} label={t("admin.manageDonors")} value={stats?.totalDonors ?? 0} tint="green" />
            <StatCard icon={FiSearch} label={t("admin.manageSeekers")} value={stats?.totalSeekers ?? 0} tint="blue" />
            <StatCard icon={FiClock} label={t("overview.pendingRequests")} value={stats?.pendingRequests ?? 0} tint="amber" />
            <StatCard icon={FiCheckCircle} label={t("overview.totalDonations")} value={stats?.completedDonations ?? 0} tint="green" />
          </div>

          <div className="grid grid-cols-1 gap-4 lg:grid-cols-3">
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              className="glass-card p-6 lg:col-span-2"
            >
              <div className="mb-4 flex items-center justify-between">
                <h3 className="font-semibold text-gray-900 dark:text-white">Blood Group Distribution</h3>
                <Link to="/admin/analytics" className="flex items-center gap-1 text-xs font-semibold text-brand-600 hover:underline">
                  বিস্তারিত <FiArrowRight size={12} />
                </Link>
              </div>
              <div className="h-64 w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={bloodGroupData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                    <XAxis dataKey="bloodGroup" stroke="#9ca3af" fontSize={12} />
                    <YAxis stroke="#9ca3af" fontSize={12} allowDecimals={false} />
                    <Tooltip />
                    <Bar dataKey="count" fill="#e21f1f" radius={[6, 6, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.05 }}
              className="glass-card flex flex-col justify-center gap-4 p-6 text-center"
            >
              <FiHeart className="mx-auto text-brand-500" size={28} />
              <div>
                <p className="text-3xl font-bold text-gray-900 dark:text-white">{stats?.totalOrganizations ?? 0}</p>
                <p className="text-sm text-gray-500 dark:text-gray-400">নিবন্ধিত সংগঠন</p>
              </div>
              <Link to="/admin/organizations" className="btn-primary justify-center !py-2 text-sm">
                পরিচালনা করুন
              </Link>
            </motion.div>
          </div>

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {quickLinks.map((link, i) => (
              <motion.div
                key={link.to}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.05 * i }}
              >
                <Link
                  to={link.to}
                  className="glass-card flex h-full items-start gap-3 p-5 transition-transform hover:-translate-y-0.5"
                >
                  <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-brand-50 text-brand-600 dark:bg-brand-950/50 dark:text-brand-400">
                    <link.icon size={18} />
                  </div>
                  <div>
                    <p className="font-semibold text-gray-900 dark:text-white">{link.label}</p>
                    <p className="mt-0.5 text-xs text-gray-500 dark:text-gray-400">{link.desc}</p>
                  </div>
                </Link>
              </motion.div>
            ))}
          </div>
        </>
      )}
    </div>
  );
}
