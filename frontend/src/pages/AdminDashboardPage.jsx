import { useCallback, useEffect, useMemo, useState } from "react";
import {
  BookOpen,
  ChevronRight,
  Eye,
  ShieldCheck,
  Trash2,
  UserRound,
  Users,
} from "lucide-react";
import {
  changeAdminStudyStatus,
  deleteAdminMember,
  deleteAdminStudy,
  getAdminMemberDetail,
  getAdminMembers,
  getAdminStudies,
} from "../api/adminApi";
import { getErrorMessage } from "../api/apiClient";
import ConfirmModal from "../components/common/ConfirmModal";
import EmptyState from "../components/common/EmptyState";
import ErrorMessage from "../components/common/ErrorMessage";
import Loading from "../components/common/Loading";
import StatusBadge from "../components/study/StatusBadge";
import { useAuth } from "../context/AuthContext";
import { formatDate } from "../utils/format";

const TABS = {
  MEMBERS: "members",
  STUDIES: "studies",
};

export default function AdminDashboardPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState(TABS.MEMBERS);
  const [members, setMembers] = useState([]);
  const [studies, setStudies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionError, setActionError] = useState("");
  const [selectedMember, setSelectedMember] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [confirmTarget, setConfirmTarget] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [changingStudyId, setChangingStudyId] = useState(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const [memberData, studyData] = await Promise.all([
        getAdminMembers(),
        getAdminStudies(),
      ]);
      setMembers(Array.isArray(memberData) ? memberData : []);
      setStudies(Array.isArray(studyData) ? studyData : []);
    } catch (requestError) {
      setError(getErrorMessage(requestError, "관리자 데이터를 불러오지 못했습니다."));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const stats = useMemo(
    () => ({
      memberCount: members.length,
      studyCount: studies.length,
      recruitingCount: studies.filter((study) => study.status === "RECRUITING")
        .length,
    }),
    [members, studies],
  );

  const openMemberDetail = async (memberId) => {
    setDetailLoading(true);
    setActionError("");

    try {
      const detail = await getAdminMemberDetail(memberId);
      setSelectedMember(detail);
    } catch (requestError) {
      setActionError(
        getErrorMessage(requestError, "회원 상세 정보를 불러오지 못했습니다."),
      );
    } finally {
      setDetailLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirmTarget) return;

    setActionLoading(true);
    setActionError("");

    try {
      if (confirmTarget.type === "member") {
        const result = await deleteAdminMember(confirmTarget.id);
        if (result?.result !== "success") {
          throw new Error("회원을 삭제하지 못했습니다.");
        }
        setMembers((current) =>
          current.filter((member) => member.memberId !== confirmTarget.id),
        );
      } else {
        await deleteAdminStudy(confirmTarget.id);
        setStudies((current) =>
          current.filter((study) => study.studyId !== confirmTarget.id),
        );
      }
      setConfirmTarget(null);
    } catch (requestError) {
      setActionError(getErrorMessage(requestError, requestError.message));
      setConfirmTarget(null);
    } finally {
      setActionLoading(false);
    }
  };

  const handleStatusChange = async (study, nextStatus) => {
    if (study.status === nextStatus) return;

    setChangingStudyId(study.studyId);
    setActionError("");

    try {
      const updated = await changeAdminStudyStatus(study.studyId, nextStatus);
      setStudies((current) =>
        current.map((item) =>
          item.studyId === study.studyId ? { ...item, ...updated } : item,
        ),
      );
    } catch (requestError) {
      setActionError(
        getErrorMessage(requestError, "스터디 상태를 변경하지 못했습니다."),
      );
    } finally {
      setChangingStudyId(null);
    }
  };

  if (loading) {
    return (
      <div className="page admin-page">
        <Loading label="관리자 콘솔을 준비하는 중" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="page admin-page">
        <ErrorMessage message={error} onRetry={loadData} />
      </div>
    );
  }

  return (
    <div className="page admin-page">
      <header className="page-header admin-header">
        <div>
          <span className="eyebrow">ADMIN CONSOLE</span>
          <h1>서비스 관리</h1>
          <p>회원과 스터디 상태를 한곳에서 안전하게 관리하세요.</p>
        </div>
        <span className="admin-identity">
          <ShieldCheck size={18} />
          <span>
            <strong>관리자 접속 중</strong>
            <small>{user?.email}</small>
          </span>
        </span>
      </header>

      <section className="admin-stat-grid" aria-label="서비스 현황">
        <article className="admin-stat-card">
          <span className="admin-stat-icon violet">
            <Users size={21} />
          </span>
          <div>
            <small>전체 회원</small>
            <strong>{stats.memberCount}</strong>
          </div>
        </article>
        <article className="admin-stat-card">
          <span className="admin-stat-icon blue">
            <BookOpen size={21} />
          </span>
          <div>
            <small>전체 스터디</small>
            <strong>{stats.studyCount}</strong>
          </div>
        </article>
        <article className="admin-stat-card">
          <span className="admin-stat-icon green">
            <ShieldCheck size={21} />
          </span>
          <div>
            <small>모집 중</small>
            <strong>{stats.recruitingCount}</strong>
          </div>
        </article>
      </section>

      {actionError && (
        <div className="admin-inline-error" role="alert">
          {actionError}
        </div>
      )}

      <section className="admin-workspace">
        <div className="admin-tabs" role="tablist" aria-label="관리 대상">
          <button
            type="button"
            className={activeTab === TABS.MEMBERS ? "active" : ""}
            role="tab"
            aria-selected={activeTab === TABS.MEMBERS}
            onClick={() => setActiveTab(TABS.MEMBERS)}
          >
            <Users size={17} />
            회원 관리
            <span>{members.length}</span>
          </button>
          <button
            type="button"
            className={activeTab === TABS.STUDIES ? "active" : ""}
            role="tab"
            aria-selected={activeTab === TABS.STUDIES}
            onClick={() => setActiveTab(TABS.STUDIES)}
          >
            <BookOpen size={17} />
            스터디 관리
            <span>{studies.length}</span>
          </button>
        </div>

        {activeTab === TABS.MEMBERS ? (
          <MemberManagement
            members={members}
            currentEmail={user?.email}
            detailLoading={detailLoading}
            onView={openMemberDetail}
            onDelete={(member) =>
              setConfirmTarget({
                type: "member",
                id: member.memberId,
                label: member.nickname || member.email,
              })
            }
          />
        ) : (
          <StudyManagement
            studies={studies}
            changingStudyId={changingStudyId}
            onStatusChange={handleStatusChange}
            onDelete={(study) =>
              setConfirmTarget({
                type: "study",
                id: study.studyId,
                label: study.title,
              })
            }
          />
        )}
      </section>

      <ConfirmModal
        open={Boolean(selectedMember)}
        title="회원 상세 정보"
        confirmLabel="닫기"
        onConfirm={() => setSelectedMember(null)}
        onClose={() => setSelectedMember(null)}
      >
        {selectedMember && (
          <div className="admin-member-detail">
            <span className="member-avatar admin-detail-avatar">
              <UserRound size={22} />
            </span>
            <div className="admin-detail-heading">
              <strong>{selectedMember.nickname}</strong>
              <span>{selectedMember.email}</span>
            </div>
            <dl>
              <div>
                <dt>회원 번호</dt>
                <dd>#{selectedMember.memberId}</dd>
              </div>
              <div>
                <dt>이름</dt>
                <dd>{selectedMember.name}</dd>
              </div>
              <div>
                <dt>권한</dt>
                <dd className="admin-role-list">
                  {selectedMember.roles?.map((role) => (
                    <span key={role}>{role}</span>
                  ))}
                </dd>
              </div>
            </dl>
          </div>
        )}
      </ConfirmModal>

      <ConfirmModal
        open={Boolean(confirmTarget)}
        title={
          confirmTarget?.type === "member" ? "회원을 삭제할까요?" : "스터디를 삭제할까요?"
        }
        description={
          confirmTarget &&
          `"${confirmTarget.label}" 관련 데이터가 삭제되며 이 작업은 되돌릴 수 없습니다.`
        }
        confirmLabel="삭제"
        destructive
        loading={actionLoading}
        onConfirm={handleDelete}
        onClose={() => !actionLoading && setConfirmTarget(null)}
      />
    </div>
  );
}

