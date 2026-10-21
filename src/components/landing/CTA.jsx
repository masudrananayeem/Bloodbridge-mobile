import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { FiArrowRight } from "react-icons/fi";
import { useLanguage } from "../../context/LanguageContext.jsx";

export default function CTA() {
  const { t } = useLanguage();
  return (
    <section className="px-4 sm:px-6 lg:px-8">
      <motion.div
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, amount: 0.4 }}
        transition={{ duration: 0.6 }}
        className="relative mx-auto max-w-6xl overflow-hidden rounded-3xl bg-brand-gradient px-8 py-16 text-center shadow-2xl shadow-brand-600/30 sm:px-16"
      >
        {/* Subtle pulsing glow behind the text, echoing the heartbeat motif */}
        <div className="pointer-events-none absolute inset-0">
          <div className="absolute left-1/2 top-1/2 h-64 w-64 -translate-x-1/2 -translate-y-1/2 rounded-full bg-white/10 blur-3xl animate-pulseSlow" />
        </div>

        <h2 className="relative text-3xl font-extrabold text-white sm:text-4xl">
          {t("landing.ctaTitle")}
        </h2>
        <p className="relative mx-auto mt-4 max-w-xl text-brand-50/90">{t("landing.ctaSubtitle")}</p>
        <Link
          to="/login"
          className="relative mt-8 inline-flex items-center gap-2 rounded-full bg-white px-8 py-3 font-semibold text-brand-700 shadow-lg transition-transform duration-200 hover:scale-105"
        >
          {t("landing.ctaButton")} <FiArrowRight />
        </Link>
      </motion.div>
    </section>
  );
}
