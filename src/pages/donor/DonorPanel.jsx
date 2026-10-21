import { Routes, Route } from "react-router-dom";
import DonorOverview from "./DonorOverview.jsx";
import DonorProfile from "./DonorProfile.jsx";
import DonorAvailability from "./DonorAvailability.jsx";
import DonorHistoryPage from "./DonorHistoryPage.jsx";
import DonorRequestsPage from "./DonorRequestsPage.jsx";
import DonorNearbyPage from "./DonorNearbyPage.jsx";
import DonorOrganizationsPage from "./DonorOrganizationsPage.jsx";
import DonorNotificationsPage from "./DonorNotificationsPage.jsx";
import DonorSettingsPage from "./DonorSettingsPage.jsx";

// Mounted at /dashboard/donor/* by App.jsx — handles all Donor sub-pages
export default function DonorPanel() {
  return (
    <Routes>
      <Route index element={<DonorOverview />} />
      <Route path="profile" element={<DonorProfile />} />
      <Route path="availability" element={<DonorAvailability />} />
      <Route path="history" element={<DonorHistoryPage />} />
      <Route path="requests" element={<DonorRequestsPage />} />
      <Route path="nearby" element={<DonorNearbyPage />} />
      <Route path="organizations" element={<DonorOrganizationsPage />} />
      <Route path="notifications" element={<DonorNotificationsPage />} />
      <Route path="settings" element={<DonorSettingsPage />} />
    </Routes>
  );
}
