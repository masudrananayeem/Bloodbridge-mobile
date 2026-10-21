import { Routes, Route } from "react-router-dom";
import SeekerOverview from "./SeekerOverview.jsx";
import SeekerProfile from "./SeekerProfile.jsx";
import SeekerSearchPage from "./SeekerSearchPage.jsx";
import SeekerEmergencyPage from "./SeekerEmergencyPage.jsx";
import SeekerMyRequestsPage from "./SeekerMyRequestsPage.jsx";
import SeekerSavedPage from "./SeekerSavedPage.jsx";
import SeekerOrganizationsPage from "./SeekerOrganizationsPage.jsx";
import SeekerHistoryPage from "./SeekerHistoryPage.jsx";
import SeekerNotificationsPage from "./SeekerNotificationsPage.jsx";
import SeekerSettingsPage from "./SeekerSettingsPage.jsx";

// Mounted at /dashboard/seeker/* by App.jsx — handles all Seeker sub-pages
export default function SeekerPanel() {
  return (
    <Routes>
      <Route index element={<SeekerOverview />} />
      <Route path="profile" element={<SeekerProfile />} />
      <Route path="search" element={<SeekerSearchPage />} />
      <Route path="emergency" element={<SeekerEmergencyPage />} />
      <Route path="my-requests" element={<SeekerMyRequestsPage />} />
      <Route path="saved" element={<SeekerSavedPage />} />
      <Route path="organizations" element={<SeekerOrganizationsPage />} />
      <Route path="history" element={<SeekerHistoryPage />} />
      <Route path="notifications" element={<SeekerNotificationsPage />} />
      <Route path="settings" element={<SeekerSettingsPage />} />
    </Routes>
  );
}
