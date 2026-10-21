import axiosInstance from "./axiosInstance.js";

export const createRequest = async (payload) => {
  const { data } = await axiosInstance.post("/requests", payload);
  return data;
};

export const getMyRequests = async (status) => {
  const { data } = await axiosInstance.get("/requests/my-requests", {
    params: status ? { status } : {},
  });
  return data;
};

export const cancelRequest = async (id) => {
  const { data } = await axiosInstance.patch(`/requests/${id}/cancel`);
  return data;
};

export const getIncomingRequests = async () => {
  const { data } = await axiosInstance.get("/requests/incoming");
  return data;
};

export const getNearbyRequests = async () => {
  const { data } = await axiosInstance.get("/requests/nearby");
  return data;
};

export const respondToRequest = async (id, action) => {
  const { data } = await axiosInstance.patch(`/requests/${id}/respond`, { action });
  return data;
};
