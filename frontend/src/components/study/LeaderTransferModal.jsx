import { useEffect, useState } from "react";
import { Crown, UserRound } from "lucide-react";
import ConfirmModal from "../common/ConfirmModal";

export default function LeaderTransferModal({
  open,
  members = [],
  loading,
  onConfirm,
  onClose,
}) {
  const [selectedMemberId, setSelectedMemberId] = useState(null);
  const candidates = members.filter((member) => member.studyRole === "MEMBER");

  useEffect(() => {
    if (!open) setSelectedMemberId(null);
  }, [open]);

  return (
    <ConfirmModal
      open={open}
      title="새로운 리더 선택"
      description="리더 권한을 넘기면 회원님의 역할은 일반 멤버로 변경됩니다."
      confirmLabel="리더 양도"
      loading={loading}
      onClose={onClose}
      onConfirm={() => selectedMemberId && onConfirm(selectedMemberId)}
    >
      <div className="leader-candidate-list">
        {candidates.length === 0 ? (
          <div className="inline-empty">
            양도할 수 있는 일반 스터디원이 없습니다.
          </div>
        ) : (
          candidates.map((member) => (
            <label className="leader-candidate" key={member.memberId}>
              <input
                type="radio"
                name="newLeader"
                value={member.memberId}
                checked={selectedMemberId === member.memberId}
                onChange={() => setSelectedMemberId(member.memberId)}
              />
              <span className="member-avatar">
                <UserRound size={17} />
              </span>
              <span>
                <strong>{member.nickname}</strong>
                <small>{member.name}</small>
              </span>
              {selectedMemberId === member.memberId && (
                <Crown className="candidate-crown" size={18} />
              )}
            </label>
          ))
        )}
      </div>
    </ConfirmModal>
  );
}
