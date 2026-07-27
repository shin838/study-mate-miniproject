import apiClient from "./apiClient";

export async function getStudyPosts(studyId, page = 0, size = 20) {
  const { data } = await apiClient.get(`/studies/${studyId}/posts`, {
    params: { page, size },
  });
  return data;
}

export async function createStudyPost(studyId, content) {
  const { data } = await apiClient.post(`/studies/${studyId}/posts`, {
    content,
  });
  return data;
}

export async function updateStudyPost(studyId, postId, content) {
  const { data } = await apiClient.patch(
    `/studies/${studyId}/posts/${postId}`,
    { content },
  );
  return data;
}

export async function deleteStudyPost(studyId, postId) {
  await apiClient.delete(`/studies/${studyId}/posts/${postId}`);
}
