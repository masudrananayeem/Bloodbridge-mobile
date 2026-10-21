export default function Loader({ fullScreen = false }) {
  return (
    <div
      className={
        fullScreen
          ? "flex min-h-screen items-center justify-center bg-white dark:bg-surface-dark"
          : "flex items-center justify-center py-16"
      }
    >
      <div className="h-10 w-10 animate-spin rounded-full border-4 border-brand-200 border-t-brand-600" />
    </div>
  );
}
