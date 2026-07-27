import apiClient from "./apiClient";

export async function getMyParticipatingStudies() {
  const { data } = await apiClient.get("/my/applications");
  return data;
}

export async function getMyParticipatingStudyDetail(studyId) {
  const { data } = await apiClient.get(`/my/applications/${studyId}`);
  return data;
}

export async function getMyCreatedStudies() {
  const { data } = await apiClient.get("/my/studies");
  return data;
}
