import { useCallback, useEffect, useState } from "react";
import { Plus, Search, SlidersHorizontal, Sparkles } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { getStudies } from "../api/studyApi";
import { getErrorMessage } from "../api/apiClient";
import StudyCard from "../components/study/StudyCard";
import Pagination from "../components/study/Pagination";
import Loading from "../components/common/Loading";
import EmptyState from "../components/common/EmptyState";
import ErrorMessage from "../components/common/ErrorMessage";

export default function StudyBoardPage() {
  const navigate = useNavigate();
  const [keywordInput, setKeywordInput] = useState("");
  const [keyword, setKeyword] = useState("");
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadStudies = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setData(await getStudies({ keyword, page, size: 9 }));
    } catch (requestError) {
      setError(getErrorMessage(requestError, "스터디 목록을 불러오지 못했습니다."));
    } finally {
      setLoading(false);
    }
  }, [keyword, page]);

  useEffect(() => {
    loadStudies();
  }, [loadStudies]);

  const handleSearch = (event) => {
    event.preventDefault();
    setPage(0);
    setKeyword(keywordInput);
  };

  return (
    <div className="page">
      <header className="page-header board-header">
        <div>
          <span className="eyebrow">
            <Sparkles size={13} />
            FIND YOUR CREW
          </span>
          <h1>함께할 스터디를 찾아보세요</h1>
          <p>관심 있는 주제를 검색하고 새로운 배움에 합류해보세요.</p>
        </div>
        <button
          className="button primary"
          type="button"
          onClick={() => navigate("/studies/new")}
        >
          <Plus size={18} />
          스터디 만들기
        </button>
      </header>

      <section className="board-toolbar">
        <form className="search-box" onSubmit={handleSearch}>
          <Search size={19} />
          <input
            value={keywordInput}
            placeholder="스터디 제목으로 검색"
            onChange={(event) => setKeywordInput(event.target.value)}
          />
          <button type="submit">검색</button>
        </form>
        <div className="sort-display">
          <SlidersHorizontal size={16} />
          최신 등록순
        </div>
      </section>

      {keyword && !loading && (
        <div className="result-summary">
          <strong>“{keyword}”</strong> 검색 결과 {data?.totalElements || 0}개
          <button
            type="button"
            onClick={() => {
              setKeywordInput("");
              setKeyword("");
              setPage(0);
            }}
          >
            검색 초기화
          </button>
        </div>
      )}

      {loading && <Loading label="모집 중인 스터디를 찾고 있어요" />}
      {!loading && error && <ErrorMessage message={error} onRetry={loadStudies} />}
      {!loading && !error && data?.content?.length === 0 && (
        <EmptyState
          title={keyword ? "검색 결과가 없습니다" : "등록된 스터디가 없습니다"}
          description={
            keyword
              ? "다른 검색어로 다시 찾아보세요."
              : "첫 번째 스터디를 만들어 새로운 메이트를 만나보세요."
          }
          action={
            <button
              className="button primary"
              type="button"
              onClick={() => navigate("/studies/new")}
            >
              <Plus size={17} />
              스터디 만들기
            </button>
          }
        />
      )}
      {!loading && !error && data?.content?.length > 0 && (
        <>
          <section className="study-grid">
            {data.content.map((study) => (
              <StudyCard key={study.studyId} study={study} />
            ))}
          </section>
          <Pagination
            page={data.number}
            totalPages={data.totalPages}
            onChange={setPage}
          />
        </>
      )}
    </div>
  );
}
