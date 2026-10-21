import { useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { FiLoader } from "react-icons/fi";
import { sendPasswordResetEmail } from "firebase/auth";
import { auth } from "../../config/firebase.js";
import { useAuth } from "../../context/AuthContext.jsx";
import { useLanguage } from "../../context/LanguageContext.jsx";
import { deleteMyAccount } from "../../services/userService.js";

export default function AccountSettings() {
  const { user, logout } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [sending, setSending] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [confirmText, setConfirmText] = useState("");

  const handlePasswordReset = async () => {
    setSending(true);
    try {
      await sendPasswordResetEmail(auth, user.email);
      toast.success("পাসওয়ার্ড রিসেট লিংক ইমেইলে পাঠানো হয়েছে");
    } catch {
      toast.error("লিংক পাঠানো যায়নি");
    } finally {
      setSending(false);
    }
  };

  const handleDeleteAccount = async () => {
    setDeleting(true);
    try {
      // Deletes the Firestore profile + every request/history/notification
      // tied to this user, then the underlying Firebase Auth account.
      await deleteMyAccount();
      toast.success("আপনার অ্যাকাউন্ট স্থায়ীভাবে ডিলিট করা হয়েছে");
      await logout();
      navigate("/");
    } catch {
      toast.error("অ্যাকাউন্ট ডিলিট করা যায়নি, আবার চেষ্টা করুন");
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="max-w-2xl space-y-6">
      <div className="glass-card p-6">
        <h3 className="mb-1 font-semibold text-gray-900 dark:text-white">{t("settings.resetPasswordTitle")}</h3>
        <p className="mb-4 text-sm text-gray-500 dark:text-gray-400">{t("settings.resetPasswordDesc")}</p>
        <button onClick={handlePasswordReset} disabled={sending} className="btn-primary">
          {sending ? <FiLoader className="animate-spin" /> : t("settings.sendResetLink")}
        </button>
      </div>

      <div className="glass-card border border-red-200 p-6 dark:border-red-900/50">
        <h3 className="mb-1 font-semibold text-red-600">{t("settings.dangerZone")}</h3>
        <p className="mb-4 text-sm text-gray-500 dark:text-gray-400">
          {t("settings.deleteAccountDesc")} {t("settings.typeToConfirm")}: <b>DELETE</b>
        </p>
        <input
          value={confirmText}
          onChange={(e) => setConfirmText(e.target.value)}
          placeholder="DELETE"
          className="mb-3 w-full rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm outline-none focus:border-red-400 dark:border-white/10 dark:bg-white/5 dark:text-white"
        />
        <button
          onClick={handleDeleteAccount}
          disabled={confirmText !== "DELETE" || deleting}
          className="flex items-center gap-2 rounded-full bg-red-600 px-6 py-2.5 text-sm font-semibold text-white disabled:opacity-40"
        >
          {deleting ? <FiLoader className="animate-spin" /> : t("settings.deleteAccount")}
        </button>
      </div>
    </div>
  );
}
