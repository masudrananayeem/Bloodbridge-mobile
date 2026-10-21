import { useState } from "react";
import { useForm } from "react-hook-form";
import { motion, AnimatePresence } from "framer-motion";
import toast from "react-hot-toast";
import { FiLoader, FiX } from "react-icons/fi";
import { createRequest } from "../../services/requestService.js";

// donor: the target donor object (id, fullName, bloodGroup, district, upazila, ...)
// onClose(): called to dismiss the modal
// onSent(): called after a successful send, so the parent can refresh state
export default function SendRequestModal({ donor, onClose, onSent }) {
  const [submitting, setSubmitting] = useState(false);
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ defaultValues: { units: 1, urgency: "Medium" } });

  const inputClass =
    "w-full rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm text-gray-900 outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100 dark:border-white/10 dark:bg-white/5 dark:text-white";

  const onSubmit = async (formData) => {
    setSubmitting(true);
    try {
      await createRequest({
        ...formData,
        bloodGroup: donor.bloodGroup,
        district: donor.district,
        upazila: donor.upazila,
        targetDonorUid: donor.id,
      });
      toast.success(`${donor.fullName} কে রিকোয়েস্ট পাঠানো হয়েছে`);
      onSent?.();
      onClose();
    } catch (err) {
      toast.error(err?.response?.data?.message || "রিকোয়েস্ট পাঠানো যায়নি");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
        onClick={onClose}
      >
        <motion.div
          initial={{ opacity: 0, y: 20, scale: 0.97 }}
          animate={{ opacity: 1, y: 0, scale: 1 }}
          exit={{ opacity: 0, y: 10, scale: 0.97 }}
          onClick={(e) => e.stopPropagation()}
          className="glass-card w-full max-w-lg space-y-4 p-6"
        >
          <div className="flex items-center justify-between">
            <div>
              <h3 className="font-semibold text-gray-900 dark:text-white">Send Request to {donor.fullName}</h3>
              <p className="text-xs text-gray-500 dark:text-gray-400">
                {donor.bloodGroup} · {donor.upazila}, {donor.district}
              </p>
            </div>
            <button onClick={onClose} className="rounded-full p-1.5 text-gray-400 hover:bg-gray-100 dark:hover:bg-white/10">
              <FiX />
            </button>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div>
                <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">Units Needed</label>
                <input
                  type="number"
                  min={1}
                  {...register("units", { required: true, min: 1 })}
                  className={inputClass}
                />
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">Urgency</label>
                <select {...register("urgency", { required: true })} className={inputClass}>
                  <option value="High">High</option>
                  <option value="Medium">Medium</option>
                  <option value="Low">Low</option>
                </select>
              </div>
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">Hospital</label>
              <input
                {...register("hospital", { required: true })}
                placeholder="হাসপাতালের নাম"
                className={`${inputClass} ${errors.hospital ? "border-red-400" : ""}`}
              />
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">Needed By</label>
              <input
                type="date"
                {...register("neededByDate", { required: true })}
                className={`${inputClass} ${errors.neededByDate ? "border-red-400" : ""}`}
              />
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">
                Message to {donor.fullName} (ঐচ্ছিক)
              </label>
              <textarea
                {...register("message")}
                rows={3}
                placeholder="আপনার পরিস্থিতি সংক্ষেপে লিখুন..."
                className={inputClass}
              />
            </div>

            <button type="submit" disabled={submitting} className="btn-primary w-full justify-center">
              {submitting ? <FiLoader className="animate-spin" /> : "Send Request"}
            </button>
          </form>
        </motion.div>
      </motion.div>
    </AnimatePresence>
  );
}
