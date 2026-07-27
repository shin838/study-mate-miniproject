import { useEffect, useState } from "react";
import { ArrowLeft, Check, FileText, Type, Users } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import { createStudy, getStudy, updateStudy } from "../api/studyApi";
import { getErrorMessage } from "../api/apiClient";
import { useStudies } from "../context/StudyContext";
import Loading from "../components/common/Loading";

const initialForm = {
  title: "",
  content: "",
  maxMember: 1,
};

export default function StudyFormPage() {
  const { studyId } = useParams();
  const isEdit = Boolean(studyId);
  const navigate = useNavigate();
  const { refreshParticipatingStudies } = useStudies();
  const [form, setForm] = useState(initialForm);
  const [initialLoading, setInitialLoading] = useState(isEdit);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!isEdit) return;

    getStudy(studyId)
      .then((study) =>
        setForm({
          title: study.title,
          content: study.content,
          maxMember: study.maxMember,
        }),
      )
      .catch((requestError) =>
        setError(getErrorMessage(requestError, "수정할 스터디를 불러오지 못했습니다.")),
      )
      .finally(() => setInitialLoading(false));
  }, [isEdit, studyId]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({
      ...current,
      [name]: name === "maxMember" ? Number(value) : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (!form.title.trim() || !form.content.trim()) {
      setError("제목과 내용을 모두 입력해주세요.");
      return;
    }

    if (form.maxMember < 1) {
      setError("최대 인원은 1명 이상이어야 합니다.");
      return;
    }

    setSubmitting(true);
    try {
      const result = isEdit
        ? await updateStudy(studyId, form)
        : await createStudy(form);
      await refreshParticipatingStudies();
      navigate(`/studies/${result.studyId}`, { replace: true });
    } catch (requestError) {
      setError(getErrorMessage(requestError, "스터디를 저장하지 못했습니다."));
    } finally {
      setSubmitting(false);
    }
  };

  if (initialLoading) return <div className="page"><Loading /></div>;

  return (
    <div className="page narrow-page">
      <button className="back-link" type="button" onClick={() => navigate(-1)}>
        <ArrowLeft size={17} />
        돌아가기
      </button>
      <header className="page-header">
        <div>
          <span className="eyebrow">{isEdit ? "EDIT STUDY" : "CREATE STUDY"}</span>
          <h1>{isEdit ? "스터디 정보 수정" : "새로운 스터디 만들기"}</h1>
          <p>
            {isEdit
              ? "변경할 내용을 확인하고 저장해주세요."
              : "함께 배우고 싶은 주제와 목표를 소개해주세요."}
          </p>
        </div>
      </header>

      <form className="study-form card" onSubmit={handleSubmit}>
        {error && <div className="form-error">{error}</div>}

        <label className="field">
          <span>스터디 제목</span>
          <div className="input-with-icon">
            <Type size={18} />
            <input
              name="title"
              value={form.title}
              maxLength={100}
              placeholder="예: 매일 함께하는 Java 알고리즘"
              onChange={handleChange}
            />
          </div>
          <small className="field-hint">{form.title.length}/100</small>
        </label>

        <label className="field">
          <span>스터디 소개</span>
          <div className="textarea-with-icon">
            <FileText size={18} />
            <textarea
              name="content"
              value={form.content}
              rows={10}
              placeholder="스터디 목표, 진행 방식, 함께하고 싶은 메이트를 소개해주세요."
              onChange={handleChange}
            />
          </div>
        </label>

        <label className="field member-count-field">
          <span>최대 멤버 수</span>
          <div className="input-with-icon">
            <Users size={18} />
            <input
              type="number"
              name="maxMember"
              min="1"
              value={form.maxMember}
              onChange={handleChange}
            />
          </div>
          <small className="field-hint">리더를 제외한 일반 멤버 기준입니다.</small>
        </label>

        <div className="form-actions">
          <button className="button ghost" type="button" onClick={() => navigate(-1)}>
            취소
          </button>
          <button className="button primary" type="submit" disabled={submitting}>
            <Check size={17} />
            {submitting ? "저장 중..." : isEdit ? "변경사항 저장" : "스터디 만들기"}
          </button>
        </div>
      </form>
    </div>
  );
}
