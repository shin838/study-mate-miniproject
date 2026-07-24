import { Crown, UserRound } from "lucide-react";
import { formatDate, roleLabel } from "../../utils/format";

export default function StudyMemberList({ members = [] }) {
  return (
    <div className="member-list">
      {members.map((member) => (
        <div className="member-row" key={member.memberId}>
          <span
            className={`member-avatar ${
              member.studyRole === "LEADER" ? "leader" : ""
            }`}
          >
            {member.studyRole === "LEADER" ? (
              <Crown size={17} />
            ) : (
              <UserRound size={17} />
            )}
          </span>
          <span className="member-main">
            <strong>{member.nickname}</strong>
            <small>{member.name}</small>
          </span>
          <span className={`role-chip ${member.studyRole.toLowerCase()}`}>
            {roleLabel(member.studyRole)}
          </span>
          <small className="member-joined">
            {formatDate(member.joinedAt)} 참여
          </small>
        </div>
      ))}
    </div>
  );
}
