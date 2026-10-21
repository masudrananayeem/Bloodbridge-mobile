import AvailabilityToggle from "../../components/donor/AvailabilityToggle.jsx";

export default function DonorAvailability() {
  return (
    <div className="max-w-2xl space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Availability</h1>
      <AvailabilityToggle />
    </div>
  );
}
