import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import toast from "react-hot-toast";
import { FiShield, FiTrash2, FiSearch } from "react-icons/fi";
import { getAllUsers, toggleVerifyUser, deleteUser } from "../../services/adminService.js";
import Loader from "../../components/common/Loader.jsx";

export default function AdminUsers() {
  const [searchParams, setSearchParams] = useSearchParams();
  const mode = searchParams.get("mode") || "";

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  const load = () => {
    setLoading(true);
    getAllUsers({ mode: mode || undefined, search: search || undefined })
      .then(({ users }) => setUsers(users))
      .catch(() => setUsers([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, [mode]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleSearch = (e) => {
    e.preventDefault();
    load();
  };

  const handleVerify = async (id) => {
    try {
      const { isVerified } = await toggleVerifyUser(id);
      setUsers((prev) => prev.map((u) => (u.id === id ? { ...u, isVerified } : u)));
      toast.success(isVerified ? "ইউজার ভেরিফাই করা হয়েছে" : "ভেরিফিকেশন সরানো হয়েছে");
    } catch {
      toast.error("করা যায়নি");
    }
  };

  const handleDelete = async (id) => {
    if (!confirm("এই ইউজারকে স্থায়ীভাবে ডিলিট করবেন?")) return;
    try {
      await deleteUser(id);
      setUsers((prev) => prev.filter((u) => u.id !== id));
      toast.success("ইউজার ডিলিট করা হয়েছে");
    } catch {
      toast.error("ডিলিট করা যায়নি");
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
          {mode === "donor" ? "Manage Donors" : mode === "seeker" ? "Manage Seekers" : "Manage Users"}
        </h1>

        <div className="flex gap-2">
          {["", "donor", "seeker"].map((m) => (
            <button
              key={m || "all"}
              onClick={() => setSearchParams(m ? { mode: m } : {})}
              className={`rounded-full px-4 py-1.5 text-sm font-medium capitalize transition-colors ${
                mode === m
                  ? "bg-brand-gradient text-white"
                  : "border border-gray-200 text-gray-600 dark:border-white/10 dark:text-gray-300"
              }`}
            >
              {m || "All"}
            </button>
          ))}
        </div>
      </div>

      <form onSubmit={handleSearch} className="flex max-w-md gap-2">
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="নাম, ইমেইল বা ফোন দিয়ে খুঁজুন"
          className="w-full rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100 dark:border-white/10 dark:bg-white/5 dark:text-white"
        />
        <button type="submit" className="btn-primary !px-4">
          <FiSearch />
        </button>
      </form>

      {loading ? (
        <Loader />
      ) : (
        <div className="glass-card overflow-x-auto p-2">
          <table className="w-full min-w-[700px] text-left text-sm">
            <thead>
              <tr className="text-xs uppercase tracking-wider text-gray-400">
                <th className="px-4 py-3">Name</th>
                <th className="px-4 py-3">Blood Group</th>
                <th className="px-4 py-3">Location</th>
                <th className="px-4 py-3">Mode</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="border-t border-gray-100 dark:border-white/10">
                  <td className="px-4 py-3">
                    <p className="font-medium text-gray-900 dark:text-white">{u.fullName}</p>
                    <p className="text-xs text-gray-400">{u.email}</p>
                  </td>
                  <td className="px-4 py-3">
                    {u.profileComplete === false ? (
                      <span className="rounded-full bg-amber-50 px-2.5 py-1 text-xs font-semibold text-amber-600 dark:bg-amber-950/40">
                        Incomplete Profile
                      </span>
                    ) : (
                      <span className="rounded-full bg-brand-50 px-2.5 py-1 text-xs font-semibold text-brand-700 dark:bg-brand-950/50 dark:text-brand-300">
                        {u.bloodGroup}
                      </span>
                    )}
                  </td>
                  <td className="px-4 py-3 text-gray-500 dark:text-gray-400">
                    {u.profileComplete === false ? "—" : `${u.upazila}, ${u.district}`}
                  </td>
                  <td className="px-4 py-3 capitalize text-gray-500 dark:text-gray-400">{u.activeMode}</td>
                  <td className="px-4 py-3">
                    {u.isVerified ? (
                      <span className="flex items-center gap-1 text-xs font-semibold text-blue-600">
                        <FiShield size={12} /> Verified
                      </span>
                    ) : (
                      <span className="text-xs text-gray-400">Unverified</span>
                    )}
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex justify-end gap-2">
                      <button
                        onClick={() => handleVerify(u.id)}
                        disabled={u.profileComplete === false}
                        title={u.profileComplete === false ? "Profile isn't complete yet" : ""}
                        className="rounded-lg border border-gray-200 px-3 py-1.5 text-xs font-semibold text-gray-600 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40 dark:border-white/10 dark:text-gray-300 dark:hover:bg-white/5"
                      >
                        {u.isVerified ? "Unverify" : "Verify"}
                      </button>
                      <button
                        onClick={() => handleDelete(u.id)}
                        className="rounded-lg border border-red-200 p-1.5 text-red-500 hover:bg-red-50 dark:border-red-900/50 dark:hover:bg-red-950/30"
                      >
                        <FiTrash2 size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {users.length === 0 && (
            <p className="p-8 text-center text-gray-400">কোনো ইউজার পাওয়া যায়নি।</p>
          )}
        </div>
      )}
    </div>
  );
}
