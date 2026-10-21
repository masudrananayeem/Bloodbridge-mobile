import { NavLink } from "react-router-dom";
import {
  FiGrid,
  FiUser,
  FiToggleLeft,
  FiClock,
  FiInbox,
  FiMapPin,
  FiBell,
  FiSettings,
  FiSearch,
  FiAlertTriangle,
  FiBookmark,
  FiList,
  FiUsers,
} from "react-icons/fi";
import Logo from "../common/Logo.jsx";
import { useLanguage } from "../../context/LanguageContext.jsx";

const donorLinks = [
  { to: "/dashboard/donor", labelKey: "sidebar.overview", icon: FiGrid, end: true },
  { to: "/dashboard/donor/profile", labelKey: "sidebar.profile", icon: FiUser },
  { to: "/dashboard/donor/availability", labelKey: "sidebar.availability", icon: FiToggleLeft },
  { to: "/dashboard/donor/history", labelKey: "sidebar.donationHistory", icon: FiClock },
  { to: "/dashboard/donor/requests", labelKey: "sidebar.incomingRequests", icon: FiInbox },
  { to: "/dashboard/donor/nearby", labelKey: "sidebar.nearbyRequests", icon: FiMapPin },
  { to: "/dashboard/donor/organizations", labelKey: "sidebar.organizations", icon: FiUsers },
  { to: "/dashboard/donor/notifications", labelKey: "sidebar.notifications", icon: FiBell },
  { to: "/dashboard/donor/settings", labelKey: "sidebar.settings", icon: FiSettings },
];

const seekerLinks = [
  { to: "/dashboard/seeker", labelKey: "sidebar.overview", icon: FiGrid, end: true },
  { to: "/dashboard/seeker/profile", labelKey: "sidebar.profile", icon: FiUser },
  { to: "/dashboard/seeker/search", labelKey: "sidebar.searchDonor", icon: FiSearch },
  { to: "/dashboard/seeker/emergency", labelKey: "sidebar.emergencyRequest", icon: FiAlertTriangle },
  { to: "/dashboard/seeker/my-requests", labelKey: "sidebar.myRequests", icon: FiList },
  { to: "/dashboard/seeker/saved", labelKey: "sidebar.savedDonors", icon: FiBookmark },
  { to: "/dashboard/seeker/organizations", labelKey: "sidebar.organizations", icon: FiUsers },
  { to: "/dashboard/seeker/history", labelKey: "sidebar.requestHistory", icon: FiClock },
  { to: "/dashboard/seeker/notifications", labelKey: "sidebar.notifications", icon: FiBell },
  { to: "/dashboard/seeker/settings", labelKey: "sidebar.settings", icon: FiSettings },
];

export default function Sidebar({ mode, open, onClose }) {
  const { t } = useLanguage();
  const links = mode === "seeker" ? seekerLinks : donorLinks;

  return (
    <>
      {/* Mobile backdrop */}
      {open && (
        <div className="fixed inset-0 z-30 bg-black/40 lg:hidden" onClick={onClose} />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-40 w-64 shrink-0 transform border-r border-gray-100 bg-white p-5 transition-transform duration-300 dark:border-white/10 dark:bg-surface-dark lg:static lg:translate-x-0 ${
          open ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="mb-8 px-2">
          <Logo />
        </div>

        <nav className="space-y-1">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.end}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-colors ${
                  isActive
                    ? "bg-brand-gradient text-white shadow-md shadow-brand-600/25"
                    : "text-gray-600 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-white/5"
                }`
              }
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
