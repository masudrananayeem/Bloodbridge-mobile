import IncomingRequests from "../../components/donor/IncomingRequests.jsx";

export default function DonorRequestsPage() {
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Incoming Requests</h1>
      <IncomingRequests />
    </div>
  );
}
