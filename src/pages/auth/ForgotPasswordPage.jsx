import { useState } from "react";
import { useForm } from "react-hook-form";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";
import { FiLoader, FiCheckCircle } from "react-icons/fi";
import AuthLayout from "../../components/auth/AuthLayout.jsx";
import FormInput from "../../components/auth/FormInput.jsx";
import { useAuth } from "../../context/AuthContext.jsx";

export default function ForgotPasswordPage() {
  const { resetPassword } = useAuth();
  const [submitting, setSubmitting] = useState(false);
  const [sent, setSent] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();

  const onSubmit = async ({ email }) => {
    setSubmitting(true);
    try {
      await resetPassword(email);
      setSent(true);
    } catch (err) {
      toast.error(
        err.code === "auth/user-not-found"
          ? "এই ইমেইলে কোনো অ্যাকাউন্ট পাওয়া যায়নি।"
          : "কিছু একটা সমস্যা হয়েছে, আবার চেষ্টা করুন।"
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthLayout title="পাসওয়ার্ড রিসেট করুন" subtitle="আপনার ইমেইলে একটি রিসেট লিংক পাঠানো হবে">
      {sent ? (
        <div className="glass-card flex flex-col items-center gap-3 p-8 text-center">
          <FiCheckCircle className="text-brand-600" size={40} />
          <p className="text-sm text-gray-600 dark:text-gray-300">
            রিসেট লিংক পাঠানো হয়েছে। আপনার ইমেইল ইনবক্স (এবং স্প্যাম ফোল্ডার) চেক করুন।
          </p>
        </div>
      ) : (
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <FormInput
            label="Email"
            type="email"
            error={errors.email}
            registration={register("email", {
              required: "Email দিন",
              pattern: { value: /^\S+@\S+$/i, message: "সঠিক Email দিন" },
            })}
            placeholder="you@example.com"
          />
          <button type="submit" disabled={submitting} className="btn-primary w-full justify-center">
            {submitting ? <FiLoader className="animate-spin" /> : "রিসেট লিংক পাঠান"}
          </button>
        </form>
      )}

      <p className="mt-8 text-center text-sm text-gray-500 dark:text-gray-400">
        <Link to="/login" className="font-semibold text-brand-600 hover:underline">
          ← লগইনে ফিরে যান
        </Link>
      </p>
    </AuthLayout>
  );
}
