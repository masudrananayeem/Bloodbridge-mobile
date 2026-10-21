import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { FiPlus, FiEdit2, FiTrash2, FiShield, FiX, FiLoader } from "react-icons/fi";
import {
  getOrganizations,
  createOrganization,
  updateOrganization,
  toggleVerifyOrganization,
  deleteOrganization,
} from "../../services/organizationService.js";
import { uploadProfileImage } from "../../services/uploadImage.js";
import districts from "../../utils/districts.js";
import Loader from "../../components/common/Loader.jsx";

const TYPES = ["Voluntary Group", "NGO", "Blood Bank", "Hospital"];

const emptyForm = {
  name: "",
  type: "Voluntary Group",
  description: "",
  district: "",
  upazila: "",
  address: "",
  phone: "",
  email: "",
  website: "",
  logoURL: "",
};

export default function AdminOrganizations() {
  const [organizations, setOrganizations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [logoFile, setLogoFile] = useState(null);
  const [saving, setSaving] = useState(false);

  const load = () => {
    setLoading(true);
    getOrganizations()
      .then(({ organizations }) => setOrganizations(organizations))
      .catch(() => setOrganizations([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const openCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setLogoFile(null);
    setFormOpen(true);
  };

  const openEdit = (org) => {
    setEditingId(org.id);
    setForm({ ...emptyForm, ...org });
    setLogoFile(null);
    setFormOpen(true);
  };

  const handleChange = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      let logoURL = form.logoURL;
      if (logoFile) logoURL = await uploadProfileImage(logoFile);
      const payload = { ...form, logoURL };

      if (editingId) {
        const { organization } = await updateOrganization(editingId, payload);
        setOrganizations((prev) => prev.map((o) => (o.id === editingId ? organization : o)));
        toast.success("তথ্য আপডেট হয়েছে");
      } else {
        const { organization } = await createOrganization(payload);
        setOrganizations((prev) => [organization, ...prev]);
        toast.success("সংগঠন যোগ করা হয়েছে");
      }
      setFormOpen(false);
    } catch (err) {
      toast.error(err?.response?.data?.message || "সেভ করা যায়নি");
    } finally {
      setSaving(false);
    }
  };

  const handleVerify = async (id) => {
    try {
      const { organization } = await toggleVerifyOrganization(id);
      setOrganizations((prev) => prev.map((o) => (o.id === id ? organization : o)));
      toast.success(organization.isVerified ? "ভেরিফাই করা হয়েছে" : "ভেরিফিকেশন সরানো হয়েছে");
    } catch {
      toast.error("করা যায়নি");
    }
  };

  const handleDelete = async (id) => {
    if (!confirm("এই সংগঠনটি স্থায়ীভাবে ডিলিট করবেন?")) return;
    try {
      await deleteOrganization(id);
      setOrganizations((prev) => prev.filter((o) => o.id !== id));
      toast.success("ডিলিট করা হয়েছে");
    } catch {
      toast.error("ডিলিট করা যায়নি");
    }
  };

  const inputClass =
    "w-full rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm text-gray-900 outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100 dark:border-white/10 dark:bg-white/5 dark:text-white";

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Blood Donor Organizations</h1>
        <button onClick={openCreate} className="btn-primary">
          <FiPlus /> Add Organization
        </button>
      </div>

      {loading && <Loader />}

      {!loading && (
        <div className="glass-card overflow-x-auto p-0">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-gray-100 text-xs uppercase text-gray-400 dark:border-white/10">
              <tr>
                <th className="px-5 py-3">Name</th>
                <th className="px-5 py-3">Type</th>
                <th className="px-5 py-3">District</th>
                <th className="px-5 py-3">Phone</th>
                <th className="px-5 py-3">Status</th>
                <th className="px-5 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {organizations.map((org) => (
                <tr key={org.id} className="border-b border-gray-50 dark:border-white/5">
                  <td className="px-5 py-3 font-medium text-gray-900 dark:text-white">{org.name}</td>
                  <td className="px-5 py-3 text-gray-500 dark:text-gray-400">{org.type}</td>
                  <td className="px-5 py-3 text-gray-500 dark:text-gray-400">{org.district}</td>
                  <td className="px-5 py-3 text-gray-500 dark:text-gray-400">{org.phone}</td>
                  <td className="px-5 py-3">
                    <button
                      onClick={() => handleVerify(org.id)}
                      className={`flex items-center gap-1 rounded-full px-2.5 py-1 text-xs font-semibold ${
                        org.isVerified
                          ? "bg-blue-50 text-blue-600 dark:bg-blue-950/40"
                          : "bg-gray-100 text-gray-500 dark:bg-white/10"
                      }`}
                    >
                      <FiShield size={12} /> {org.isVerified ? "Verified" : "Unverified"}
                    </button>
                  </td>
                  <td className="px-5 py-3">
                    <div className="flex justify-end gap-2">
                      <button
                        onClick={() => openEdit(org)}
                        className="rounded-lg p-2 text-gray-500 hover:bg-gray-100 dark:hover:bg-white/10"
                      >
                        <FiEdit2 size={15} />
                      </button>
                      <button
                        onClick={() => handleDelete(org.id)}
                        className="rounded-lg p-2 text-red-500 hover:bg-red-50 dark:hover:bg-red-950/30"
                      >
                        <FiTrash2 size={15} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {organizations.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-5 py-8 text-center text-gray-400">
                    কোনো সংগঠন যোগ করা হয়নি।
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {formOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" onClick={() => setFormOpen(false)}>
          <div
            onClick={(e) => e.stopPropagation()}
            className="glass-card max-h-[90vh] w-full max-w-lg space-y-4 overflow-y-auto p-6"
          >
            <div className="flex items-center justify-between">
              <h3 className="font-semibold text-gray-900 dark:text-white">
                {editingId ? "Edit Organization" : "Add Organization"}
              </h3>
              <button onClick={() => setFormOpen(false)} className="rounded-full p-1.5 text-gray-400 hover:bg-gray-100 dark:hover:bg-white/10">
                <FiX />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-3">
              <input name="name" value={form.name} onChange={handleChange} required placeholder="Organization name" className={inputClass} />

              <div className="grid grid-cols-2 gap-3">
                <select name="type" value={form.type} onChange={handleChange} className={inputClass}>
                  {TYPES.map((t) => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
                <select name="district" value={form.district} onChange={handleChange} required className={inputClass}>
                  <option value="">District</option>
                  {districts.map((d) => (
                    <option key={d} value={d}>{d}</option>
                  ))}
                </select>
              </div>

              <input name="upazila" value={form.upazila} onChange={handleChange} placeholder="Upazila" className={inputClass} />
              <input name="address" value={form.address} onChange={handleChange} placeholder="Address" className={inputClass} />

              <div className="grid grid-cols-2 gap-3">
                <input name="phone" value={form.phone} onChange={handleChange} required placeholder="Phone" className={inputClass} />
                <input name="email" value={form.email} onChange={handleChange} placeholder="Email (optional)" className={inputClass} />
              </div>

              <input name="website" value={form.website} onChange={handleChange} placeholder="Website (optional)" className={inputClass} />
              <textarea
                name="description"
                value={form.description}
                onChange={handleChange}
                rows={3}
                placeholder="সংক্ষিপ্ত বিবরণ"
                className={inputClass}
              />

              <div>
                <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">Logo (optional)</label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => setLogoFile(e.target.files?.[0] || null)}
                  className="text-sm text-gray-500 dark:text-gray-400"
                />
              </div>

              <button type="submit" disabled={saving} className="btn-primary w-full justify-center">
                {saving ? <FiLoader className="animate-spin" /> : "Save"}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
