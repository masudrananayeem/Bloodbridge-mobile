import axiosInstance from "./axiosInstance.js";

export const getDashboardStats = async () => {
  const { data } = await axiosInstance.get("/admin/stats");
  return data;
};

export const getAnalytics = async () => {
  const { data } = await axiosInstance.get("/admin/analytics");
  return data;
};

export const getAllUsers = async (params) => {
  const { data } = await axiosInstance.get("/admin/users", { params });
  return data;
};

export const toggleVerifyUser = async (id) => {
  const { data } = await axiosInstance.patch(`/admin/users/${id}/verify`);
  return data;
};

export const deleteUser = async (id) => {
  const { data } = await axiosInstance.delete(`/admin/users/${id}`);
  return data;
};

export const getAllRequests = async (params) => {
  const { data } = await axiosInstance.get("/admin/requests", { params });
  return data;
};

export const updateRequestStatus = async (id, status) => {
  const { data } = await axiosInstance.patch(`/admin/requests/${id}/status`, { status });
  return data;
};
