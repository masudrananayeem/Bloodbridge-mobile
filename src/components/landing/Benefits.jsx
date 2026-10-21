import { motion } from "framer-motion";
import { FiHeart, FiActivity, FiUsers, FiZap } from "react-icons/fi";
import { useLanguage } from "../../context/LanguageContext.jsx";

const icons = [FiHeart, FiActivity, FiUsers, FiZap];

export default function Benefits() {
  const { t } = useLanguage();
  const benefits = t("landing.benefitsList");

  return (
    <section className="bg-gray-50 dark:bg-white/[0.02]">
      <div className="section-container">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.4 }}
          transition={{ duration: 0.5 }}
          className="mx-auto mb-14 max-w-2xl text-center"
        >
          <h2 className="text-3xl font-bold text-gray-900 dark:text-white sm:text-4xl">
            {t("landing.benefitsTitle")}
          </h2>
        </motion.div>

        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {benefits.map((b, i) => {
            const Icon = icons[i];
            return (
              <motion.div
                key={b.title}
                initial={{ opacity: 0, scale: 0.9 }}
                whileInView={{ opacity: 1, scale: 1 }}
                viewport={{ once: true, amount: 0.3 }}
                transition={{ duration: 0.4, delay: i * 0.1 }}
                whileHover={{ y: -6 }}
                className="rounded-2xl bg-white p-6 text-center shadow-md dark:bg-white/5"
              >
                <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-brand-50 text-brand-600 dark:bg-brand-950/50 dark:text-brand-400">
                  <Icon size={26} />
                </div>
                <h3 className="mb-1 font-semibold text-gray-900 dark:text-white">{b.title}</h3>
                <p className="text-sm text-gray-500 dark:text-gray-400">{b.desc}</p>
              </motion.div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
