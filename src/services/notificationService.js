import axiosInstance from "./axiosInstance.js";

export const getNotifications = async () => {
  const { data } = await axiosInstance.get("/notifications");
  return data;
};

export const markAsRead = async (id) => {
  const { data } = await axiosInstance.patch(`/notifications/${id}/read`);
  return data;
};

export const markAllAsRead = async () => {
  const { data } = await axiosInstance.patch("/notifications/read-all");
  return data;
};
