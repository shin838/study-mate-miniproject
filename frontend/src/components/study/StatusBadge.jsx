import { statusLabel } from "../../utils/format";

export default function StatusBadge({ status }) {
  return (
    <span className={`status-badge ${status?.toLowerCase()}`}>
      <i />
      {statusLabel(status)}
    </span>
  );
}
