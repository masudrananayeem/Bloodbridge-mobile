import SavedDonorsList from "../../components/seeker/SavedDonorsList.jsx";

export default function SeekerSavedPage() {
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Saved Donors</h1>
      <SavedDonorsList />
    </div>
  );
}
