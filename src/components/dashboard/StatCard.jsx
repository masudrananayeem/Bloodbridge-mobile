import { motion } from "framer-motion";

export default function StatCard({ icon: Icon, label, value, tint = "brand", hint }) {
  const tints = {
    brand: "bg-gradient-to-br from-brand-500 to-brand-700 text-white shadow-brand-600/30",
    green: "bg-gradient-to-br from-emerald-400 to-emerald-600 text-white shadow-emerald-600/30",
    amber: "bg-gradient-to-br from-amber-400 to-amber-600 text-white shadow-amber-600/30",
    blue: "bg-gradient-to-br from-blue-400 to-blue-600 text-white shadow-blue-600/30",
  };

  return (
    <motion.div
      whileHover={{ y: -3 }}
      transition={{ duration: 0.2 }}
      className="glass-card flex items-center gap-4 p-5"
    >
      <div className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl shadow-lg ${tints[tint]}`}>
        <Icon size={22} />
      </div>
      <div className="min-w-0">
        <p className="truncate text-2xl font-bold text-gray-900 dark:text-white">{value}</p>
        <p className="truncate text-sm text-gray-500 dark:text-gray-400">{label}</p>
        {hint && <p className="mt-0.5 text-xs text-gray-400 dark:text-gray-500">{hint}</p>}
      </div>
    </motion.div>
  );
}
