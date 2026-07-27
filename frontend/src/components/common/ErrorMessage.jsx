import { AlertCircle, RefreshCw } from "lucide-react";

export default function ErrorMessage({ message, onRetry }) {
  return (
    <div className="state-panel error-state" role="alert">
      <span className="state-icon">
        <AlertCircle size={24} />
      </span>
      <h3>잠시 문제가 생겼어요</h3>
      <p>{message}</p>
      {onRetry && (
        <button className="button secondary" type="button" onClick={onRetry}>
          <RefreshCw size={16} />
          다시 시도
        </button>
      )}
    </div>
  );
}
