import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import { FiChevronDown, FiDroplet, FiSearch } from "react-icons/fi";
import toast from "react-hot-toast";
import { useAuth } from "../../context/AuthContext.jsx";
import { useLanguage } from "../../context/LanguageContext.jsx";
import { switchMode as switchModeApi } from "../../services/userService.js";

export default function RoleSwitch() {
  const { user, setUser } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const mode = user?.activeMode || "donor";

  const handleSwitch = async (nextMode) => {
    if (nextMode === mode) return setOpen(false);
    setLoading(true);
    try {
      await switchModeApi(nextMode);
      setUser((prev) => ({ ...prev, activeMode: nextMode }));
      navigate(`/dashboard/${nextMode}`);
      toast.success(nextMode === "donor" ? t("common.bloodDonor") : t("common.bloodSeeker"));
    } catch {
      toast.error("মোড পরিবর্তন করা যায়নি");
    } finally {
      setLoading(false);
      setOpen(false);
    }
  };

  return (
    <div className="relative">
      <button
        onClick={() => setOpen((o) => !o)}
        disabled={loading}
        className="flex items-center gap-2 rounded-full border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm dark:border-white/10 dark:bg-white/5 dark:text-gray-200"
      >
        {mode === "donor" ? <FiDroplet className="text-brand-600" /> : <FiSearch className="text-brand-600" />}
        {t("common.currentMode")}: {mode === "donor" ? t("common.bloodDonor") : t("common.bloodSeeker")}
        <FiChevronDown className={`transition-transform ${open ? "rotate-180" : ""}`} />
      </button>

      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: -8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -8 }}
            transition={{ duration: 0.15 }}
            className="glass-card absolute right-0 z-20 mt-2 w-56 overflow-hidden !rounded-xl p-1"
          >
            <button
              onClick={() => handleSwitch("donor")}
              className="flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-left text-sm hover:bg-brand-50 dark:hover:bg-white/10"
            >
              <FiDroplet className="text-brand-600" /> {t("common.bloodDonor")}
            </button>
            <button
              onClick={() => handleSwitch("seeker")}
              className="flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-left text-sm hover:bg-brand-50 dark:hover:bg-white/10"
            >
              <FiSearch className="text-brand-600" /> {t("common.bloodSeeker")}
            </button>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
