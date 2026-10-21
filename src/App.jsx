import { Suspense, lazy } from "react";
import { Routes, Route } from "react-router-dom";
import Loader from "./components/common/Loader.jsx";
import PrivateRoute from "./components/common/PrivateRoute.jsx";

/*
  IMPORTANT (Performance):
  Every page below is lazy-loaded with React.lazy(). This means the browser
  only downloads the JS for the page the user is currently visiting —
  not the entire app at once. Combined with the manualChunks split in
  vite.config.js (react/router, firebase, framer-motion separated),
  this keeps the initial bundle small and the site fast on first load.
  We will fill in each page file in the upcoming steps.
*/

// Public pages
const LandingPage = lazy(() => import("./pages/LandingPage.jsx"));
const LoginPage = lazy(() => import("./pages/auth/LoginPage.jsx"));
const RegisterPage = lazy(() => import("./pages/auth/RegisterPage.jsx"));
const ForgotPasswordPage = lazy(() => import("./pages/auth/ForgotPasswordPage.jsx"));
const CompleteProfilePage = lazy(() => import("./pages/auth/CompleteProfilePage.jsx"));

// Dashboard (role-switchable, protected)
const DashboardLayout = lazy(() => import("./layouts/DashboardLayout.jsx"));
const DashboardOverview = lazy(() => import("./pages/dashboard/DashboardOverview.jsx"));
const DonorPanel = lazy(() => import("./pages/donor/DonorPanel.jsx"));
const SeekerPanel = lazy(() => import("./pages/seeker/SeekerPanel.jsx"));

// Admin (separate chunk — only loaded for admins)
const AdminLayout = lazy(() => import("./layouts/AdminLayout.jsx"));
const AdminOverview = lazy(() => import("./pages/admin/AdminOverview.jsx"));
const AdminUsers = lazy(() => import("./pages/admin/AdminUsers.jsx"));
const AdminRequests = lazy(() => import("./pages/admin/AdminRequests.jsx"));
const AdminAnalytics = lazy(() => import("./pages/admin/AdminAnalytics.jsx"));
const AdminSettings = lazy(() => import("./pages/admin/AdminSettings.jsx"));
const AdminOrganizations = lazy(() => import("./pages/admin/AdminOrganizations.jsx"));

// 404
const NotFound = lazy(() => import("./pages/NotFound.jsx"));

function App() {
  return (
    <Suspense fallback={<Loader fullScreen />}>
      <Routes>
        {/* Public */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route
          path="/complete-profile"
          element={
            <PrivateRoute skipProfileCheck>
              <CompleteProfilePage />
            </PrivateRoute>
          }
        />

        {/* Protected user dashboard (donor/seeker role switch happens inside) */}
        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <DashboardLayout />
            </PrivateRoute>
          }
        >
          <Route index element={<DashboardOverview />} />
          <Route path="donor/*" element={<DonorPanel />} />
          <Route path="seeker/*" element={<SeekerPanel />} />
        </Route>

        {/* Protected admin panel */}
        <Route
          path="/admin"
          element={
            <PrivateRoute requiredRole="admin">
              <AdminLayout />
            </PrivateRoute>
          }
        >
          <Route index element={<AdminOverview />} />
          <Route path="users" element={<AdminUsers />} />
          <Route path="requests" element={<AdminRequests />} />
          <Route path="analytics" element={<AdminAnalytics />} />
          <Route path="settings" element={<AdminSettings />} />
          <Route path="organizations" element={<AdminOrganizations />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Routes>
    </Suspense>
  );
}

export default App;
