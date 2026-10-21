import { motion } from "framer-motion";
import { FiUserPlus, FiEdit3, FiRepeat, FiHeart } from "react-icons/fi";
import { useLanguage } from "../../context/LanguageContext.jsx";

const icons = [FiUserPlus, FiEdit3, FiRepeat, FiHeart];

export default function HowItWorks() {
  const { t } = useLanguage();
  const steps = t("landing.howItWorksSteps");

  return (
    <section id="how-it-works" className="bg-gray-50 dark:bg-white/[0.02]">
      <div className="section-container">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.4 }}
          transition={{ duration: 0.5 }}
          className="mx-auto mb-16 max-w-2xl text-center"
        >
          <h2 className="text-3xl font-bold text-gray-900 dark:text-white sm:text-4xl">
            {t("landing.howItWorksTitle")}
          </h2>
        </motion.div>

        <div className="relative grid grid-cols-1 gap-10 sm:grid-cols-2 lg:grid-cols-4">
          {/* Connecting line — draws once as the section scrolls into view */}
          <motion.div
            initial={{ scaleX: 0 }}
            whileInView={{ scaleX: 1 }}
            viewport={{ once: true, amount: 0.5 }}
            transition={{ duration: 1, ease: "easeInOut" }}
            style={{ transformOrigin: "left" }}
            className="absolute left-0 right-0 top-8 hidden h-0.5 bg-brand-200 dark:bg-brand-900 lg:block"
          />

          {steps.map((step, i) => {
            const Icon = icons[i];
            return (
              <motion.div
                key={step.title}
                initial={{ opacity: 0, y: 24 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, amount: 0.4 }}
                transition={{ duration: 0.5, delay: i * 0.15 }}
                className="relative flex flex-col items-center text-center"
              >
                <div className="relative z-10 mb-5 flex h-16 w-16 items-center justify-center rounded-full bg-brand-gradient text-white shadow-lg shadow-brand-600/30">
                  <Icon size={26} />
                </div>
                <span className="mb-1 text-xs font-bold uppercase tracking-wider text-brand-600">
                  {t("landing.step")} {i + 1}
                </span>
                <h3 className="mb-2 font-semibold text-gray-900 dark:text-white">{step.title}</h3>
                <p className="max-w-[220px] text-sm text-gray-500 dark:text-gray-400">{step.desc}</p>
              </motion.div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
