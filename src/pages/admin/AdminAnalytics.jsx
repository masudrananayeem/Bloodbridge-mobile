import { useEffect, useState } from "react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from "recharts";
import { getAnalytics } from "../../services/adminService.js";
import Loader from "../../components/common/Loader.jsx";

const statusColors = {
  pending: "#f59e0b",
  accepted: "#3b82f6",
  completed: "#10b981",
  cancelled: "#9ca3af",
};

export default function AdminAnalytics() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAnalytics()
      .then(setData)
      .catch(() => setData(null))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Loader />;

  const bloodGroupData = data?.bloodGroupDistribution || [];
  const statusData = data?.requestStatusBreakdown || [];
  const totalRequests = statusData.reduce((sum, s) => sum + s.count, 0) || 1;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Reports & Analytics</h1>

      <div className="glass-card p-6">
        <h3 className="mb-4 font-semibold text-gray-900 dark:text-white">Blood Group Distribution (Users)</h3>
        <div className="h-72 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={bloodGroupData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="bloodGroup" stroke="#9ca3af" fontSize={12} />
              <YAxis allowDecimals={false} stroke="#9ca3af" fontSize={12} />
              <Tooltip
                contentStyle={{ borderRadius: 12, border: "none", boxShadow: "0 4px 12px rgba(0,0,0,0.1)" }}
              />
              <Bar dataKey="count" fill="#e21f1f" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="glass-card p-6">
        <h3 className="mb-4 font-semibold text-gray-900 dark:text-white">Request Status Breakdown</h3>
        <div className="space-y-3">
          {statusData.map((s) => (
            <div key={s.status}>
              <div className="mb-1 flex justify-between text-sm">
                <span className="capitalize text-gray-600 dark:text-gray-300">{s.status}</span>
                <span className="text-gray-400">{s.count}</span>
              </div>
              <div className="h-2 w-full overflow-hidden rounded-full bg-gray-100 dark:bg-white/10">
                <div
                  className="h-full rounded-full"
                  style={{
                    width: `${(s.count / totalRequests) * 100}%`,
                    backgroundColor: statusColors[s.status] || "#e21f1f",
                  }}
                />
              </div>
            </div>
          ))}
          {statusData.length === 0 && (
            <p className="text-sm text-gray-400">এখনো কোনো রিকোয়েস্ট ডেটা নেই।</p>
          )}
        </div>
      </div>
    </div>
  );
}
