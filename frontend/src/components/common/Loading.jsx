export default function Loading({ label = "불러오는 중" }) {
  return (
    <div className="state-panel" role="status" aria-live="polite">
      <span className="spinner" />
      <p>{label}</p>
    </div>
  );
}