function MemberManagement({
  members,
  currentEmail,
  detailLoading,
  onView,
  onDelete,
}) {
  if (members.length === 0) {
    return (
      <EmptyState
        title="등록된 회원이 없습니다"
        description="회원이 가입하면 이곳에서 확인할 수 있습니다."
      />
    );
  }

  return (
    <div className="admin-table-wrap">
      <table className="admin-table">
        <thead>
          <tr>
            <th>회원</th>
            <th>이메일</th>
            <th>회원 번호</th>
            <th className="admin-action-column">관리</th>
          </tr>
        </thead>
        <tbody>
          {members.map((member) => {
            const isCurrentAdmin = member.email === currentEmail;
            return (
              <tr key={member.memberId}>
                <td data-label="회원">
                  <span className="admin-member-cell">
                    <span className="member-avatar">
                      {(member.nickname || member.name || "?").slice(0, 1)}
                    </span>
                    <span>
                      <strong>{member.nickname || "-"}</strong>
                      <small>{member.name || "-"}</small>
                    </span>
                  </span>
                </td>
                <td data-label="이메일">{member.email}</td>
                <td data-label="회원 번호">#{member.memberId}</td>
                <td data-label="관리">
                  <div className="admin-row-actions">
                    <button
                      type="button"
                      className="icon-button"
                      aria-label={`${member.nickname} 상세 조회`}
                      title="상세 조회"
                      disabled={detailLoading}
                      onClick={() => onView(member.memberId)}
                    >
                      <Eye size={16} />
                    </button>
                    <button
                      type="button"
                      className="icon-button danger-icon"
                      aria-label={`${member.nickname} 회원 삭제`}
                      title={
                        isCurrentAdmin ? "현재 로그인한 계정은 삭제할 수 없습니다" : "회원 삭제"
                      }
                      disabled={isCurrentAdmin}
                      onClick={() => onDelete(member)}
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

function StudyManagement({
  studies,
  changingStudyId,
  onStatusChange,
  onDelete,
}) {
  if (studies.length === 0) {
    return (
      <EmptyState
        title="등록된 스터디가 없습니다"
        description="스터디가 만들어지면 이곳에서 관리할 수 있습니다."
      />
    );
  }

  return (
    <div className="admin-study-list">
      {studies.map((study) => (
        <article className="admin-study-row" key={study.studyId}>
          <div className="admin-study-main">
            <div className="admin-study-title-row">
              <StatusBadge status={study.status} />
              <span>#{study.studyId}</span>
            </div>
            <strong>{study.title}</strong>
            <p>{study.content}</p>
            <div className="admin-study-meta">
              <span>작성자 {study.creatorNickname}</span>
              <span>정원 {study.maxMember}명</span>
              <span>{formatDate(study.createdAt)}</span>
            </div>
          </div>
          <div className="admin-study-actions">
            <label>
              <span>모집 상태</span>
              <select
                value={study.status}
                disabled={changingStudyId === study.studyId}
                onChange={(event) => onStatusChange(study, event.target.value)}
              >
                <option value="RECRUITING">모집 중</option>
                <option value="CLOSED">마감</option>
              </select>
              <ChevronRight size={15} />
            </label>
            <button
              type="button"
              className="button danger compact"
              onClick={() => onDelete(study)}
            >
              <Trash2 size={15} />
              삭제
            </button>
          </div>
        </article>
      ))}
    </div>
  );
}
