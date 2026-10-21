import EmergencyRequestForm from "../../components/seeker/EmergencyRequestForm.jsx";

export default function SeekerEmergencyPage() {
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Emergency Request</h1>
      <EmergencyRequestForm />
    </div>
  );
}
