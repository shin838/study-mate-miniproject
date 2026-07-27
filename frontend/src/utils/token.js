export function decodeJwt(token) {
  try {
    const payload = token.split(".")[1];
    const normalized = payload.replace(/-/g, "+").replace(/_/g, "/");
    const decoded = decodeURIComponent(
      atob(normalized)
        .split("")
        .map((char) => `%${char.charCodeAt(0).toString(16).padStart(2, "0")}`)
        .join(""),
    );
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}

export function getUserFromToken(token) {
  const payload = decodeJwt(token);
  if (!payload) return null;

  return {
    email: payload.sub || "로그인 사용자",
    roles: Array.isArray(payload.roles) ? payload.roles : [],
    expiresAt: payload.exp ? payload.exp * 1000 : null,
  };
}
