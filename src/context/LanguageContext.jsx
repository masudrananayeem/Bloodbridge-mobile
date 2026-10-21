import { createContext, useContext, useEffect, useMemo, useState } from "react";
import translations from "../i18n/translations.js";

const LanguageContext = createContext(null);

const STORAGE_KEY = "bloodbridge_language";

export default function LanguageProvider({ children }) {
  const [language, setLanguage] = useState(() => localStorage.getItem(STORAGE_KEY) || "bn");

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, language);
    document.documentElement.lang = language;
  }, [language]);

  const toggleLanguage = () => setLanguage((prev) => (prev === "bn" ? "en" : "bn"));

  // t("nav.login") -> looks up translations[language]["nav.login"], falling
  // back to English, then to the raw key itself so a missing translation
  // never breaks the UI — it just shows in English (or the key) instead.
  const t = useMemo(() => {
    return (key) => translations[language]?.[key] ?? translations.en?.[key] ?? key;
  }, [language]);

  const value = { language, setLanguage, toggleLanguage, t };

  return <LanguageContext.Provider value={value}>{children}</LanguageContext.Provider>;
}

export const useLanguage = () => {
  const ctx = useContext(LanguageContext);
  if (!ctx) throw new Error("useLanguage must be used within a LanguageProvider");
  return ctx;
};
