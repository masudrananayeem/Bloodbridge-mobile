import NearbyRequests from "../../components/donor/NearbyRequests.jsx";

export default function DonorNearbyPage() {
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Nearby Requests</h1>
      <NearbyRequests />
    </div>
  );
}
