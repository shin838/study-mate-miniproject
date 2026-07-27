import { ChevronLeft, ChevronRight } from "lucide-react";

export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null;

  const visiblePages = Array.from(
    { length: Math.min(totalPages, 5) },
    (_, index) => {
      const start = Math.max(
        0,
        Math.min(page - 2, Math.max(totalPages - 5, 0)),
      );
      return start + index;
    },
  );

  return (
    <nav className="pagination" aria-label="페이지 이동">
      <button
        type="button"
        aria-label="이전 페이지"
        disabled={page === 0}
        onClick={() => onChange(page - 1)}
      >
        <ChevronLeft size={17} />
      </button>
      {visiblePages.map((pageNumber) => (
        <button
          key={pageNumber}
          type="button"
          className={page === pageNumber ? "active" : ""}
          aria-current={page === pageNumber ? "page" : undefined}
          onClick={() => onChange(pageNumber)}
        >
          {pageNumber + 1}
        </button>
      ))}
      <button
        type="button"
        aria-label="다음 페이지"
        disabled={page >= totalPages - 1}
        onClick={() => onChange(page + 1)}
      >
        <ChevronRight size={17} />
      </button>
    </nav>
  );
}
