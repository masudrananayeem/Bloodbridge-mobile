import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { FiLoader, FiAlertTriangle } from "react-icons/fi";
import bloodGroups from "../../utils/bloodGroups.js";
import districts from "../../utils/districts.js";
import { createRequest } from "../../services/requestService.js";
import { useLanguage } from "../../context/LanguageContext.jsx";

export default function EmergencyRequestForm() {
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ defaultValues: { urgency: "High", units: 1 } });

  const onSubmit = async (formData) => {
    setSubmitting(true);
    try {
      const isEmergency = formData.urgency === "High";
      await createRequest({ ...formData, isEmergency });
      toast.success(
        isEmergency
          ? "জরুরি রিকোয়েস্ট পাঠানো হয়েছে — নিকটতম ৫ জন ডোনার সাথে সাথে নোটিফিকেশন পাবেন।"
          : "রিকোয়েস্ট পাঠানো হয়েছে — কাছাকাছি ডোনাররা নোটিফিকেশন পাবেন।"
      );
      navigate("/dashboard/seeker/my-requests");
    } catch (err) {
      toast.error(err?.response?.data?.message || "রিকোয়েস্ট পাঠানো যায়নি, আবার চেষ্টা করুন।");
    } finally {
      setSubmitting(false);
    }
  };

  const inputClass =
    "w-full rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm text-gray-900 outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100 dark:border-white/10 dark:bg-white/5 dark:text-white";
  const errClass = "border-red-400";

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="glass-card max-w-2xl space-y-4 p-6">
      <div className="mb-2 flex items-center gap-2 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-600 dark:bg-red-950/30">
        <FiAlertTriangle /> সঠিক তথ্য দিন। <b>High</b> Urgency বেছে নিলে শুধু আপনার সবচেয়ে কাছের ৫ জন ডোনারকে সাথে সাথে নোটিফাই করা হবে; Medium/Low হলে একই ব্লাড গ্রুপ ও জেলার সব Active ডোনারকে জানানো হবে।
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("common.bloodGroup")}</label>
          <select {...register("bloodGroup", { required: true })} className={`${inputClass} ${errors.bloodGroup ? errClass : ""}`}>
            <option value="">বেছে নিন</option>
            {bloodGroups.map((g) => (
              <option key={g} value={g}>{g}</option>
            ))}
          </select>
        </div>
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("emergency.units")}</label>
          <input
            type="number"
            min={1}
            {...register("units", { required: true, min: 1 })}
            className={`${inputClass} ${errors.units ? errClass : ""}`}
          />
        </div>
      </div>

      <div>
        <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("emergency.hospital")}</label>
        <input
          {...register("hospital", { required: true })}
          placeholder="হাসপাতালের নাম"
          className={`${inputClass} ${errors.hospital ? errClass : ""}`}
        />
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("common.district")}</label>
          <select {...register("district", { required: true })} className={`${inputClass} ${errors.district ? errClass : ""}`}>
            <option value="">বেছে নিন</option>
            {districts.map((d) => (
              <option key={d} value={d}>{d}</option>
            ))}
          </select>
        </div>
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("common.upazila")}</label>
          <input
            {...register("upazila", { required: true })}
            className={`${inputClass} ${errors.upazila ? errClass : ""}`}
          />
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("emergency.urgency")}</label>
          <select {...register("urgency", { required: true })} className={inputClass}>
            <option value="High">High</option>
            <option value="Medium">Medium</option>
            <option value="Low">Low</option>
          </select>
        </div>
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("emergency.neededBy")}</label>
          <input
            type="date"
            {...register("neededByDate", { required: true })}
            className={`${inputClass} ${errors.neededByDate ? errClass : ""}`}
          />
        </div>
      </div>

      <div>
        <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("emergency.reason")}</label>
        <textarea {...register("reason")} rows={2} placeholder="সংক্ষেপে কারণ লিখুন" className={inputClass} />
      </div>

      <div>
        <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">
          {t("emergency.message")}
        </label>
        <textarea
          {...register("message")}
          rows={2}
          placeholder="ডোনারদের জন্য একটি সংক্ষিপ্ত বার্তা লিখুন"
          className={inputClass}
        />
      </div>

      <button type="submit" disabled={submitting} className="btn-primary w-full justify-center">
        {submitting ? <FiLoader className="animate-spin" /> : t("emergency.submit")}
      </button>
    </form>
  );
}
