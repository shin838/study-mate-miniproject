export function formatDate(value) {
  if (!value) return "-";

  return new Intl.DateTimeFormat("ko-KR", {
    year: "numeric",
    month: "short",
    day: "numeric",
  }).format(new Date(value));
}

export function roleLabel(role) {
  return role === "LEADER" ? "리더" : "멤버";
}

export function statusLabel(status) {
  return status === "RECRUITING" ? "모집 중" : "마감";
}
