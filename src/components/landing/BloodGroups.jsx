import { motion } from "framer-motion";
import { useLanguage } from "../../context/LanguageContext.jsx";

const groups = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];

export default function BloodGroups() {
  const { t } = useLanguage();
  return (
    <section className="section-container">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, amount: 0.4 }}
        transition={{ duration: 0.5 }}
        className="mx-auto mb-14 max-w-2xl text-center"
      >
        <h2 className="text-3xl font-bold text-gray-900 dark:text-white sm:text-4xl">{t("landing.bloodGroupsTitle")}</h2>
        <p className="mt-4 text-gray-500 dark:text-gray-400">{t("landing.bloodGroupsSubtitle")}</p>
      </motion.div>

      <div className="grid grid-cols-4 gap-4 sm:gap-6 lg:grid-cols-8">
        {groups.map((g, i) => (
          <motion.div
            key={g}
            initial={{ opacity: 0, scale: 0.7 }}
            whileInView={{ opacity: 1, scale: 1 }}
            viewport={{ once: true, amount: 0.3 }}
            transition={{ duration: 0.4, delay: i * 0.06 }}
            whileHover={{ scale: 1.08, rotate: -2 }}
            className="flex aspect-square cursor-pointer flex-col items-center justify-center rounded-2xl bg-brand-gradient text-white shadow-lg shadow-brand-600/20"
          >
            <span className="text-2xl font-extrabold sm:text-3xl">{g}</span>
          </motion.div>
        ))}
      </div>
    </section>
  );
}
