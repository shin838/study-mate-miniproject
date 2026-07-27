import apiClient from "./apiClient";

export async function loginMember(email, password) {
  const body = new URLSearchParams({ email, password });
  const { data } = await apiClient.post("/members/login", body, {
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
  });
  return data;
}

export async function registerMember(payload) {
  const body = new FormData();
  Object.entries(payload).forEach(([key, value]) => body.append(key, value));
  const { data } = await apiClient.post("/members/register", body);
  return data;
}

export async function withdrawMember() {
  const { data } = await apiClient.delete("/members/withdraw");
  return data;
}
