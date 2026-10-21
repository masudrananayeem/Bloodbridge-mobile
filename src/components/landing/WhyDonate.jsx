import { motion } from "framer-motion";
import { FiDroplet, FiAlertCircle, FiUsers, FiHeart } from "react-icons/fi";
import { useLanguage } from "../../context/LanguageContext.jsx";

const icons = [FiDroplet, FiAlertCircle, FiUsers, FiHeart];

// Animate in once when scrolled into view; never replay on scroll-up
const cardVariant = {
  hidden: { opacity: 0, y: 30 },
  show: (i) => ({
    opacity: 1,
    y: 0,
    transition: { duration: 0.5, delay: i * 0.1, ease: "easeOut" },
  }),
};

export default function WhyDonate() {
  const { t } = useLanguage();
  const stats = t("landing.whyDonateStats");

  return (
    <section className="section-container">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, amount: 0.4 }}
        transition={{ duration: 0.5 }}
        className="mx-auto mb-14 max-w-2xl text-center"
      >
        <h2 className="text-3xl font-bold text-gray-900 dark:text-white sm:text-4xl">
          {t("landing.whyDonateTitle")}
        </h2>
        <p className="mt-4 text-gray-500 dark:text-gray-400">{t("landing.whyDonateSubtitle")}</p>
      </motion.div>

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((s, i) => {
          const Icon = icons[i];
          return (
            <motion.div
              key={s.title}
              custom={i}
              initial="hidden"
              whileInView="show"
              viewport={{ once: true, amount: 0.3 }}
              variants={cardVariant}
              className="glass-card p-6 transition-transform duration-300 hover:-translate-y-1"
            >
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-brand-gradient text-white">
                <Icon size={22} />
              </div>
              <h3 className="mb-2 font-semibold text-gray-900 dark:text-white">{s.title}</h3>
              <p className="text-sm leading-relaxed text-gray-500 dark:text-gray-400">{s.desc}</p>
            </motion.div>
          );
        })}
      </div>
    </section>
  );
}
