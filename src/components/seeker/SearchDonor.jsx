import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import toast from "react-hot-toast";
import { FiSearch, FiShield, FiBookmark, FiSend, FiMapPin, FiNavigation } from "react-icons/fi";
import bloodGroups from "../../utils/bloodGroups.js";
import districts from "../../utils/districts.js";
import { searchDonors, toggleSavedDonor } from "../../services/userService.js";
import { useLanguage } from "../../context/LanguageContext.jsx";
import SendRequestModal from "./SendRequestModal.jsx";
import Loader from "../common/Loader.jsx";

// By default (no filters) shows every currently-Active donor, nearest to
// the seeker's own present address first. Blood group / district / "Near
// Me" filters narrow it down further. Donor phone/email always arrive
// pre-masked from the backend — full contact is only ever revealed once
// that donor accepts an actual request.
export default function SearchDonor() {
  const { t } = useLanguage();
  const [filters, setFilters] = useState({ bloodGroup: "", district: "", upazila: "" });
  const [nearMe, setNearMe] = useState(true);
  const [donors, setDonors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [savedIds, setSavedIds] = useState(new Set());
  const [requestingDonor, setRequestingDonor] = useState(null);

  const handleChange = (e) => setFilters((f) => ({ ...f, [e.target.name]: e.target.value }));

  const runSearch = async (activeFilters = filters, useNearMe = nearMe) => {
    setLoading(true);
    try {
      const cleanFilters = Object.fromEntries(Object.entries(activeFilters).filter(([, v]) => v));
      if (useNearMe && !cleanFilters.district) cleanFilters.nearMe = "true";
      const { donors } = await searchDonors(cleanFilters);
      setDonors(donors);
    } catch {
      toast.error("সার্চ করা যায়নি");
    } finally {
      setLoading(false);
    }
  };

  // Load every active donor (nearest-first) as soon as the page opens.
  useEffect(() => {
    runSearch();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const handleSearch = (e) => {
    e.preventDefault();
    runSearch(filters, nearMe);
  };

  const handleNearMeToggle = () => {
    const next = !nearMe;
    setNearMe(next);
    runSearch({ ...filters, district: next ? "" : filters.district }, next);
    if (next) setFilters((f) => ({ ...f, district: "" }));
  };

  const handleSave = async (donorId) => {
    try {
      const { saved } = await toggleSavedDonor(donorId);
      setSavedIds((prev) => {
        const next = new Set(prev);
        saved ? next.add(donorId) : next.delete(donorId);
        return next;
      });
      toast.success(saved ? "ডোনার সেভ করা হয়েছে" : "ডোনার সেভ থেকে সরানো হয়েছে");
    } catch {
      toast.error("সেভ করা যায়নি");
    }
  };

  const inputClass =
    "w-full rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm text-gray-900 outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100 dark:border-white/10 dark:bg-white/5 dark:text-white";

  return (
    <div className="space-y-6">
      <form onSubmit={handleSearch} className="glass-card grid grid-cols-1 gap-4 p-5 sm:grid-cols-5">
        <select name="bloodGroup" value={filters.bloodGroup} onChange={handleChange} className={inputClass}>
          <option value="">{t("common.anyBloodGroup")}</option>
          {bloodGroups.map((g) => (
            <option key={g} value={g}>{g}</option>
          ))}
        </select>

        <select
          name="district"
          value={filters.district}
          onChange={(e) => {
            handleChange(e);
            if (e.target.value) setNearMe(false);
          }}
          className={inputClass}
        >
          <option value="">{t("common.anyDistrict")}</option>
          {districts.map((d) => (
            <option key={d} value={d}>{d}</option>
          ))}
        </select>

        <input
          name="upazila"
          value={filters.upazila}
          onChange={handleChange}
          placeholder="Upazila"
          className={inputClass}
        />

        <button
          type="button"
          onClick={handleNearMeToggle}
          className={`flex items-center justify-center gap-2 rounded-xl border py-2.5 text-sm font-semibold transition-colors ${
            nearMe
              ? "border-brand-500 bg-brand-50 text-brand-700 dark:bg-brand-950/40 dark:text-brand-300"
              : "border-gray-200 text-gray-600 dark:border-white/10 dark:text-gray-300"
          }`}
        >
          <FiNavigation size={14} /> {t("common.nearMe")}
        </button>

        <button type="submit" className="btn-primary justify-center">
          <FiSearch /> {t("common.search")}
        </button>
      </form>

      {loading && <Loader />}

      {!loading && donors.length === 0 && (
        <div className="glass-card p-12 text-center text-gray-500 dark:text-gray-400">
          {t("search.noResults")}
        </div>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {donors.map((d, i) => (
          <motion.div
            key={d.id}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: i * 0.05 }}
            className="glass-card p-5"
          >
            <div className="mb-3 flex items-center gap-3">
              {d.photoURL ? (
                <img src={d.photoURL} alt="" className="h-12 w-12 rounded-full object-cover" />
              ) : (
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-brand-gradient font-bold text-white">
                  {d.fullName?.charAt(0)}
                </div>
              )}
              <div className="min-w-0">
                <p className="flex items-center gap-1 font-semibold text-gray-900 dark:text-white">
                  {d.fullName}
                  {d.isVerified && <FiShield className="shrink-0 text-blue-500" size={14} />}
                </p>
                <p className="truncate text-[11px] text-gray-400" title={d.id}>
                  ID: {d.id.slice(0, 8)}
                </p>
              </div>
            </div>

            <div className="mb-1 flex flex-wrap items-center gap-2">
              <span className="rounded-full bg-brand-50 px-2.5 py-1 text-xs font-semibold text-brand-700 dark:bg-brand-950/50 dark:text-brand-300">
                {d.bloodGroup}{typeof d.age === "number" ? ` · ${d.age}y` : ""}
              </span>
              {typeof d.distanceKm === "number" && (
                <span className="rounded-full bg-gray-100 px-2.5 py-1 text-xs font-medium text-gray-600 dark:bg-white/10 dark:text-gray-300">
                  {d.distanceKm === 0 ? "একই জেলা" : `~${d.distanceKm} km`}
                </span>
              )}
              {d.daysUntilEligible > 0 && (
                <span className="rounded-full bg-amber-50 px-2.5 py-1 text-xs font-semibold text-amber-600 dark:bg-amber-950/40">
                  🩸 আবার Available: {d.daysUntilEligible} দিন পর
                </span>
              )}
            </div>
            <p className="mb-1 flex items-center gap-1 text-xs text-gray-500 dark:text-gray-400">
              <FiMapPin size={12} /> {d.upazila}, {d.district}
            </p>
            <p className="mb-4 text-xs text-gray-400">
              {d.phone} {d.email ? `· ${d.email}` : ""}
            </p>

            <div className="flex gap-2">
              <button
                onClick={() => handleSave(d.id)}
                className="flex flex-1 items-center justify-center gap-2 rounded-full border border-gray-200 py-2 text-xs font-semibold text-gray-600 dark:border-white/10 dark:text-gray-300"
              >
                <FiBookmark size={14} /> {savedIds.has(d.id) ? t("search.saved") : t("search.save")}
              </button>
              <button
                onClick={() => setRequestingDonor(d)}
                className="flex flex-1 items-center justify-center gap-2 rounded-full bg-brand-gradient py-2 text-xs font-semibold text-white"
              >
                <FiSend size={14} /> {t("common.request")}
              </button>
            </div>
          </motion.div>
        ))}
      </div>

      {requestingDonor && (
        <SendRequestModal donor={requestingDonor} onClose={() => setRequestingDonor(null)} />
      )}
    </div>
  );
}
