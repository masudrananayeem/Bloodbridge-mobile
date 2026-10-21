import axiosInstance from "./axiosInstance.js";

export const updateProfile = async (payload) => {
  const { data } = await axiosInstance.put("/users/profile", payload);
  return data;
};

export const toggleAvailability = async (isAvailable) => {
  const { data } = await axiosInstance.patch("/users/availability", { isAvailable });
  return data;
};

export const switchMode = async (mode) => {
  const { data } = await axiosInstance.patch("/users/mode", { mode });
  return data;
};

export const getDonationHistory = async () => {
  const { data } = await axiosInstance.get("/users/donation-history");
  return data;
};

export const recordDonation = async (payload = {}) => {
  const { data } = await axiosInstance.post("/users/record-donation", payload);
  return data;
};

export const searchDonors = async (filters) => {
  const { data } = await axiosInstance.get("/users/search-donors", { params: filters });
  return data;
};

export const toggleSavedDonor = async (donorId) => {
  const { data } = await axiosInstance.patch(`/users/saved-donors/${donorId}`);
  return data;
};

export const getSavedDonors = async () => {
  const { data } = await axiosInstance.get("/users/saved-donors");
  return data;
};

export const deleteMyAccount = async () => {
  const { data } = await axiosInstance.delete("/users/me");
  return data;
};
