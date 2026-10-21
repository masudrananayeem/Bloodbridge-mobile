import { motion } from "framer-motion";

// badges: [{ label, tone: "light" | "dark" }]
// tone: "brand" (default, red gradient) | "dark" (admin panel)
export default function WelcomeBanner({ title, subtitle, photoURL, initial, badges = [], rightSlot, tone = "brand" }) {
  const bannerBg =
    tone === "dark"
      ? "bg-gradient-to-br from-gray-900 via-gray-950 to-black shadow-black/30"
      : "bg-brand-gradient shadow-brand-600/20";

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className={`relative overflow-hidden rounded-3xl p-6 text-white shadow-lg sm:p-8 ${bannerBg}`}
    >
      {/* Decorative translucent blobs — quiet texture, not the focal point */}
      <div className={`pointer-events-none absolute -right-10 -top-14 h-52 w-52 rounded-full blur-2xl ${tone === "dark" ? "bg-red-600/10" : "bg-white/10"}`} />
      <div className="pointer-events-none absolute -bottom-16 left-1/3 h-40 w-40 rounded-full bg-black/10 blur-2xl" />

      <div className="relative flex flex-col items-start justify-between gap-6 sm:flex-row sm:items-center">
        <div className="flex items-center gap-4">
          {photoURL ? (
            <img src={photoURL} alt="" className="h-14 w-14 rounded-full border-2 border-white/40 object-cover sm:h-16 sm:w-16" />
          ) : (
            <div className="flex h-14 w-14 items-center justify-center rounded-full border-2 border-white/40 bg-white/15 text-xl font-bold sm:h-16 sm:w-16">
              {initial}
            </div>
          )}
          <div>
            <h1 className="text-xl font-bold sm:text-2xl">{title}</h1>
            {subtitle && <p className="mt-0.5 text-sm text-white/80">{subtitle}</p>}
            {badges.length > 0 && (
              <div className="mt-2 flex flex-wrap gap-2">
                {badges.map((badge) => (
                  <span
                    key={badge.label}
                    className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
                      badge.tone === "dark" ? "bg-black/20" : "bg-white/20"
                    }`}
                  >
                    {badge.label}
                  </span>
                ))}
              </div>
            )}
          </div>
        </div>

        {rightSlot && <div className="shrink-0">{rightSlot}</div>}
      </div>
    </motion.div>
  );
}
