import { X } from "lucide-react";

export default function ConfirmModal({
  open,
  title,
  description,
  confirmLabel = "확인",
  destructive = false,
  loading = false,
  onConfirm,
  onClose,
  children,
}) {
  if (!open) return null;

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={onClose}>
      <section
        className="modal-card"
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <button
          className="icon-button modal-close"
          type="button"
          aria-label="닫기"
          onClick={onClose}
        >
          <X size={19} />
        </button>
        <span className="eyebrow">PLEASE CONFIRM</span>
        <h2 id="modal-title">{title}</h2>
        {description && <p className="modal-description">{description}</p>}
        {children}
        <div className="modal-actions">
          <button
            className="button ghost"
            type="button"
            disabled={loading}
            onClick={onClose}
          >
            취소
          </button>
          <button
            className={`button ${destructive ? "danger" : "primary"}`}
            type="button"
            disabled={loading}
            onClick={onConfirm}
          >
            {loading ? "처리 중..." : confirmLabel}
          </button>
        </div>
      </section>
    </div>
  );
}
