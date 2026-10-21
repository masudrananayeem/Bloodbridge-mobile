import { useLanguage } from "../../context/LanguageContext.jsx";

export default function LanguageToggle({ className = "" }) {
  const { language, toggleLanguage } = useLanguage();

  return (
    <button
      onClick={toggleLanguage}
      title={language === "bn" ? "Switch to English" : "বাংলায় দেখুন"}
      className={`flex h-9 items-center gap-1 rounded-full border border-gray-200 bg-white px-3 text-xs font-semibold text-gray-600 transition-colors hover:border-brand-300 dark:border-white/10 dark:bg-white/5 dark:text-gray-200 ${className}`}
    >
      <span className={language === "bn" ? "text-brand-600" : "text-gray-400"}>বাং</span>
      <span className="text-gray-300 dark:text-gray-600">|</span>
      <span className={language === "en" ? "text-brand-600" : "text-gray-400"}>EN</span>
    </button>
  );
}
