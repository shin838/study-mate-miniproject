import { useEffect, useState } from "react";
import { NavLink, useLocation, useNavigate } from "react-router-dom";
import {
  BookOpen,
  ChevronRight,
  Compass,
  Crown,
  LogOut,
  Menu,
  PanelLeftClose,
  Plus,
  ShieldCheck,
  Sparkles,
  UserRound,
  X,
} from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { useStudies } from "../../context/StudyContext";
import { roleLabel } from "../../utils/format";

export default function Sidebar() {
  const { user, isAdmin, logout } = useAuth();
  const {
    participatingStudies,
    sidebarLoading,
    sidebarError,
    refreshParticipatingStudies,
  } = useStudies();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    refreshParticipatingStudies();
  }, [refreshParticipatingStudies]);

  useEffect(() => {
    setMobileOpen(false);
  }, [location.pathname]);

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  const content = (
    <>
      <div className="brand">
        <span className="brand-mark">
          <Sparkles size={20} fill="currentColor" />
        </span>
        <span>
          <strong>Study Mate</strong>
          <small>LEARN TOGETHER</small>
        </span>
        <button
          type="button"
          className="icon-button sidebar-mobile-close"
          aria-label="메뉴 닫기"
          onClick={() => setMobileOpen(false)}
        >
          <X size={20} />
        </button>
      </div>

      <nav className="primary-nav" aria-label="주요 메뉴">
        <NavLink to="/studies" className="nav-item">
          <Compass size={19} />
          <span>스터디 모집 게시판</span>
          <ChevronRight className="nav-chevron" size={16} />
        </NavLink>
        <NavLink to="/my/studies" className="nav-item">
          <BookOpen size={19} />
          <span>내가 만든 스터디</span>
          <ChevronRight className="nav-chevron" size={16} />
        </NavLink>
        <NavLink to="/studies/new" className="nav-item nav-item-create">
          <Plus size={19} />
          <span>새 스터디 만들기</span>
        </NavLink>
        {isAdmin && (
          <NavLink to="/admin" className="nav-item nav-item-admin">
            <ShieldCheck size={19} />
            <span>관리자 콘솔</span>
            <ChevronRight className="nav-chevron" size={16} />
          </NavLink>
        )}
      </nav>

      <div className="sidebar-divider" />

      <section className="study-channel-section">
        <div className="sidebar-section-title">
          <span>참여 중인 스터디</span>
          <span className="count-pill">{participatingStudies.length}</span>
        </div>

        <div className="study-channel-list">
          {sidebarLoading && (
            <div className="sidebar-message">
              <span className="spinner tiny" />
              목록을 불러오는 중
            </div>
          )}
          {!sidebarLoading && sidebarError && (
            <button
              type="button"
              className="sidebar-message error"
              onClick={refreshParticipatingStudies}
            >
              {sidebarError}
            </button>
          )}
          {!sidebarLoading &&
            !sidebarError &&
            participatingStudies.length === 0 && (
              <div className="sidebar-empty">
                참여 중인 스터디가 아직 없어요.
              </div>
            )}
          {participatingStudies.map((study) => (
            <NavLink
              key={study.studyId}
              to={`/my/applications/${study.studyId}`}
              className="study-channel"
            >
              <span className="channel-avatar">
                {study.studyRole === "LEADER" ? (
                  <Crown size={15} />
                ) : (
                  <span>{study.title.slice(0, 1)}</span>
                )}
              </span>
              <span className="channel-copy">
                <strong>{study.title}</strong>
                <small>{roleLabel(study.studyRole)}</small>
              </span>
            </NavLink>
          ))}
        </div>
      </section>

      <div className="sidebar-profile">
        <span className="profile-avatar">
          <UserRound size={19} />
          <i />
        </span>
        <span className="profile-copy">
          <strong>{user?.email?.split("@")[0] || "Study Mate"}</strong>
          <small>
            {isAdmin ? "ADMIN · " : ""}
            {user?.email || "로그인 사용자"}
          </small>
        </span>
        <button
          type="button"
          className="icon-button"
          aria-label="로그아웃"
          title="로그아웃"
          onClick={handleLogout}
        >
          <LogOut size={18} />
        </button>
      </div>
    </>
  );

  return (
    <>
      <button
        type="button"
        className="mobile-menu-button"
        aria-label="메뉴 열기"
        onClick={() => setMobileOpen(true)}
      >
        <Menu size={21} />
      </button>
      {mobileOpen && (
        <button
          className="sidebar-overlay"
          type="button"
          aria-label="메뉴 닫기"
          onClick={() => setMobileOpen(false)}
        />
      )}
      <aside className={`sidebar ${mobileOpen ? "mobile-open" : ""}`}>
        {content}
      </aside>
    </>
  );
}
