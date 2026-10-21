import { useState } from "react";
import { useForm } from "react-hook-form";
import { Link, useNavigate, useLocation } from "react-router-dom";
import toast from "react-hot-toast";
import { FcGoogle } from "react-icons/fc";
import { FiLoader } from "react-icons/fi";
import AuthLayout from "../../components/auth/AuthLayout.jsx";
import FormInput from "../../components/auth/FormInput.jsx";
import { useAuth } from "../../context/AuthContext.jsx";

export default function LoginPage() {
  const { loginWithEmail, loginWithGoogle } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [submitting, setSubmitting] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();

  const redirectAfterLogin = (profile) => {
    if (profile?.profileComplete === false) {
      return navigate("/complete-profile", { replace: true });
    }
    const from = location.state?.from?.pathname;
    if (from) return navigate(from, { replace: true });
    navigate(profile?.role === "admin" ? "/admin" : "/dashboard", { replace: true });
  };

  const onSubmit = async (formData) => {
    setSubmitting(true);
    try {
      const profile = await loginWithEmail(formData.email, formData.password);
      toast.success("সফলভাবে লগইন হয়েছে!");
      redirectAfterLogin(profile);
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleGoogleLogin = async () => {
    setGoogleLoading(true);
    try {
      const profile = await loginWithGoogle();
      toast.success("সফলভাবে লগইন হয়েছে!");
      redirectAfterLogin(profile);
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setGoogleLoading(false);
    }
  };

  return (
    <AuthLayout title="আবার স্বাগতম" subtitle="আপনার অ্যাকাউন্টে লগইন করুন">
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
        <FormInput
          label="Password"
          type="password"
          autoComplete="current-password"
          error={errors.password}
          registration={register("password", { required: "Password দিন" })}
          placeholder="••••••••"
        />

        <div className="mb-6 flex justify-end">
          <Link to="/forgot-password" className="text-sm font-medium text-brand-600 hover:underline">
            পাসওয়ার্ড ভুলে গেছেন?
          </Link>
        </div>

        <button type="submit" disabled={submitting} className="btn-primary w-full justify-center">
          {submitting ? <FiLoader className="animate-spin" /> : "Login"}
        </button>
      </form>

      <div className="my-6 flex items-center gap-3 text-xs text-gray-400">
        <div className="h-px flex-1 bg-gray-200 dark:bg-white/10" />
        অথবা
        <div className="h-px flex-1 bg-gray-200 dark:bg-white/10" />
      </div>

      <button
        onClick={handleGoogleLogin}
        disabled={googleLoading}
        className="flex w-full items-center justify-center gap-3 rounded-full border border-gray-200 bg-white py-2.5 text-sm font-semibold text-gray-700 shadow-sm transition-colors hover:bg-gray-50 dark:border-white/10 dark:bg-white/5 dark:text-gray-200"
      >
        {googleLoading ? <FiLoader className="animate-spin" /> : <FcGoogle size={20} />}
        Google দিয়ে লগইন করুন
      </button>

      <p className="mt-8 text-center text-sm text-gray-500 dark:text-gray-400">
        অ্যাকাউন্ট নেই?{" "}
        <Link to="/register" className="font-semibold text-brand-600 hover:underline">
          রেজিস্টার করুন
        </Link>
      </p>
    </AuthLayout>
  );
}

// Extracts a user-friendly error message from either a Firebase auth error
// or a backend (axios) error.
function getErrorMessage(err) {
  // Backend error — the real message lives in the response body
  if (err.response?.data?.message) {
    return err.response.data.message;
  }
  // Firebase auth error — map the error code
  if (err.code) {
    return mapFirebaseError(err.code);
  }
  // Fallback
  return "কিছু একটা সমস্যা হয়েছে, আবার চেষ্টা করুন।";
}

// Turns Firebase's cryptic error codes into friendly Bangla messages
function mapFirebaseError(code) {
  const map = {
    "auth/user-not-found": "এই ইমেইলে কোনো অ্যাকাউন্ট পাওয়া যায়নি।",
    "auth/wrong-password": "পাসওয়ার্ড সঠিক নয়।",
    "auth/invalid-credential": "ইমেইল বা পাসওয়ার্ড সঠিক নয়।",
    "auth/invalid-login-credentials": "ইমেইল বা পাসওয়ার্ড সঠিক নয়।",
    "auth/operation-not-allowed": "Firebase-এ Email/Password লগইন চালু নেই।",
    "auth/user-disabled": "এই অ্যাকাউন্টটি বন্ধ আছে।",
    "auth/too-many-requests": "অনেকবার চেষ্টা করা হয়েছে, কিছুক্ষণ পর আবার চেষ্টা করুন।",
    "auth/popup-closed-by-user": "লগইন উইন্ডো বন্ধ হয়ে গেছে।",
  };
  return map[code] || "কিছু একটা সমস্যা হয়েছে, আবার চেষ্টা করুন।";
}
