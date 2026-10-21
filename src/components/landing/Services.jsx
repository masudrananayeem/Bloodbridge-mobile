import { motion } from "framer-motion";
import {
  FiUserPlus,
  FiSearch,
  FiAlertTriangle,
  FiCheckCircle,
  FiMapPin,
  FiClock,
  FiBell,
  FiUserCheck,
} from "react-icons/fi";
import { useLanguage } from "../../context/LanguageContext.jsx";

const icons = [FiUserPlus, FiUserCheck, FiSearch, FiAlertTriangle, FiCheckCircle, FiMapPin, FiClock, FiBell];

export default function Services() {
  const { t } = useLanguage();
  const services = t("landing.servicesList");

  return (
    <section className="section-container">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, amount: 0.4 }}
        transition={{ duration: 0.5 }}
        className="mx-auto mb-14 max-w-2xl text-center"
      >
        <h2 className="text-3xl font-bold text-gray-900 dark:text-white sm:text-4xl">{t("landing.servicesTitle")}</h2>
      </motion.div>

      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {services.map((title, i) => {
          const Icon = icons[i];
          return (
            <motion.div
              key={title}
              initial={{ opacity: 0, y: 24 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, amount: 0.3 }}
              transition={{ duration: 0.45, delay: (i % 4) * 0.08 }}
              className="glass-card group flex flex-col items-start gap-4 p-6 transition-transform duration-300 hover:-translate-y-1"
            >
              <div className="flex h-11 w-11 items-center justify-center rounded-lg bg-brand-gradient text-white transition-transform group-hover:rotate-6">
                <Icon size={20} />
              </div>
              <h3 className="font-semibold text-gray-900 dark:text-white">{title}</h3>
            </motion.div>
          );
        })}
      </div>
    </section>
  );
}
