import { useCallback, useEffect, useState } from "react";
import { Plus, Sparkles } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { getMyCreatedStudies } from "../api/myPageApi";
import { getErrorMessage } from "../api/apiClient";
import StudyCard from "../components/study/StudyCard";
import Loading from "../components/common/Loading";
import EmptyState from "../components/common/EmptyState";
import ErrorMessage from "../components/common/ErrorMessage";

export default function MyCreatedStudiesPage() {
  const navigate = useNavigate();
  const [studies, setStudies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadStudies = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setStudies(await getMyCreatedStudies());
    } catch (requestError) {
      setError(getErrorMessage(requestError, "내 스터디를 불러오지 못했습니다."));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadStudies();
  }, [loadStudies]);

  return (
    <div className="page">
      <header className="page-header board-header">
        <div>
          <span className="eyebrow">
            <Sparkles size={13} />
            CREATED BY ME
          </span>
          <h1>내가 만든 스터디</h1>
          <p>직접 개설한 모집글을 확인하고 관리할 수 있어요.</p>
        </div>
        <button className="button primary" onClick={() => navigate("/studies/new")}>
          <Plus size={18} />
          새 스터디
        </button>
      </header>

      {loading && <Loading />}
      {!loading && error && <ErrorMessage message={error} onRetry={loadStudies} />}
      {!loading && !error && studies.length === 0 && (
        <EmptyState
          title="아직 만든 스터디가 없습니다"
          description="새로운 주제로 첫 스터디를 시작해보세요."
        />
      )}
      {!loading && !error && studies.length > 0 && (
        <section className="study-grid">
          {studies.map((study) => (
            <StudyCard key={study.studyId} study={study} />
          ))}
        </section>
      )}
    </div>
  );
}
