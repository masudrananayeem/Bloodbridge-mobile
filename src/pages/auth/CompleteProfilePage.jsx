import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { FiLoader } from "react-icons/fi";
import AuthLayout from "../../components/auth/AuthLayout.jsx";
import FormInput from "../../components/auth/FormInput.jsx";
import { useAuth } from "../../context/AuthContext.jsx";
import { updateProfile } from "../../services/userService.js";
import districts from "../../utils/districts.js";

const bloodGroups = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];

// Shown right after a brand-new Google sign-up. Google only ever gives us
// name/email/photo — this collects everything else the app actually needs
// (blood group, phone, district, DOB, present address) before letting the
// person into the dashboard.
export default function CompleteProfilePage() {
  const { user, setUser } = useAuth();
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ defaultValues: { fullName: user?.fullName || "" } });

  const selectClass =
    "w-full rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm text-gray-900 outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-100 dark:border-white/10 dark:bg-white/5 dark:text-white";

  const onSubmit = async (formData) => {
    setSubmitting(true);
    try {
      const { user: updated } = await updateProfile(formData);
      setUser(updated);
      toast.success("প্রোফাইল সম্পূর্ণ হয়েছে! স্বাগতম।");
      navigate("/dashboard", { replace: true });
    } catch (err) {
      toast.error(err?.response?.data?.message || "সেভ করা যায়নি, আবার চেষ্টা করুন।");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout
      title="প্রোফাইল সম্পূর্ণ করুন"
      subtitle="Google দিয়ে সাইন আপ করায় কিছু জরুরি তথ্য বাকি আছে — এগুলো ছাড়া ডোনার/সিকার হিসেবে ব্যবহার করা যাবে না।"
    >
      <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
        <FormInput
          label="Full Name"
          error={errors.fullName}
          registration={register("fullName", { required: "পূর্ণ নাম দিন" })}
          placeholder="আপনার নাম"
        />

        <FormInput
          label="Phone Number"
          error={errors.phone}
          registration={register("phone", {
            required: "ফোন নম্বর দিন",
            pattern: { value: /^01[3-9]\d{8}$/, message: "সঠিক বাংলাদেশি নম্বর দিন" },
          })}
          placeholder="01XXXXXXXXX"
        />

        <div className="grid grid-cols-1 gap-x-4 sm:grid-cols-2">
          <FormInput label="Blood Group" error={errors.bloodGroup}>
            <select {...register("bloodGroup", { required: "ব্লাড গ্রুপ বেছে নিন" })} className={selectClass}>
              <option value="">বেছে নিন</option>
              {bloodGroups.map((g) => (
                <option key={g} value={g}>{g}</option>
              ))}
            </select>
          </FormInput>

          <FormInput
            label="Date of Birth"
            type="date"
            error={errors.dateOfBirth}
            registration={register("dateOfBirth", { required: "জন্ম তারিখ দিন" })}
          />
        </div>

        <div className="grid grid-cols-1 gap-x-4 sm:grid-cols-2">
          <FormInput label="District" error={errors.district}>
            <select {...register("district", { required: "জেলা বেছে নিন" })} className={selectClass}>
              <option value="">বেছে নিন</option>
              {districts.map((d) => (
                <option key={d} value={d}>{d}</option>
              ))}
            </select>
          </FormInput>

          <FormInput
            label="Upazila"
            error={errors.upazila}
            registration={register("upazila", { required: "উপজেলা দিন" })}
            placeholder="আপনার উপজেলা"
          />
        </div>

        <FormInput
          label="Present Address"
          error={errors.address}
          registration={register("address", { required: "বর্তমান ঠিকানা দিন" })}
          placeholder="বিস্তারিত ঠিকানা — এটি দিয়ে নিকটতম ডোনার/সিকার নির্ধারণ করা হবে"
        />

        <button type="submit" disabled={submitting} className="btn-primary mt-2 w-full justify-center">
          {submitting ? <FiLoader className="animate-spin" /> : "Save & Continue"}
        </button>
      </form>
    </AuthLayout>
  );
}
