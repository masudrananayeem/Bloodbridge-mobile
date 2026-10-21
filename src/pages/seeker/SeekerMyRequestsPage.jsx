import RequestsList from "../../components/seeker/RequestsList.jsx";

export default function SeekerMyRequestsPage() {
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white">My Requests</h1>
      <RequestsList
        statuses={["pending", "accepted"]}
        emptyText="এই মুহূর্তে কোনো সক্রিয় রিকোয়েস্ট নেই।"
      />
    </div>
  );
}
