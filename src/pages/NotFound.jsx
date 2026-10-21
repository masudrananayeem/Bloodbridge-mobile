import { Link } from "react-router-dom";

export default function NotFound() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-4 text-center">
      <h1 className="text-6xl font-bold text-brand-600">404</h1>
      <p className="text-gray-500 dark:text-gray-400">এই পেজটি খুঁজে পাওয়া যায়নি।</p>
      <Link to="/" className="btn-primary">হোমে ফিরে যান</Link>
    </div>
  );
}
