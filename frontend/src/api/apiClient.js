import axios from "axios";

export const ACCESS_TOKEN_KEY = "studyMateAccessToken";
export const REFRESH_TOKEN_KEY = "studyMateRefreshToken";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 10000,
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY);

  if (token) {
    config.headers["X-AUTH-TOKEN"] = token;
  }

  return config;
});

let refreshPromise = null;

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);

    if (
      error.response?.status === 401 &&
      refreshToken &&
      !originalRequest?._retry &&
      !originalRequest?.url?.includes("/members/refresh")
    ) {
      originalRequest._retry = true;

      refreshPromise ??= axios
        .post(
          `${import.meta.env.VITE_API_BASE_URL || "/api"}/members/refresh`,
          { refreshToken },
        )
        .then(({ data }) => {
          if (data.result !== "success" || !data.token) {
            throw new Error("토큰을 갱신할 수 없습니다.");
          }

          localStorage.setItem(ACCESS_TOKEN_KEY, data.token);
          if (data.refreshToken) {
            localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);
          }
          return data.token;
        })
        .finally(() => {
          refreshPromise = null;
        });

      try {
        const newToken = await refreshPromise;
        originalRequest.headers["X-AUTH-TOKEN"] = newToken;
        return apiClient(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem(ACCESS_TOKEN_KEY);
        localStorage.removeItem(REFRESH_TOKEN_KEY);
        window.location.hash = "#/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export function getErrorMessage(error, fallback = "요청을 처리하지 못했습니다.") {
  const data = error.response?.data;

  if (typeof data === "string" && data.trim()) {
    return data;
  }

  if (data?.message) {
    return data.message;
  }

  if (error.response?.status === 403) {
    return "이 작업을 수행할 권한이 없습니다.";
  }

  if (error.response?.status === 404) {
    return "요청한 정보를 찾을 수 없습니다.";
  }

  if (!error.response) {
    return "서버에 연결할 수 없습니다. 백엔드가 실행 중인지 확인해주세요.";
  }

  return fallback;
}

export default apiClient;
