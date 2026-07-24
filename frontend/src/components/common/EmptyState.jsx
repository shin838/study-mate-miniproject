import { Inbox } from "lucide-react";

export default function EmptyState({
  title = "표시할 내용이 없습니다",
  description = "새로운 내용이 등록되면 여기에 표시됩니다.",
  action,
}) {
  return (
    <div className="state-panel empty-state">
      <span className="state-icon">
        <Inbox size={24} />
      </span>
      <h3>{title}</h3>
      <p>{description}</p>
      {action}
    </div>
  );
}
