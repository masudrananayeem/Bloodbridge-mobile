import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";

// /dashboard (no sub-path) redirects straight into whichever mode
// (donor/seeker) the user was last in — the actual dashboards live
// at /dashboard/donor/* and /dashboard/seeker/*.
export default function DashboardOverview() {
  const { user } = useAuth();
  const mode = user?.activeMode || "donor";
  return <Navigate to={`/dashboard/${mode}`} replace />;
}
