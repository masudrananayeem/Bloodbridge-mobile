import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import Logo from "../common/Logo.jsx";
import ThemeToggle from "../common/ThemeToggle.jsx";
import LanguageToggle from "../common/LanguageToggle.jsx";
import { useAuth } from "../../context/AuthContext.jsx";
import { useLanguage } from "../../context/LanguageContext.jsx";

export default function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const { user } = useAuth();
  const { t } = useLanguage();

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 12);
    window.addEventListener("scroll", onScroll);
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <motion.header
      initial={{ y: -80, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.5, ease: "easeOut" }}
      className={`sticky top-0 z-50 transition-all duration-300 ${
        scrolled
          ? "bg-white/80 dark:bg-surface-dark/80 backdrop-blur-lg shadow-md"
          : "bg-transparent"
      }`}
    >
      <nav className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
        <Logo />

        <div className="flex items-center gap-3">
          <LanguageToggle className="hidden sm:flex" />
          <ThemeToggle />
          {user ? (
            <Link to={user.role === "admin" ? "/admin" : "/dashboard"} className="btn-primary !px-5 !py-2.5 text-sm">
              {t("nav.dashboard")}
            </Link>
          ) : (
            <>
              <Link
                to="/register"
                className="hidden text-sm font-semibold text-gray-600 hover:text-brand-600 dark:text-gray-300 sm:block"
              >
                {t("nav.register")}
              </Link>
              <Link to="/login" className="btn-primary !px-5 !py-2.5 text-sm">
                {t("nav.login")}
              </Link>
            </>
          )}
        </div>
      </nav>
    </motion.header>
  );
}
