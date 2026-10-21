import { useState } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { FiLoader, FiUpload } from "react-icons/fi";
import { useAuth } from "../../context/AuthContext.jsx";
import { useLanguage } from "../../context/LanguageContext.jsx";
import { updateProfile } from "../../services/userService.js";
import { uploadProfileImage } from "../../services/uploadImage.js";
import { getAge } from "../../utils/age.js";
import districts from "../../utils/districts.js";

const bloodGroups = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];

export default function ProfileForm() {
  const { user, setUser } = useAuth();
  const { t } = useLanguage();
  const [submitting, setSubmitting] = useState(false);
  const [preview, setPreview] = useState(user?.photoURL || null);
  const [photoFile, setPhotoFile] = useState(null);
  const age = getAge(user?.dateOfBirth);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({
    defaultValues: {
      fullName: user?.fullName,
      phone: user?.phone,
      bloodGroup: user?.bloodGroup,
      district: user?.district,
      upazila: user?.upazila,
      address: user?.address,
      dateOfBirth: user?.dateOfBirth ? new Date(user.dateOfBirth).toISOString().slice(0, 10) : "",
    },
  });

  const watchedDob = watch("dateOfBirth");
  const livePreviewAge = getAge(watchedDob);

  const handlePhotoChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setPhotoFile(file);
    setPreview(URL.createObjectURL(file));
  };

  const onSubmit = async (formData) => {
    setSubmitting(true);
    try {
      let photoURL = user?.photoURL || "";
      if (photoFile) photoURL = await uploadProfileImage(photoFile);

      const { user: updated } = await updateProfile({ ...formData, photoURL });
      setUser(updated);
      toast.success("প্রোফাইল আপডেট হয়েছে");
    } catch {
      toast.error("প্রোফাইল আপডেট করা যায়নি");
    } finally {
      setSubmitting(false);
    }
  };

  const inputClass =
    "w-full rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm text-gray-900 outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100 dark:border-white/10 dark:bg-white/5 dark:text-white";

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="glass-card max-w-2xl space-y-4 p-6">
      <div className="flex items-center gap-4">
        <label className="relative flex h-16 w-16 cursor-pointer items-center justify-center overflow-hidden rounded-full border-2 border-dashed border-gray-300 bg-gray-50 dark:border-white/20 dark:bg-white/5">
          {preview ? (
            <img src={preview} alt="preview" className="h-full w-full object-cover" />
          ) : (
            <FiUpload className="text-gray-400" />
          )}
          <input type="file" accept="image/*" className="hidden" onChange={handlePhotoChange} />
        </label>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          {t("profile.changePhoto")}
          {age !== null && (
            <span className="mt-1 block font-semibold text-gray-700 dark:text-gray-200">
              {t("profile.age")}: {age} {t("common.years")}
            </span>
          )}
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("common.fullName")}</label>
          <input {...register("fullName")} className={inputClass} />
        </div>
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("common.phone")}</label>
          <input {...register("phone")} className={inputClass} />
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("common.bloodGroup")}</label>
          <select {...register("bloodGroup")} className={inputClass}>
            {bloodGroups.map((g) => (
              <option key={g} value={g}>{g}</option>
            ))}
          </select>
        </div>
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("common.dateOfBirth")}</label>
          <input type="date" {...register("dateOfBirth")} className={inputClass} />
          {livePreviewAge !== null && (
            <p className="mt-1 text-xs text-gray-400">{t("profile.age")}: {livePreviewAge} {t("common.years")}</p>
          )}
        </div>
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("common.upazila")}</label>
          <input {...register("upazila")} className={inputClass} />
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">{t("common.district")}</label>
          <select {...register("district")} className={inputClass}>
            {districts.map((d) => (
              <option key={d} value={d}>{d}</option>
            ))}
          </select>
        </div>
      </div>

      <div>
        <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">
          {t("common.address")} <span className="text-red-500">*</span>
        </label>
        <textarea
          {...register("address", { required: "Present address is required" })}
          rows={3}
          placeholder="আপনার বর্তমান ঠিকানা বিস্তারিত লিখুন"
          className={`${inputClass} ${errors.address ? "border-red-400" : ""}`}
        />
        {errors.address && <p className="mt-1 text-xs text-red-500">{errors.address.message}</p>}
        <p className="mt-1 text-xs text-gray-400">
          এই ঠিকানার District ব্যবহার করে "Near Me" সার্চ এবং Emergency রিকোয়েস্টে নিকটতম ডোনার নির্ধারণ করা হয়।
        </p>
      </div>

      <button type="submit" disabled={submitting} className="btn-primary">
        {submitting ? <FiLoader className="animate-spin" /> : t("common.save")}
      </button>
    </form>
  );
}
