import { useEffect, useId, useState } from "react";
import { animate, motion } from "framer-motion";
import { useLanguage } from "../../context/LanguageContext.jsx";
import { DONATION_COOLDOWN_DAYS, getDaysUntilEligible } from "../../utils/donationEligibility.js";

const BODY_TOP = 6;
const BODY_BOTTOM = 254;
const MIN_LEVEL = 18; // % of blood left right after donating

// Repeating sine wave (period 60), wide enough to slide seamlessly.
const wavePath = (amp) => {
  let d = `M0 0 Q15 ${-amp} 30 0`;
  for (let x = 60; x <= 300; x += 30) d += ` T${x} 0`;
  return `${d} V400 H0 Z`;
};

const BUBBLES = [
  { x: 48, size: 2.4, delay: 0, dur: 3.4 },
  { x: 64, size: 1.8, delay: 1.1, dur: 4.1 },
  { x: 76, size: 2.8, delay: 0.5, dur: 3.8 },
  { x: 40, size: 1.6, delay: 2.2, dur: 4.6 },
  { x: 58, size: 2.2, delay: 1.7, dur: 3.1 },
  { x: 84, size: 1.7, delay: 2.8, dur: 4.3 },
];

function BodyShapes() {
  return (
    <>
      <circle cx="60" cy="25" r="17" />
      <rect x="53" y="40" width="14" height="14" rx="5" />
      <path d="M31 60 Q31 50 42 50 H78 Q89 50 89 60 V116 Q89 142 78 142 H42 Q31 142 31 116 Z" />
      <rect x="8" y="55" width="17" height="86" rx="8.5" transform="rotate(5 16 55)" />
      <rect x="95" y="55" width="17" height="86" rx="8.5" transform="rotate(-5 104 55)" />
      <rect x="35" y="134" width="23" height="120" rx="11" />
      <rect x="62" y="134" width="23" height="120" rx="11" />
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

  // Count the percentage up smoothly whenever it changes.
  const [shown, setShown] = useState(0);
  useEffect(() => {
    const controls = animate(0, pct, { duration: 1.4, ease: "easeOut", onUpdate: (v) => setShown(Math.round(v)) });
    return () => controls.stop();
  }, [pct]);

  const full = !recovering;

  return (
    <div className="flex shrink-0 flex-col items-center gap-2">
      <motion.div
        animate={{ scale: [1, 1.018, 1] }}
        transition={{ duration: 3.6, repeat: Infinity, ease: "easeInOut" }}
        className="relative"
        style={{ filter: full ? "drop-shadow(0 0 10px rgba(239,68,68,0.45))" : "none" }}
      >
        <svg viewBox="0 0 120 260" className="h-44 w-auto sm:h-52" role="img" aria-label={`${t("availability.bloodLevel")} ${pct}%`}>
          <defs>
            <clipPath id={`body-${uid}`}>
              <BodyShapes />
            </clipPath>
            <linearGradient id={`blood-${uid}`} x1="0" y1="0" x2="0" y2="1">
              <stop offset="0" stopColor="#f87171" />
              <stop offset="0.45" stopColor="#dc2626" />
              <stop offset="1" stopColor="#7f1d1d" />
            </linearGradient>
            <linearGradient id={`shine-${uid}`} x1="0" y1="0" x2="1" y2="0">
              <stop offset="0" stopColor="#fff" stopOpacity="0.28" />
              <stop offset="0.35" stopColor="#fff" stopOpacity="0" />
            </linearGradient>
          </defs>

          <g fill="currentColor" className="text-gray-200 dark:text-white/10">
            <BodyShapes />
          </g>

          <g clipPath={`url(#body-${uid})`}>
            <motion.g initial={{ y: BODY_BOTTOM + 10 }} animate={{ y }} transition={{ type: "spring", stiffness: 45, damping: 16 }}>
              <motion.path
                d={wavePath(7)}
                fill="#ef4444"
                opacity="0.4"
                animate={{ x: [-60, 0] }}
                transition={{ duration: 3.4, repeat: Infinity, ease: "linear" }}
              />
              <motion.path
                d={wavePath(5)}
                fill={`url(#blood-${uid})`}
                animate={{ x: [0, -60] }}
                transition={{ duration: 2.3, repeat: Infinity, ease: "linear" }}
              />
              {BUBBLES.map((b, i) => (
                <motion.circle
                  key={i}
                  cx={b.x}
                  r={b.size}
                  fill="#fecaca"
                  initial={{ cy: 230, opacity: 0 }}
                  animate={{ cy: [230, 20], opacity: [0, 0.7, 0] }}
                  transition={{ duration: b.dur, delay: b.delay, repeat: Infinity, ease: "easeOut" }}
                />
              ))}
            </motion.g>
            <rect x="0" y="0" width="120" height="260" fill={`url(#shine-${uid})`} />
          </g>


          <motion.path
            d="M60 100 C40 86 46 72 56 76 C58 77 59.5 78.5 60 80 C60.5 78.5 62 77 64 76 C74 72 80 86 60 100 Z"
            fill="#fff"
            opacity="0.85"
            style={{ transformBox: "fill-box", transformOrigin: "center" }}
            animate={{ scale: [1, 1.22, 1, 1.15, 1] }}
            transition={{ duration: recovering ? 1.6 : 1.1, repeat: Infinity, ease: "easeInOut", times: [0, 0.15, 0.3, 0.45, 1] }}
          />

          <g fill="none" stroke="currentColor" strokeWidth="1.5" className="text-gray-400 dark:text-white/30">
            <BodyShapes />
          </g>
        </svg>
      </motion.div>

      <div className="text-center">
        <p className="text-lg font-bold tabular-nums text-red-600 dark:text-red-400">{shown}%</p>
        <p className="text-[11px] text-gray-500 dark:text-gray-400">
          {recovering ? `${t("availability.bloodRecovering")} · ${daysLeft} ${t("availability.days")}` : t("availability.bloodFull")}
        </p>
        <div className="mx-auto mt-1.5 h-1 w-20 overflow-hidden rounded-full bg-gray-200 dark:bg-white/10">
          <motion.div
            className="h-full rounded-full bg-gradient-to-r from-red-400 to-red-600"
            initial={{ width: 0 }}
            animate={{ width: `${pct}%` }}
            transition={{ duration: 1.4, ease: "easeOut" }}
          />
        </div>
      </div>
    </div>
  );
}
