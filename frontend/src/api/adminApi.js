import apiClient from "./apiClient";

export async function getAdminMembers() {
  const { data } = await apiClient.get("/admin/members");
  return data;
}

export async function getAdminMemberDetail(memberId) {
  const { data } = await apiClient.get(`/admin/members/${memberId}`);
  return data;
}

export async function deleteAdminMember(memberId) {
  const { data } = await apiClient.delete(`/admin/members/${memberId}`);
  return data;
}

export async function getAdminStudies() {
  const { data } = await apiClient.get("/admin/studies");
  return data;
}

export async function changeAdminStudyStatus(studyId, status) {
  const { data } = await apiClient.patch(`/admin/studies/${studyId}/status`, {
    status,
  });
  return data;
}

export async function deleteAdminStudy(studyId) {
  await apiClient.delete(`/admin/studies/${studyId}`);
}
