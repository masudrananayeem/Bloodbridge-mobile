import axiosInstance from "./axiosInstance.js";

export const getOrganizations = async (filters = {}) => {
  const { data } = await axiosInstance.get("/organizations", { params: filters });
  return data;
};

export const getOrganizationById = async (id) => {
  const { data } = await axiosInstance.get(`/organizations/${id}`);
  return data;
};

// Admin-only
export const createOrganization = async (payload) => {
  const { data } = await axiosInstance.post("/organizations", payload);
  return data;
};

export const updateOrganization = async (id, payload) => {
  const { data } = await axiosInstance.put(`/organizations/${id}`, payload);
  return data;
};

export const toggleVerifyOrganization = async (id) => {
  const { data } = await axiosInstance.patch(`/organizations/${id}/verify`);
  return data;
};

export const deleteOrganization = async (id) => {
  const { data } = await axiosInstance.delete(`/organizations/${id}`);
  return data;
};
