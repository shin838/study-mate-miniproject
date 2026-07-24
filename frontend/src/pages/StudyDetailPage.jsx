import { useCallback, useEffect, useMemo, useState } from "react";
import {
  ArrowLeft,
  CalendarDays,
  Edit3,
  LogIn,
  LogOut,
  Trash2,
  UserRound,
  Users,
} from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import {
  applyStudy,
  cancelStudyApplication,
  deleteStudy,
  getStudy,
} from "../api/studyApi";
import {
  getMyCreatedStudies,
  getMyParticipatingStudies,
} from "../api/myPageApi";
import { getErrorMessage } from "../api/apiClient";
import { useStudies } from "../context/StudyContext";
import { formatDate } from "../utils/format";
import StatusBadge from "../components/study/StatusBadge";
import Loading from "../components/common/Loading";
import ErrorMessage from "../components/common/ErrorMessage";
import ConfirmModal from "../components/common/ConfirmModal";

export default function StudyDetailPage() {
  const { studyId } = useParams();
  const navigate = useNavigate();
  const { refreshParticipatingStudies } = useStudies();
  const [study, setStudy] = useState(null);
  const [participation, setParticipation] = useState(null);
  const [isCreator, setIsCreator] = useState(false);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState("");
  const [feedback, setFeedback] = useState("");
  const [confirmAction, setConfirmAction] = useState(null);

  const loadDetail = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [studyData, participating, created] = await Promise.all([
        getStudy(studyId),
        getMyParticipatingStudies(),
        getMyCreatedStudies(),
      ]);
      setStudy(studyData);
      setParticipation(
        participating.find((item) => String(item.studyId) === String(studyId)) ||
          null,
      );
      setIsCreator(
        created.some((item) => String(item.studyId) === String(studyId)),
      );
    } catch (requestError) {
      setError(getErrorMessage(requestError, "스터디 정보를 불러오지 못했습니다."));
    } finally {
      setLoading(false);
    }
  }, [studyId]);

  useEffect(() => {
    loadDetail();
  }, [loadDetail]);

  const actionCopy = useMemo(
    () => ({
      delete: {
        title: "스터디를 삭제할까요?",
        description:
          "삭제한 모집글과 관련 데이터는 다시 복구할 수 없습니다.",
        label: "삭제",
        destructive: true,
      },
      cancel: {
        title: "스터디 참여를 취소할까요?",
        description: "참여 목록에서 사라지며, 모집 중이라면 다시 신청할 수 있어요.",
        label: "참여 취소",
        destructive: true,
      },
      apply: {
        title: "이 스터디에 참여할까요?",
        description: "신청과 동시에 스터디 멤버로 등록됩니다.",
        label: "참여하기",
      },
    }),
    [],
  );

  const handleConfirmedAction = async () => {
    setActionLoading(true);
    setFeedback("");
    try {
      if (confirmAction === "delete") {
        await deleteStudy(studyId);
        await refreshParticipatingStudies();
        navigate("/studies", { replace: true });
        return;
      }

      if (confirmAction === "apply") {
        await applyStudy(studyId);
        setFeedback("스터디에 참여했어요.");
      }

      if (confirmAction === "cancel") {
        await cancelStudyApplication(studyId);
        setFeedback("스터디 참여를 취소했어요.");
      }

      await Promise.all([loadDetail(), refreshParticipatingStudies()]);
      setConfirmAction(null);
    } catch (requestError) {
      setFeedback(getErrorMessage(requestError));
      setConfirmAction(null);
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) return <div className="page"><Loading /></div>;
  if (error) {
    return (
      <div className="page">
        <ErrorMessage message={error} onRetry={loadDetail} />
      </div>
    );
  }

  return (
    <div className="page detail-page">
      <button className="back-link" type="button" onClick={() => navigate(-1)}>
        <ArrowLeft size={17} />
        목록으로 돌아가기
      </button>

      {feedback && <div className="feedback-banner">{feedback}</div>}

      <article className="detail-hero">
        <div className="detail-title-row">
          <div>
            <StatusBadge status={study.status} />
            <h1>{study.title}</h1>
          </div>
          <div className="detail-actions">
            {isCreator && (
              <>
                <button
                  className="button secondary"
                  type="button"
                  onClick={() => navigate(`/studies/${studyId}/edit`)}
                >
                  <Edit3 size={17} />
                  수정
                </button>
                <button
                  className="button danger-ghost"
                  type="button"
                  onClick={() => setConfirmAction("delete")}
                >
                  <Trash2 size={17} />
                  삭제
                </button>
              </>
            )}
            {!participation && !isCreator && study.status === "RECRUITING" && (
              <button
                className="button primary"
                type="button"
                onClick={() => setConfirmAction("apply")}
              >
                <LogIn size={17} />
                참여하기
              </button>
            )}
            {participation?.studyRole === "MEMBER" && (
              <button
                className="button danger-ghost"
                type="button"
                onClick={() => setConfirmAction("cancel")}
              >
                <LogOut size={17} />
                참여 취소
              </button>
            )}
          </div>
        </div>

        <div className="detail-meta-strip">
          <span>
            <UserRound size={17} />
            <small>작성자</small>
            <strong>{study.creatorNickname}</strong>
          </span>
          <span>
            <Users size={17} />
            <small>모집 인원</small>
            <strong>최대 {study.maxMember}명</strong>
          </span>
          <span>
            <CalendarDays size={17} />
            <small>작성일</small>
            <strong>{formatDate(study.createdAt)}</strong>
          </span>
        </div>
      </article>

      <section className="detail-body card">
        <div className="section-heading">
          <span className="eyebrow">ABOUT THIS STUDY</span>
          <h2>스터디 소개</h2>
        </div>
        <p className="study-content">{study.content}</p>
      </section>

      {participation && (
        <section className="participation-callout">
          <span className="channel-avatar">
            {participation.studyRole === "LEADER" ? "♛" : "✓"}
          </span>
          <div>
            <strong>현재 이 스터디에 참여 중이에요.</strong>
            <p>
              내 역할은 {participation.studyRole === "LEADER" ? "리더" : "멤버"}입니다.
            </p>
          </div>
          <button
            className="button secondary"
            type="button"
            onClick={() => navigate(`/my/applications/${studyId}`)}
          >
            스터디 공간으로
          </button>
        </section>
      )}

      <ConfirmModal
        open={Boolean(confirmAction)}
        title={actionCopy[confirmAction]?.title}
        description={actionCopy[confirmAction]?.description}
        confirmLabel={actionCopy[confirmAction]?.label}
        destructive={actionCopy[confirmAction]?.destructive}
        loading={actionLoading}
        onClose={() => setConfirmAction(null)}
        onConfirm={handleConfirmedAction}
      />
    </div>
  );
}
