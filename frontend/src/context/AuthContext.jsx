import { createContext, useContext, useMemo, useState } from "react";
import {
  ACCESS_TOKEN_KEY,
  REFRESH_TOKEN_KEY,
} from "../api/apiClient";
import { loginMember } from "../api/authApi";
import { getUserFromToken } from "../utils/token";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [accessToken, setAccessToken] = useState(() =>
    localStorage.getItem(ACCESS_TOKEN_KEY),
  );

  const user = useMemo(
    () => (accessToken ? getUserFromToken(accessToken) : null),
    [accessToken],
  );

  const login = async (email, password) => {
    const result = await loginMember(email, password);

    if (result.result !== "success" || !result.token) {
      throw new Error("이메일 또는 비밀번호를 확인해주세요.");
    }

    localStorage.setItem(ACCESS_TOKEN_KEY, result.token);
    if (result.refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, result.refreshToken);
    }
    setAccessToken(result.token);
  };

  const logout = () => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    setAccessToken(null);
  };

  const value = {
    accessToken,
    user,
    isAuthenticated: Boolean(accessToken && user),
    isAdmin: Boolean(user?.roles?.includes("ROLE_ADMIN")),
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
