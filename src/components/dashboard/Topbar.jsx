import { useEffect, useRef, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { FiMenu, FiLogOut, FiChevronDown, FiBell, FiCheckCircle, FiXCircle, FiShield } from "react-icons/fi";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";
import ThemeToggle from "../common/ThemeToggle.jsx";
import LanguageToggle from "../common/LanguageToggle.jsx";
import RoleSwitch from "./RoleSwitch.jsx";
import { getNotifications, markAsRead } from "../../services/notificationService.js";

const iconFor = { request: FiBell, accepted: FiCheckCircle, rejected: FiXCircle, verified: FiShield, filled: FiXCircle };

export default function Topbar({ onMenuClick }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const [notifOpen, setNotifOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const [recentNotifications, setRecentNotifications] = useState([]);
  const notifRef = useRef(null);

  const mode = user?.activeMode || "donor";

  const loadNotifications = () => {
    getNotifications()
      .then(({ notifications, unreadCount }) => {
        setUnreadCount(unreadCount);
        setRecentNotifications(notifications.slice(0, 5));
      })
      .catch(() => {});
  };

  useEffect(() => {
    loadNotifications();
  }, []);

  // Close the dropdown on an outside click
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (notifRef.current && !notifRef.current.contains(e.target)) setNotifOpen(false);
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = async () => {
    await logout();
    toast.success("লগআউট হয়েছে");
    navigate("/");
  };

  const goToNotifications = () => {
    setNotifOpen(false);
    navigate(`/dashboard/${mode}/notifications`);
  };

  const handleNotificationClick = async (n) => {
    if (!n.isRead) {
      try {
        await markAsRead(n.id);
      } catch {
        /* non-critical — still navigate */
      }
    }
    goToNotifications();
  };

  return (
    <header className="sticky top-0 z-20 flex items-center justify-between border-b border-gray-100 bg-white/80 px-4 py-3 backdrop-blur-lg dark:border-white/10 dark:bg-surface-dark/80 sm:px-6">
      <div className="flex items-center gap-3">
        <button
          onClick={onMenuClick}
          className="rounded-lg p-2 text-gray-500 hover:bg-gray-100 dark:hover:bg-white/5 lg:hidden"
        >
          <FiMenu size={20} />
        </button>
        <div>
          <RoleSwitch />
        </div>
      </div>

      <div className="flex items-center gap-3">
        <div className="relative" ref={notifRef}>
          <button
            onClick={() => {
              setNotifOpen((o) => !o);
              if (!notifOpen) loadNotifications();
            }}
            className="relative flex h-10 w-10 items-center justify-center rounded-full border border-gray-200 bg-white text-gray-600 dark:border-white/10 dark:bg-white/5 dark:text-gray-200"
          >
            <FiBell size={18} />
            {unreadCount > 0 && (
              <span className="absolute -right-1 -top-1 flex h-4 w-4 items-center justify-center rounded-full bg-brand-600 text-[10px] font-bold text-white">
                {unreadCount > 9 ? "9+" : unreadCount}
              </span>
            )}
          </button>

          <AnimatePresence>
            {notifOpen && (
              <motion.div
                initial={{ opacity: 0, y: -8 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -8 }}
                transition={{ duration: 0.15 }}
                className="glass-card absolute right-0 z-20 mt-2 w-80 overflow-hidden !rounded-xl p-0"
              >
                <div className="border-b border-gray-100 px-4 py-3 dark:border-white/10">
                  <p className="text-sm font-semibold text-gray-900 dark:text-white">Notifications</p>
                </div>

                {recentNotifications.length === 0 ? (
                  <p className="px-4 py-6 text-center text-sm text-gray-400">এখনো কোনো নোটিফিকেশন নেই।</p>
                ) : (
                  <div className="max-h-80 overflow-y-auto">
                    {recentNotifications.map((n) => {
                      const Icon = iconFor[n.type] || FiBell;
                      return (
                        <button
                          key={n.id}
                          onClick={() => handleNotificationClick(n)}
                          className={`flex w-full items-start gap-3 border-b border-gray-50 px-4 py-3 text-left text-sm hover:bg-gray-50 dark:border-white/5 dark:hover:bg-white/5 ${
                            !n.isRead ? "bg-brand-50/40 dark:bg-brand-950/20" : ""
                          }`}
                        >
                          <Icon size={16} className="mt-0.5 shrink-0 text-brand-500" />
                          <span className="flex-1 text-gray-700 dark:text-gray-200">
                            {n.message}
                            <span className="mt-0.5 block text-xs text-gray-400">
                              {new Date(n.createdAt).toLocaleString()}
                            </span>
                          </span>
                        </button>
                      );
                    })}
                  </div>
                )}

                <button
                  onClick={goToNotifications}
                  className="block w-full bg-gray-50 px-4 py-2.5 text-center text-xs font-semibold text-brand-600 hover:bg-gray-100 dark:bg-white/5 dark:hover:bg-white/10"
                >
                  সব নোটিফিকেশন দেখুন
                </button>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        <LanguageToggle />
        <ThemeToggle />

        <div className="relative">
          <button
            onClick={() => setMenuOpen((o) => !o)}
            className="flex items-center gap-2 rounded-full pl-1 pr-3 py-1 hover:bg-gray-100 dark:hover:bg-white/5"
          >
            {user?.photoURL ? (
              <img src={user.photoURL} alt="" className="h-8 w-8 rounded-full object-cover" />
            ) : (
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-brand-gradient text-xs font-bold text-white">
                {user?.fullName?.charAt(0) || "U"}
              </div>
            )}
            <span className="hidden text-sm font-medium text-gray-700 dark:text-gray-200 sm:block">
              {user?.fullName || "User"}
            </span>
            <FiChevronDown size={14} className="text-gray-400" />
          </button>

          <AnimatePresence>
            {menuOpen && (
              <motion.div
                initial={{ opacity: 0, y: -8 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -8 }}
                transition={{ duration: 0.15 }}
                className="glass-card absolute right-0 z-20 mt-2 w-44 overflow-hidden !rounded-xl p-1"
              >
                <button
                  onClick={handleLogout}
                  className="flex w-full items-center gap-2 rounded-lg px-3 py-2.5 text-left text-sm text-red-500 hover:bg-red-50 dark:hover:bg-red-950/30"
                >
                  <FiLogOut size={16} /> Logout
                </button>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </header>
  );
}
