import { NavLink } from "react-router-dom";
import {
  FiGrid,
  FiUsers,
  FiDroplet,
  FiSearch,
  FiClipboard,
  FiCheckCircle,
  FiBarChart2,
  FiSettings,
  FiHeart,
} from "react-icons/fi";
import Logo from "../common/Logo.jsx";
import { useLanguage } from "../../context/LanguageContext.jsx";

const links = [
  { to: "/admin", labelKey: "admin.dashboard", icon: FiGrid, end: true },
  { to: "/admin/users", labelKey: "admin.manageUsers", icon: FiUsers, end: true },
  { to: "/admin/users?mode=donor", labelKey: "admin.manageDonors", icon: FiDroplet, matchTo: "/admin/users" },
  { to: "/admin/users?mode=seeker", labelKey: "admin.manageSeekers", icon: FiSearch, matchTo: "/admin/users" },
  { to: "/admin/requests", labelKey: "admin.manageRequests", icon: FiClipboard },
  { to: "/admin/requests?status=pending", labelKey: "admin.approveRequests", icon: FiCheckCircle, matchTo: "/admin/requests" },
  { to: "/admin/organizations", labelKey: "admin.organizations", icon: FiHeart },
  { to: "/admin/analytics", labelKey: "admin.analytics", icon: FiBarChart2 },
  { to: "/admin/settings", labelKey: "admin.settings", icon: FiSettings },
];

export default function AdminSidebar({ open, onClose }) {
  const { t } = useLanguage();

  return (
    <>
      {open && <div className="fixed inset-0 z-30 bg-black/40 lg:hidden" onClick={onClose} />}

      <aside
        className={`fixed inset-y-0 left-0 z-40 w-64 shrink-0 transform border-r border-gray-100 bg-white p-5 transition-transform duration-300 dark:border-white/10 dark:bg-surface-dark lg:static lg:translate-x-0 ${
          open ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="mb-2 px-2">
          <Logo />
        </div>
        <p className="mb-6 px-2 text-xs font-semibold uppercase tracking-wider text-gray-400">Admin Panel</p>

        <nav className="space-y-1">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.end}
              onClick={onClose}
              className={({ isActive }) => {
                // For query-param variants, react-router's own `isActive` only
                // matches the pathname — good enough here since each nav item
                // still routes to a real, distinct page.
                return `flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-colors ${
                  isActive
                    ? "bg-brand-gradient text-white shadow-md shadow-brand-600/25"
                    : "text-gray-600 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-white/5"
                }`;
              }}
            >
              <link.icon size={18} />
              {t(link.labelKey)}
            </NavLink>
          ))}
        </nav>
      </aside>
    </>
  );
}
