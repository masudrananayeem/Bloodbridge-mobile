import { useState } from "react";
import { FiEye, FiEyeOff } from "react-icons/fi";

export default function FormInput({ label, error, type = "text", registration, children, ...rest }) {
  const isPassword = type === "password";
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="mb-4">
      <label className="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-300">
        {label}
      </label>
      {children ? (
        children
      ) : (
        <div className="relative">
          <input
            type={isPassword && showPassword ? "text" : type}
            {...registration}
            {...rest}
            className={`w-full rounded-xl border bg-white py-2.5 pl-4 text-sm text-gray-900 outline-none transition-colors focus:border-brand-500 focus:ring-2 focus:ring-brand-100 dark:bg-white/5 dark:text-white dark:focus:ring-brand-950 ${
              isPassword ? "pr-11" : "pr-4"
            } ${error ? "border-red-400" : "border-gray-200 dark:border-white/10"}`}
          />
          {isPassword && (
            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
              tabIndex={-1}
              aria-label={showPassword ? "Hide password" : "Show password"}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 transition-colors hover:text-gray-600 dark:hover:text-gray-200"
            >
              {showPassword ? <FiEyeOff size={18} /> : <FiEye size={18} />}
            </button>
          )}
        </div>
      )}
      {error && <p className="mt-1 text-xs text-red-500">{error.message}</p>}
    </div>
  );
}
