import OrganizationsList from "../../components/common/OrganizationsList.jsx";

export default function DonorOrganizationsPage() {
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Blood Donor Organizations</h1>
      <OrganizationsList />
    </div>
  );
}
