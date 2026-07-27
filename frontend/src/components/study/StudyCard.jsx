import { ArrowUpRight, CalendarDays, UserRound, Users } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { formatDate } from "../../utils/format";
import StatusBadge from "./StatusBadge";

export default function StudyCard({ study, compact = false }) {
  const navigate = useNavigate();

  return (
    <article
      className={`study-card ${compact ? "compact" : ""}`}
      tabIndex={0}
      role="link"
      onClick={() => navigate(`/studies/${study.studyId}`)}
      onKeyDown={(event) => {
        if (event.key === "Enter") navigate(`/studies/${study.studyId}`);
      }}
    >
      <div className="study-card-top">
        <StatusBadge status={study.status} />
        <span className="study-card-arrow">
          <ArrowUpRight size={18} />
        </span>
      </div>
      <h3>{study.title}</h3>
      <div className="study-card-meta">
        <span>
          <UserRound size={15} />
          {study.creatorNickname}
        </span>
        <span>
          <Users size={15} />
          최대 {study.maxMember}명
        </span>
        <span>
          <CalendarDays size={15} />
          {formatDate(study.createdAt)}
        </span>
      </div>
    </article>
  );
}
