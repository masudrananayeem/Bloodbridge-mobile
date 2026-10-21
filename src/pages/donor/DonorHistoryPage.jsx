import DonationHistory from "../../components/donor/DonationHistory.jsx";

export default function DonorHistoryPage() {
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Donation History</h1>
      <DonationHistory />
    </div>
  );
}
