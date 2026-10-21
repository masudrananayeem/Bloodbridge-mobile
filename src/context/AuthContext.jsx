import { createContext, useContext, useEffect, useState } from "react";
import {
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signInWithPopup,
  signInWithCredential,
  GoogleAuthProvider,
  signOut,
  sendPasswordResetEmail,
  sendEmailVerification,
  updateProfile,
  onAuthStateChanged,
} from "firebase/auth";
import { Capacitor } from "@capacitor/core";
import { FirebaseAuthentication } from "@capacitor-firebase/authentication";
import { auth, googleProvider } from "../config/firebase.js";
import { syncLogin, googleAuth, fetchCurrentUser } from "../services/authService.js";

const AuthContext = createContext(null);

export default function AuthProvider({ children }) {
  const [firebaseUser, setFirebaseUser] = useState(null); // raw Firebase user
  const [user, setUser] = useState(null); // Mongo profile + role + JWT
  const [loading, setLoading] = useState(true);

  // Keep local state in sync with Firebase's own session persistence
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (fbUser) => {
      setFirebaseUser(fbUser);
      if (fbUser) {
        try {
          // We already have a backend JWT saved (e.g. page refresh) — just
          // fetch the profile again to keep role/availability fresh.
          const token = localStorage.getItem("bb-token");
          if (token) {
            const { user: profile } = await fetchCurrentUser();
            setUser(profile);
          }
        } catch {
          // token expired/invalid — force a clean re-login
          localStorage.removeItem("bb-token");
          setUser(null);
        }
      } else {
        setUser(null);
        localStorage.removeItem("bb-token");
      }
      setLoading(false);
    });
    return unsubscribe;
  }, []);

  // Exchange the fresh Firebase ID token for our backend JWT + profile
  const syncWithBackend = async () => {
    const idToken = await auth.currentUser.getIdToken(true);
    const { token, user: profile } = await syncLogin(idToken);
    localStorage.setItem("bb-token", token);
    setUser(profile);
    return profile;
  };

  const registerWithEmail = async ({ fullName, email, password }) => {
    const cred = await createUserWithEmailAndPassword(auth, email, password);
    await updateProfile(cred.user, { displayName: fullName });
    await sendEmailVerification(cred.user);
    return cred.user;
  };

  const loginWithEmail = async (email, password) => {
    await signInWithEmailAndPassword(auth, email, password);
    return syncWithBackend();
  };

  // Sign in (or sign up) with Google. Returns the profile, which may have
  // profileComplete: false for a brand-new Google user — the caller is
  // responsible for routing them to /complete-profile in that case.
  const loginWithGoogle = async () => {
    if (Capacitor.isNativePlatform()) {
      // Android app: native Google account picker (popups don't work in a WebView)
      const result = await FirebaseAuthentication.signInWithGoogle({ skipNativeAuth: true });
      const credential = GoogleAuthProvider.credential(result.credential?.idToken);
      await signInWithCredential(auth, credential);
    } else {
      await signInWithPopup(auth, googleProvider);
    }
    const idToken = await auth.currentUser.getIdToken(true);
    const { token, user: profile } = await googleAuth(idToken);
    localStorage.setItem("bb-token", token);
    setUser(profile);
    return profile;
  };

  const resetPassword = (email) => sendPasswordResetEmail(auth, email);

  // Used by the "check your email" screen right after registration, in
  // case the first verification email got lost or landed in spam.
  const resendVerificationEmail = () => {
    if (!auth.currentUser) throw new Error("No signed-in Firebase user to resend a verification email to");
    return sendEmailVerification(auth.currentUser);
  };

  const logout = async () => {
    await signOut(auth);
    if (Capacitor.isNativePlatform()) {
      try { await FirebaseAuthentication.signOut(); } catch { /* ignore */ }
    }
    localStorage.removeItem("bb-token");
    setUser(null);
  };

  const value = {
    firebaseUser,
    user,
    setUser,
    loading,
    isAuthenticated: !!firebaseUser,
    registerWithEmail,
    loginWithEmail,
    loginWithGoogle,
    resetPassword,
    resendVerificationEmail,
    logout,
    syncWithBackend,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};
