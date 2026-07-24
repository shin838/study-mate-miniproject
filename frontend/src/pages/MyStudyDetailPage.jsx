import { useCallback, useEffect, useState } from "react";
import {
  ArrowLeft,
  CalendarDays,
  Crown,
  ExternalLink,
  ShieldCheck,
  Users,
} from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import { getMyParticipatingStudyDetail } from "../api/myPageApi";
import { transferStudyLeader } from "../api/studyApi";
import { getErrorMessage } from "../api/apiClient";
import { useStudies } from "../context/StudyContext";
import { formatDate, roleLabel } from "../utils/format";
import StatusBadge from "../components/study/StatusBadge";
import StudyMemberList from "../components/study/StudyMemberList";
import LeaderTransferModal from "../components/study/LeaderTransferModal";
import Loading from "../components/common/Loading";
import ErrorMessage from "../components/common/ErrorMessage";

export default function MyStudyDetailPage() {
  const { studyId } = useParams();
  const navigate = useNavigate();
  const { refreshParticipatingStudies } = useStudies();
  const [study, setStudy] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [feedback, setFeedback] = useState("");
  const [transferOpen, setTransferOpen] = useState(false);
  const [transferLoading, setTransferLoading] = useState(false);

  const loadDetail = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setStudy(await getMyParticipatingStudyDetail(studyId));
    } catch (requestError) {
      setError(
        getErrorMessage(requestError, "참여 스터디 정보를 불러오지 못했습니다."),
      );
    } finally {
      setLoading(false);
    }
  }, [studyId]);

  useEffect(() => {
    loadDetail();
  }, [loadDetail]);

  const handleTransfer = async (newLeaderId) => {
    setTransferLoading(true);
    setFeedback("");
    try {
      const result = await transferStudyLeader(studyId, newLeaderId);
      setFeedback(
        `${result.newLeaderNickname}님에게 리더 역할을 양도했습니다.`,
      );
      setTransferOpen(false);
      await Promise.all([loadDetail(), refreshParticipatingStudies()]);
    } catch (requestError) {
      setFeedback(getErrorMessage(requestError, "리더를 양도하지 못했습니다."));
      setTransferOpen(false);
    } finally {
      setTransferLoading(false);
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
    <div className="page my-study-page">
      <button className="back-link" type="button" onClick={() => navigate("/studies")}>
        <ArrowLeft size={17} />
        모집 게시판
      </button>
      {feedback && <div className="feedback-banner">{feedback}</div>}

      <header className="my-study-header">
        <div className="my-study-heading">
          <div className="study-symbol">{study.title.slice(0, 1)}</div>
          <div>
            <div className="heading-badges">
              <StatusBadge status={study.status} />
              <span className={`role-chip ${study.myRole.toLowerCase()}`}>
                {study.myRole === "LEADER" && <Crown size={13} />}
                {roleLabel(study.myRole)}
              </span>
            </div>
            <h1>{study.title}</h1>
            <p>{study.content}</p>
          </div>
        </div>
        <div className="header-action-group">
          <button
            className="button secondary"
            type="button"
            onClick={() => navigate(`/studies/${studyId}`)}
          >
            <ExternalLink size={17} />
            모집글 보기
          </button>
          {study.myRole === "LEADER" && (
            <button
              className="button primary"
              type="button"
              onClick={() => setTransferOpen(true)}
            >
              <Crown size={17} />
              리더 양도
            </button>
          )}
        </div>
      </header>

      <section className="study-stats">
        <div className="stat-card">
          <span className="stat-icon violet">
            <Users size={20} />
          </span>
          <span>
            <small>현재 멤버</small>
            <strong>
              {study.currentMemberCount}
              <em> / {study.maxMember}명</em>
            </strong>
          </span>
        </div>
        <div className="stat-card">
          <span className="stat-icon blue">
            <ShieldCheck size={20} />
          </span>
          <span>
            <small>내 역할</small>
            <strong>{roleLabel(study.myRole)}</strong>
          </span>
        </div>
        <div className="stat-card">
          <span className="stat-icon green">
            <CalendarDays size={20} />
          </span>
          <span>
            <small>개설일</small>
            <strong>{formatDate(study.createdAt)}</strong>
          </span>
        </div>
      </section>

      <section className="card member-section">
        <div className="section-heading horizontal">
          <div>
            <span className="eyebrow">STUDY MEMBERS</span>
            <h2>함께하는 스터디원</h2>
          </div>
          <span className="member-total">{study.members.length}명</span>
        </div>
        <StudyMemberList members={study.members} />
      </section>

      <LeaderTransferModal
        open={transferOpen}
        members={study.members}
        loading={transferLoading}
        onConfirm={handleTransfer}
        onClose={() => setTransferOpen(false)}
      />
    </div>
  );
}
