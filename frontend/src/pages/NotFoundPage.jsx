import { ArrowLeft } from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <div className="page">
      <div className="state-panel">
        <span className="error-code">404</span>
        <h2>페이지를 찾을 수 없습니다</h2>
        <p>주소가 변경되었거나 존재하지 않는 화면입니다.</p>
        <button className="button primary" onClick={() => navigate("/studies")}>
          <ArrowLeft size={17} />
          모집 게시판으로
        </button>
      </div>
    </div>
  );
}
