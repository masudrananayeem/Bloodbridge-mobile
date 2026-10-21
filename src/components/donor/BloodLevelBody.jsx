import { useId } from "react";
import { motion } from "framer-motion";
import { useLanguage } from "../../context/LanguageContext.jsx";
import { DONATION_COOLDOWN_DAYS, getDaysUntilEligible } from "../../utils/donationEligibility.js";

const BODY_TOP = 8;
const BODY_BOTTOM = 252;
const MIN_LEVEL = 18; // % of blood left right after donating

// Repeating sine wave (period 60) wide enough to slide seamlessly.
const wavePath = (amp) => {
  let d = `M0 0 Q15 ${-amp} 30 0`;
  for (let x = 60; x <= 300; x += 30) d += ` T${x} 0`;
  return `${d} V400 H0 Z`;
};

function BodyShapes() {
  return (
    <>
      <circle cx="60" cy="28" r="20" />
      <rect x="34" y="52" width="52" height="92" rx="18" />
      <rect x="12" y="56" width="17" height="84" rx="8.5" />
      <rect x="91" y="56" width="17" height="84" rx="8.5" />
      <rect x="36" y="138" width="22" height="114" rx="10" />
      <rect x="62" y="138" width="22" height="114" rx="10" />
    </>
  );
}

export default function BloodLevelBody({ lastDonationDate }) {
  const { t } = useLanguage();
  const uid = useId().replace(/:/g, "");
  const daysLeft = getDaysUntilEligible(lastDonationDate);
  const recovering = daysLeft > 0;
  const elapsed = recovering ? DONATION_COOLDOWN_DAYS - daysLeft : DONATION_COOLDOWN_DAYS;
  const pct = recovering ? Math.round(MIN_LEVEL + ((100 - MIN_LEVEL) * elapsed) / DONATION_COOLDOWN_DAYS) : 100;
  const y = BODY_BOTTOM - ((BODY_BOTTOM - BODY_TOP) * pct) / 100;

  return (
    <div className="flex shrink-0 flex-col items-center gap-2">
      <svg viewBox="0 0 120 260" className="h-44 w-auto sm:h-52" role="img" aria-label={`${t("availability.bloodLevel")} ${pct}%`}>
        <defs>
          <clipPath id={`body-${uid}`}>
            <BodyShapes />
          </clipPath>
          <linearGradient id={`blood-${uid}`} x1="0" y1="0" x2="0" y2="1">
            <stop offset="0" stopColor="#ef4444" />
            <stop offset="1" stopColor="#991b1b" />
          </linearGradient>
        </defs>

        <g fill="currentColor" className="text-gray-200 dark:text-white/10">
          <BodyShapes />
        </g>

        <g clipPath={`url(#body-${uid})`}>
          <motion.g initial={false} animate={{ y }} transition={{ type: "spring", stiffness: 60, damping: 18 }}>
            <motion.path
              d={wavePath(7)}
              fill="#ef4444"
              opacity="0.45"
              animate={{ x: [-60, 0] }}
              transition={{ duration: 3.2, repeat: Infinity, ease: "linear" }}
            />
            <motion.path
              d={wavePath(5)}
              fill={`url(#blood-${uid})`}
              animate={{ x: [0, -60] }}
              transition={{ duration: 2.2, repeat: Infinity, ease: "linear" }}
            />
          </motion.g>
        </g>

        <g fill="none" stroke="currentColor" strokeWidth="1.5" className="text-gray-400 dark:text-white/30">
          <BodyShapes />
        </g>
      </svg>

      <div className="text-center">
        <p className="text-lg font-bold text-red-600 dark:text-red-400">{pct}%</p>
        <p className="text-[11px] text-gray-500 dark:text-gray-400">
          {recovering ? `${t("availability.bloodRecovering")} · ${daysLeft} ${t("availability.days")}` : t("availability.bloodFull")}
        </p>
      </div>
    </div>
  );
}
