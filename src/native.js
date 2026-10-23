import { Capacitor } from "@capacitor/core";

// Android-app-only polish. Does nothing in a normal browser.
export async function initNative(navigate) {
  if (!Capacitor.isNativePlatform()) return;

  const [{ App }, { StatusBar, Style }, { SplashScreen }, { Keyboard }] = await Promise.all([
    import("@capacitor/app"),
    import("@capacitor/status-bar"),
    import("@capacitor/splash-screen"),
    import("@capacitor/keyboard"),
  ]);

  // Status bar colour follows the light/dark theme
  const applyStatusBar = async () => {
    const dark = document.documentElement.classList.contains("dark");
    try {
      await StatusBar.setStyle({ style: dark ? Style.Dark : Style.Light });
      await StatusBar.setBackgroundColor({ color: dark ? "#0f1117" : "#ffffff" });
    } catch { /* not supported on this device */ }
  };
  await applyStatusBar();
  new MutationObserver(applyStatusBar).observe(document.documentElement, { attributes: true, attributeFilter: ["class"] });

  // Hardware back button: go back a page; exit only from the top-level screens
  const rootPaths = ["/", "/login", "/dashboard", "/dashboard/donor", "/dashboard/seeker", "/admin"];
  App.addListener("backButton", ({ canGoBack }) => {
    const path = window.location.pathname.replace(/\/$/, "") || "/";
    if (rootPaths.includes(path) || !canGoBack) App.exitApp();
    else window.history.back();
  });

  // Keep the focused input visible when the keyboard opens
  try { await Keyboard.setResizeMode({ mode: "native" }); } catch { /* ignore */ }

  await SplashScreen.hide({ fadeOutDuration: 250 });
}
