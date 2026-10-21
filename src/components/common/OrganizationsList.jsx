import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiSearch, FiMapPin, FiPhone, FiMail, FiGlobe, FiShield, FiUsers } from "react-icons/fi";
import { getOrganizations } from "../../services/organizationService.js";
import districts from "../../utils/districts.js";
import Loader from "./Loader.jsx";

const TYPES = ["Voluntary Group", "NGO", "Blood Bank", "Hospital"];

export default function OrganizationsList() {
  const [filters, setFilters] = useState({ district: "", type: "", search: "" });
  const [organizations, setOrganizations] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = (activeFilters = filters) => {
    setLoading(true);
    const cleanFilters = Object.fromEntries(Object.entries(activeFilters).filter(([, v]) => v));
    getOrganizations(cleanFilters)
      .then(({ organizations }) => setOrganizations(organizations))
      .catch(() => setOrganizations([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const handleChange = (e) => setFilters((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = (e) => {
    e.preventDefault();
    load(filters);
  };

  const inputClass =
    "w-full rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm text-gray-900 outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100 dark:border-white/10 dark:bg-white/5 dark:text-white";

  return (
    <div className="space-y-6">
      <div className="glass-card flex items-start gap-3 p-5">
        <FiUsers className="mt-0.5 shrink-0 text-brand-500" size={20} />
        <p className="text-sm text-gray-600 dark:text-gray-300">
          এখানে বিভিন্ন রক্তদান সংগঠন, স্বেচ্ছাসেবী গ্রুপ, ব্লাড ব্যাংক ও হাসপাতালের তালিকা পাবেন — সরাসরি যোগাযোগের
          তথ্যসহ।
        </p>
      </div>

      <form onSubmit={handleSubmit} className="glass-card grid grid-cols-1 gap-4 p-5 sm:grid-cols-4">
        <input
          name="search"
          value={filters.search}
          onChange={handleChange}
          placeholder="নাম দিয়ে খুঁজুন..."
          className={inputClass}
        />
        <select name="district" value={filters.district} onChange={handleChange} className={inputClass}>
          <option value="">যেকোনো District</option>
          {districts.map((d) => (
            <option key={d} value={d}>{d}</option>
          ))}
        </select>
        <select name="type" value={filters.type} onChange={handleChange} className={inputClass}>
          <option value="">সব ধরনের সংগঠন</option>
          {TYPES.map((t) => (
            <option key={t} value={t}>{t}</option>
          ))}
        </select>
        <button type="submit" className="btn-primary justify-center">
          <FiSearch /> খুঁজুন
        </button>
      </form>

      {loading && <Loader />}

      {!loading && organizations.length === 0 && (
        <div className="glass-card p-12 text-center text-gray-500 dark:text-gray-400">
          কোনো সংগঠন পাওয়া যায়নি।
        </div>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {organizations.map((org, i) => (
          <motion.div
            key={org.id}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: i * 0.05 }}
            className="glass-card p-5"
          >
            <div className="mb-3 flex items-center gap-3">
              {org.logoURL ? (
                <img src={org.logoURL} alt="" className="h-12 w-12 rounded-full border border-gray-100 object-cover dark:border-white/10" />
              ) : (
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-brand-gradient font-bold text-white">
                  {org.name?.charAt(0)}
                </div>
              )}
              <div className="min-w-0">
                <p className="flex items-center gap-1 truncate font-semibold text-gray-900 dark:text-white">
                  {org.name}
                  {org.isVerified && <FiShield className="shrink-0 text-blue-500" size={14} title="Verified" />}
                </p>
                <span className="rounded-full bg-brand-50 px-2 py-0.5 text-[11px] font-semibold text-brand-700 dark:bg-brand-950/50 dark:text-brand-300">
                  {org.type}
                </span>
              </div>
            </div>

            {org.description && (
              <p className="mb-3 line-clamp-2 text-xs text-gray-500 dark:text-gray-400">{org.description}</p>
            )}

            <div className="space-y-1 text-xs text-gray-500 dark:text-gray-400">
              <p className="flex items-center gap-1.5">
                <FiMapPin size={12} className="shrink-0" /> {org.upazila ? `${org.upazila}, ` : ""}
                {org.district}
              </p>
              <p className="flex items-center gap-1.5">
                <FiPhone size={12} className="shrink-0" /> {org.phone}
              </p>
              {org.email && (
                <p className="flex items-center gap-1.5">
                  <FiMail size={12} className="shrink-0" /> {org.email}
                </p>
              )}
              {org.website && (
                <a
                  href={org.website}
                  target="_blank"
                  rel="noreferrer"
                  className="flex items-center gap-1.5 text-brand-600 hover:underline"
                >
                  <FiGlobe size={12} className="shrink-0" /> Website
                </a>
              )}
            </div>
          </motion.div>
        ))}
      </div>
    </div>
  );
}
