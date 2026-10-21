import { useState } from "react";
import { motion } from "framer-motion";
import toast from "react-hot-toast";
import { FiDroplet, FiLoader } from "react-icons/fi";
import { useAuth } from "../../context/AuthContext.jsx";
import { useLanguage } from "../../context/LanguageContext.jsx";
import { toggleAvailability, recordDonation } from "../../services/userService.js";
import BloodLevelBody from "./BloodLevelBody.jsx";
import { getDaysUntilEligible, getNextEligibleDateLabel, isInCooldown } from "../../utils/donationEligibility.js";

export default function AvailabilityToggle() {
  const { user, setUser } = useAuth();
  const { t } = useLanguage();
  const [loading, setLoading] = useState(false);
  const [recording, setRecording] = useState(false);
  const [confirmingDonation, setConfirmingDonation] = useState(false);
  const isAvailable = user?.isAvailable ?? true;
  const cooldown = isInCooldown(user?.lastDonationDate);
  const daysLeft = getDaysUntilEligible(user?.lastDonationDate);

  const handleToggle = async () => {
    if (!isAvailable && cooldown) {
      toast.error(`${t("availability.cooldownNote")} ${daysLeft} ${t("availability.days")}`);
      return;
    }
    setLoading(true);
    try {
      const { isAvailable: updated } = await toggleAvailability(!isAvailable);
      setUser((prev) => ({ ...prev, isAvailable: updated }));
      toast.success(updated ? t("overview.available") : t("overview.unavailable"));
    } catch (err) {
      toast.error(err?.response?.data?.message || "Couldn't update status");
    } finally {
      setLoading(false);
    }
  };

  const handleRecordDonation = async () => {
    setRecording(true);
    try {
      const { user: updated } = await recordDonation();
      setUser((prev) => ({ ...prev, ...updated }));
      toast.success(t("availability.donatedToday") + " ✅");
      setConfirmingDonation(false);
    } catch (err) {
      toast.error(err?.response?.data?.message || "Couldn't record donation");
    } finally {
      setRecording(false);
    }
  };

  return (
    <div className="space-y-4">
      <div className="glass-card flex items-center justify-between p-6">
        <div>
          <h3 className="font-semibold text-gray-900 dark:text-white">{t("availability.title")}</h3>
          <p className="text-sm text-gray-500 dark:text-gray-400">{t("availability.subtitle")}</p>
        </div>

        <button
          onClick={handleToggle}
          disabled={loading}
          className={`relative h-8 w-16 shrink-0 rounded-full transition-colors duration-300 ${
            isAvailable ? "bg-brand-gradient" : "bg-gray-300 dark:bg-white/10"
          } ${!isAvailable && cooldown ? "cursor-not-allowed opacity-60" : ""}`}
        >
          <motion.span
            animate={{ x: isAvailable ? 32 : 4 }}
            transition={{ type: "spring", stiffness: 500, damping: 30 }}
            className="absolute top-1 flex h-6 w-6 items-center justify-center rounded-full bg-white text-[10px] font-bold text-gray-600 shadow"
          >
            {isAvailable ? "ON" : "OFF"}
          </motion.span>
        </button>
      </div>

      {cooldown && (
        <div className="glass-card flex items-center justify-between gap-4 border border-amber-200 bg-amber-50 p-4 text-sm text-amber-700 dark:border-amber-900 dark:bg-amber-950/30 dark:text-amber-400">
          <div className="flex items-start gap-3">
            <FiDroplet className="mt-0.5 shrink-0" />
            <span>
              {t("availability.cooldownNote")} <b>{daysLeft} {t("availability.days")}</b> ({t("availability.nextEligible")}:{" "}
              <b>{getNextEligibleDateLabel(user?.lastDonationDate)}</b>)
            </span>
          </div>
          <BloodLevelBody lastDonationDate={user?.lastDonationDate} />
        </div>
      )}

      {!cooldown && (
        <div className="glass-card flex items-center justify-between gap-6 p-6">
          <div className="min-w-0 flex-1">
          <h3 className="mb-1 font-semibold text-gray-900 dark:text-white">{t("availability.donatedTodayQ")}</h3>
          <p className="mb-4 text-sm text-gray-500 dark:text-gray-400">{t("availability.donatedTodayDesc")}</p>

          {!confirmingDonation ? (
            <button
              onClick={() => setConfirmingDonation(true)}
              className="flex items-center gap-2 rounded-full bg-brand-gradient px-5 py-2.5 text-sm font-semibold text-white"
            >
              <FiDroplet /> {t("availability.donatedToday")}
            </button>
          ) : (
            <div className="flex flex-wrap items-center gap-3">
              <p className="text-sm text-gray-600 dark:text-gray-300">{t("availability.confirm")}</p>
              <button
                onClick={handleRecordDonation}
                disabled={recording}
                className="flex items-center gap-2 rounded-full bg-brand-gradient px-4 py-2 text-xs font-semibold text-white disabled:opacity-50"
              >
                {recording ? <FiLoader className="animate-spin" size={14} /> : null} {t("availability.yesConfirm")}
              </button>
              <button
                onClick={() => setConfirmingDonation(false)}
                className="rounded-full border border-gray-200 px-4 py-2 text-xs font-semibold text-gray-600 dark:border-white/10 dark:text-gray-300"
              >
                {t("availability.cancel")}
              </button>
            </div>
          )}
          </div>
          <BloodLevelBody lastDonationDate={user?.lastDonationDate} />
        </div>
      )}
    </div>
  );
}
