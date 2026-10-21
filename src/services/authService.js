import axiosInstance from "./axiosInstance.js";

/**
 * After Firebase creates the account, we send the extra profile fields
 * (phone, blood group, district, etc.) + the Firebase ID token to our
 * backend, which verifies the token, creates the Mongo user document,
 * and returns our own JWT for subsequent API calls.
 */
export const registerProfile = async (payload) => {
  const { data } = await axiosInstance.post("/auth/register", payload);
  return data;
};

/**
 * After Firebase login (email/password or Google), exchange the Firebase
 * ID token for our backend JWT + the user's profile/role from MongoDB.
 */
export const syncLogin = async (firebaseIdToken) => {
  const { data } = await axiosInstance.post("/auth/login", { firebaseIdToken });
  return data;
};

/**
 * Google sign-in/sign-up. Unlike syncLogin, this auto-creates a minimal
 * profile server-side for brand-new Google users (profileComplete: false) —
 * the caller should check `user.profileComplete` and route to
 * /complete-profile if it's false.
 */
export const googleAuth = async (firebaseIdToken) => {
  const { data } = await axiosInstance.post("/auth/google", { firebaseIdToken });
  return data;
};

export const fetchCurrentUser = async () => {
  const { data } = await axiosInstance.get("/auth/me");
  return data;
};
