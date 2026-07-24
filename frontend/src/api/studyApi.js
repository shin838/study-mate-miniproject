import apiClient from "./apiClient";

export async function getStudies({ keyword = "", page = 0, size = 10 } = {}) {
  const { data } = await apiClient.get("/studies", {
    params: {
      keyword: keyword.trim() || undefined,
      page,
      size,
    },
  });
  return data;
}

export async function getStudy(studyId) {
  const { data } = await apiClient.get(`/studies/${studyId}`);
  return data;
}

export async function createStudy(payload) {
  const { data } = await apiClient.post("/studies", payload);
  return data;
}

export async function updateStudy(studyId, payload) {
  const { data } = await apiClient.put(`/studies/${studyId}`, payload);
  return data;
}

export async function deleteStudy(studyId) {
  await apiClient.delete(`/studies/${studyId}`);
}

export async function applyStudy(studyId) {
  const { data } = await apiClient.post(`/studies/${studyId}/applications`);
  return data;
}

export async function cancelStudyApplication(studyId) {
  const { data } = await apiClient.delete(
    `/studies/${studyId}/applications`,
  );
  return data;
}

export async function transferStudyLeader(studyId, newLeaderId) {
  const { data } = await apiClient.patch(`/studies/${studyId}/leader`, {
    newLeaderId,
  });
  return data;
}
