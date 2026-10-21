import { useEffect, useState } from "react";
import { FiDroplet } from "react-icons/fi";
import { getDonationHistory } from "../../services/userService.js";
import Loader from "../common/Loader.jsx";

export default function DonationHistory() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getDonationHistory()
      .then(({ history }) => setHistory(history))
      .catch(() => setHistory([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Loader />;

  if (history.length === 0) {
    return (
      <div className="glass-card flex flex-col items-center gap-3 p-12 text-center">
        <FiDroplet size={32} className="text-brand-300" />
        <p className="text-gray-500 dark:text-gray-400">এখনো কোনো রক্তদানের রেকর্ড নেই।</p>
      </div>
    );
  }

  return (
    <div className="glass-card overflow-x-auto p-2">
      <table className="w-full min-w-[500px] text-left text-sm">
        <thead>
          <tr className="text-xs uppercase tracking-wider text-gray-400">
            <th className="px-4 py-3">Date</th>
            <th className="px-4 py-3">Blood Group</th>
            <th className="px-4 py-3">Hospital</th>
            <th className="px-4 py-3">District</th>
            <th className="px-4 py-3">Units</th>
          </tr>
        </thead>
        <tbody>
          {history.map((h) => (
            <tr key={h.id} className="border-t border-gray-100 dark:border-white/10">
              <td className="px-4 py-3">{new Date(h.donationDate).toLocaleDateString()}</td>
              <td className="px-4 py-3">
                <span className="rounded-full bg-brand-50 px-2.5 py-1 text-xs font-semibold text-brand-700 dark:bg-brand-950/50 dark:text-brand-300">
                  {h.bloodGroup}
                </span>
              </td>
              <td className="px-4 py-3">{h.hospital}</td>
              <td className="px-4 py-3">{h.district}</td>
              <td className="px-4 py-3">{h.units}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
