import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { FiArrowRight, FiSearch } from "react-icons/fi";
import { useLanguage } from "../../context/LanguageContext.jsx";

const container = {
  hidden: {},
  show: {
    transition: { staggerChildren: 0.15, delayChildren: 0.2 },
  },
};

const item = {
  hidden: { opacity: 0, y: 24 },
  show: { opacity: 1, y: 0, transition: { duration: 0.6, ease: "easeOut" } },
};

export default function Hero() {
  const { t } = useLanguage();

  return (
    <section className="relative overflow-hidden bg-white dark:bg-surface-dark">
      {/* Soft light backdrop with a faint red glow, instead of a dark theme */}
      <div className="pointer-events-none absolute inset-0 -z-20 bg-[radial-gradient(ellipse_at_top_right,_#fee2e2_0%,_#ffffff_55%,_#ffffff_100%)] dark:bg-[radial-gradient(ellipse_at_top_right,_#3b0a0a_0%,_#0b0b12_55%,_#000000_100%)]" />

      {/* Abstract, stylised Bangladesh map silhouette — pure decoration,
          giving the hero a local sense of place. */}
      <svg
        viewBox="0 0 400 500"
        className="pointer-events-none absolute -right-16 top-1/2 -z-10 h-[140%] w-auto -translate-y-1/2 opacity-[0.05] dark:opacity-[0.07]"
        fill="none"
      >
        <path
          d="M180 20 C210 40 220 70 200 95 C230 105 250 130 240 160 C270 165 290 190 275 220 C300 235 310 265 285 290 C310 310 300 345 270 355 C280 385 260 415 225 415 C220 445 190 465 160 450 C130 470 95 460 85 430 C55 435 35 410 45 380 C20 370 15 340 35 320 C15 300 20 270 45 255 C30 230 45 200 75 195 C65 165 85 135 115 135 C110 105 130 75 160 75 C155 45 165 20 180 20 Z"
          className="fill-red-900 dark:fill-white"
        />
      </svg>

      {/* Faint dot-grid texture */}
      <div
        className="pointer-events-none absolute inset-0 -z-10 opacity-[0.35] dark:opacity-[0.15]"
        style={{
          backgroundImage: "radial-gradient(rgba(0,0,0,0.06) 1px, transparent 1px)",
          backgroundSize: "28px 28px",
        }}
      />

      <div className="pointer-events-none absolute inset-0 -z-10">
        <div className="absolute -top-32 right-0 h-96 w-96 rounded-full bg-red-300/30 blur-3xl dark:bg-red-600/20" />
        <div className="absolute bottom-0 left-0 h-72 w-72 rounded-full bg-red-200/30 blur-3xl dark:bg-red-900/30" />
      </div>

      <div className="mx-auto grid max-w-7xl grid-cols-1 items-center gap-12 px-4 py-20 sm:px-6 lg:grid-cols-2 lg:px-8 lg:py-28">
        {/* Text column */}
        <motion.div variants={container} initial="hidden" animate="show">
          <motion.span
            variants={item}
            className="mb-5 inline-flex items-center gap-2 rounded-full border border-red-200 bg-red-50 px-4 py-1.5 text-xs font-semibold text-red-700 dark:border-red-800/60 dark:bg-red-950/40 dark:text-red-300"
          >
            🩸 {t("hero.badge")}
          </motion.span>

          <motion.h1
            variants={item}
            className="text-4xl font-extrabold leading-tight text-gray-900 dark:text-white sm:text-5xl lg:text-6xl"
          >
            {t("hero.title")}
            <br />
            <span className="bg-gradient-to-r from-red-500 to-red-700 bg-clip-text text-transparent">
              {t("hero.titleHighlight")}
            </span>
          </motion.h1>

          <motion.p variants={item} className="mt-6 max-w-lg text-lg text-gray-600 dark:text-gray-300">
            {t("hero.subtitle")}
          </motion.p>

          <motion.div variants={item} className="mt-8 flex flex-wrap items-center gap-4">
            <Link to="/register" className="btn-primary">
              {t("hero.ctaDonor")} <FiArrowRight />
            </Link>
            <Link
              to="/login"
              className="flex items-center gap-2 rounded-full border border-gray-200 px-5 py-2.5 text-sm font-semibold text-gray-700 hover:bg-gray-50 dark:border-white/15 dark:text-gray-200 dark:hover:bg-white/5"
            >
              <FiSearch size={15} /> {t("hero.ctaSeeker")}
            </Link>
          </motion.div>

          {/* Quick stats — grounds the hero in real numbers rather than
              just tagline copy. */}
          <motion.div
            variants={item}
            className="mt-10 grid max-w-md grid-cols-3 gap-4 border-t border-gray-200 pt-6 dark:border-white/10"
          >
            <div>
              <p className="text-2xl font-bold text-gray-900 dark:text-white">৫০০+</p>
              <p className="text-xs text-gray-500 dark:text-gray-400">{t("hero.statDonors")}</p>
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900 dark:text-white">২০০+</p>
              <p className="text-xs text-gray-500 dark:text-gray-400">{t("hero.statLives")}</p>
            </div>
            <div>
              <p className="text-2xl font-bold text-gray-900 dark:text-white">৬৪</p>
              <p className="text-xs text-gray-500 dark:text-gray-400">{t("hero.statDistricts")}</p>
            </div>
          </motion.div>
        </motion.div>

        {/* Signature illustration — a self-contained graphic (not a static
            photo file) so it can genuinely flip from white to dark the
            instant the theme toggles, instead of a fixed image. */}
        <motion.div
          initial={{ opacity: 0, scale: 0.92 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.7, ease: "easeOut", delay: 0.3 }}
          className="relative mx-auto flex h-72 w-full max-w-md items-center justify-center sm:h-96"
        >
          <div className="relative flex h-full w-full items-center justify-center overflow-hidden rounded-3xl border border-gray-100 bg-white shadow-xl shadow-gray-200/60 transition-colors duration-300 dark:border-white/10 dark:bg-gray-900 dark:shadow-black/40">
            {/* Adaptive decorative pattern inside the card itself */}
            <div
              className="pointer-events-none absolute inset-0 opacity-[0.4] dark:opacity-[0.25]"
              style={{
                backgroundImage:
                  "radial-gradient(rgba(220,38,38,0.12) 1.5px, transparent 1.5px)",
                backgroundSize: "24px 24px",
              }}
            />
            <div className="pointer-events-none absolute -right-10 -top-10 h-40 w-40 rounded-full bg-red-100 blur-2xl dark:bg-red-600/10" />
            <div className="pointer-events-none absolute -bottom-10 -left-10 h-40 w-40 rounded-full bg-red-50 blur-2xl dark:bg-red-900/20" />

            <svg viewBox="0 0 400 200" className="relative w-[90%]" fill="none">
              <motion.path
                d="M0 100 H120 L140 40 L165 160 L190 70 L210 100 H400"
                stroke="#ef4444"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
                initial={{ pathLength: 0 }}
                animate={{ pathLength: 1 }}
                transition={{ duration: 2, ease: "easeInOut", delay: 0.6 }}
              />
            </svg>

            {/* Blood drop marker riding the pulse line */}
            <motion.div
              className="absolute left-[41%] top-[18%]"
              animate={{ y: [0, -6, 0] }}
              transition={{ duration: 2.4, repeat: Infinity, ease: "easeInOut" }}
            >
              <svg width="28" height="28" viewBox="0 0 34 34" fill="none">
                <path
                  d="M17 3C17 3 6 15.5 6 22.5C6 28.85 10.9 33 17 33C23.1 33 28 28.85 28 22.5C28 15.5 17 3 17 3Z"
                  fill="#ef4444"
                />
              </svg>
            </motion.div>
          </div>
        </motion.div>
      </div>
    </section>
  );
}
