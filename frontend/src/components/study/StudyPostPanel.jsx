import { useCallback, useEffect, useRef, useState } from "react";
import {
  Check,
  LoaderCircle,
  MessageSquareText,
  Pencil,
  Send,
  Trash2,
  X,
} from "lucide-react";
import {
  createStudyPost,
  deleteStudyPost,
  getStudyPosts,
  updateStudyPost,
} from "../../api/studyPostApi";
import { getErrorMessage } from "../../api/apiClient";
import ConfirmModal from "../common/ConfirmModal";

const PAGE_SIZE = 20;

function formatMessageTime(value) {
  if (!value) return "";

  return new Intl.DateTimeFormat("ko-KR", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}

function getInitial(nickname) {
  return nickname?.trim().slice(0, 1) || "?";
}

export default function StudyPostPanel({ studyId }) {
  const listRef = useRef(null);
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [content, setContent] = useState("");
  const [error, setError] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [editingContent, setEditingContent] = useState("");
  const [actionId, setActionId] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const scrollToBottom = useCallback((behavior = "auto") => {
    requestAnimationFrame(() => {
      const element = listRef.current;
      if (element) {
        element.scrollTo({ top: element.scrollHeight, behavior });
      }
    });
  }, []);

  const loadInitial = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const result = await getStudyPosts(studyId, 0, PAGE_SIZE);
      setPosts([...(result.content ?? [])].reverse());
      setPage(0);
      setHasMore(!result.last);
      scrollToBottom();
    } catch (requestError) {
      setError(
        getErrorMessage(requestError, "스터디 라운지를 불러오지 못했습니다."),
      );
    } finally {
      setLoading(false);
    }
  }, [scrollToBottom, studyId]);

  useEffect(() => {
    loadInitial();
  }, [loadInitial]);

  const loadOlderPosts = async () => {
    if (!hasMore || loadingMore) return;

    const element = listRef.current;
    const previousHeight = element?.scrollHeight ?? 0;
    const nextPage = page + 1;

    setLoadingMore(true);
    setError("");
    try {
      const result = await getStudyPosts(studyId, nextPage, PAGE_SIZE);
      const olderPosts = [...(result.content ?? [])].reverse();
      setPosts((current) => [...olderPosts, ...current]);
      setPage(nextPage);
      setHasMore(!result.last);

      requestAnimationFrame(() => {
        if (element) {
          element.scrollTop = element.scrollHeight - previousHeight;
        }
      });
    } catch (requestError) {
      setError(
        getErrorMessage(requestError, "이전 메시지를 불러오지 못했습니다."),
      );
    } finally {
      setLoadingMore(false);
    }
  };

  const handleScroll = (event) => {
    if (event.currentTarget.scrollTop <= 36) {
      loadOlderPosts();
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const trimmedContent = content.trim();
    if (!trimmedContent || submitting) return;

    setSubmitting(true);
    setError("");
    try {
      const createdPost = await createStudyPost(studyId, trimmedContent);
      setPosts((current) => [...current, createdPost]);
      setContent("");
      scrollToBottom("smooth");
    } catch (requestError) {
      setError(getErrorMessage(requestError, "메시지를 등록하지 못했습니다."));
    } finally {
      setSubmitting(false);
    }
  };

  const handleComposerKeyDown = (event) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      event.currentTarget.form?.requestSubmit();
    }
  };

  const startEditing = (post) => {
    setEditingId(post.postId);
    setEditingContent(post.content);
    setError("");
  };

  const cancelEditing = () => {
    setEditingId(null);
    setEditingContent("");
  };

  const saveEditing = async (postId) => {
    const trimmedContent = editingContent.trim();
    if (!trimmedContent || actionId) return;

    setActionId(postId);
    setError("");
    try {
      const updatedPost = await updateStudyPost(
        studyId,
        postId,
        trimmedContent,
      );
      setPosts((current) =>
        current.map((post) => (post.postId === postId ? updatedPost : post)),
      );
      cancelEditing();
    } catch (requestError) {
      setError(
        getErrorMessage(
          requestError,
          "본인이 작성한 메시지만 수정할 수 있습니다.",
        ),
      );
    } finally {
      setActionId(null);
    }
  };

  const confirmDelete = async () => {
    if (!deleteTarget || actionId) return;

    const postId = deleteTarget.postId;
    setActionId(postId);
    setError("");
    try {
      await deleteStudyPost(studyId, postId);
      setPosts((current) => current.filter((post) => post.postId !== postId));
      setDeleteTarget(null);
    } catch (requestError) {
      setError(
        getErrorMessage(
          requestError,
          "본인이 작성한 메시지만 삭제할 수 있습니다.",
        ),
      );
      setDeleteTarget(null);
    } finally {
      setActionId(null);
    }
  };

  return (
    <>
      <section className="card study-post-panel">
        <header className="study-post-header">
          <div className="study-post-heading">
            <span className="study-post-heading-icon">
              <MessageSquareText size={19} />
            </span>
            <div>
              <span className="eyebrow">STUDY LOUNGE</span>
              <h2>스터디 라운지</h2>
            </div>
          </div>
          <span className="study-post-count">
            <i />
            {posts.length}개의 메시지
          </span>
        </header>

        {error && (
          <div className="study-post-error" role="alert">
            <span>{error}</span>
            <button type="button" onClick={() => setError("")}>
              <X size={14} />
            </button>
          </div>
        )}

        <div
          className="study-post-scroll"
          ref={listRef}
          onScroll={handleScroll}
        >
          {loading ? (
            <div className="study-post-state">
              <LoaderCircle className="spin" size={23} />
              메시지를 불러오는 중...
            </div>
          ) : (
            <>
              <div className="study-post-history">
                {loadingMore ? (
                  <>
                    <LoaderCircle className="spin" size={14} />
                    이전 메시지를 불러오는 중...
                  </>
                ) : hasMore ? (
                  "위로 스크롤하면 이전 메시지를 볼 수 있어요."
                ) : posts.length > 0 ? (
                  "라운지의 첫 메시지입니다."
                ) : null}
              </div>

              {posts.length === 0 ? (
                <div className="study-post-empty">
                  <MessageSquareText size={29} />
                  <strong>아직 메시지가 없습니다.</strong>
                  <span>첫 번째 이야기를 남겨보세요.</span>
                </div>
              ) : (
                posts.map((post) => (
                  <article className="study-post-message" key={post.postId}>
                    <span className="study-post-avatar">
                      {getInitial(post.nickname)}
                    </span>
                    <div className="study-post-body">
                      <div className="study-post-meta">
                        <strong>{post.nickname}</strong>
                        <time dateTime={post.createdAt}>
                          {formatMessageTime(post.createdAt)}
                        </time>
                      </div>

                      {editingId === post.postId ? (
                        <div className="study-post-edit">
                          <textarea
                            value={editingContent}
                            maxLength={2000}
                            autoFocus
                            onChange={(event) =>
                              setEditingContent(event.target.value)
                            }
                          />
                          <div>
                            <button
                              className="study-post-mini-button"
                              type="button"
                              onClick={cancelEditing}
                            >
                              <X size={14} />
                              취소
                            </button>
                            <button
                              className="study-post-mini-button primary"
                              type="button"
                              disabled={
                                !editingContent.trim() ||
                                actionId === post.postId
                              }
                              onClick={() => saveEditing(post.postId)}
                            >
                              {actionId === post.postId ? (
                                <LoaderCircle className="spin" size={14} />
                              ) : (
                                <Check size={14} />
                              )}
                              저장
                            </button>
                          </div>
                        </div>
                      ) : (
                        <p>{post.content}</p>
                      )}
                    </div>

                    {editingId !== post.postId && (
                      <div className="study-post-actions">
                        <button
                          type="button"
                          title="메시지 수정"
                          aria-label="메시지 수정"
                          onClick={() => startEditing(post)}
                        >
                          <Pencil size={14} />
                        </button>
                        <button
                          className="danger"
                          type="button"
                          title="메시지 삭제"
                          aria-label="메시지 삭제"
                          onClick={() => setDeleteTarget(post)}
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    )}
                  </article>
                ))
              )}
            </>
          )}
        </div>

        <form className="study-post-composer" onSubmit={handleSubmit}>
          <textarea
            value={content}
            maxLength={2000}
            rows={2}
            placeholder="스터디원들과 이야기를 나눠보세요."
            aria-label="메시지 내용"
            onChange={(event) => setContent(event.target.value)}
            onKeyDown={handleComposerKeyDown}
          />
          <div className="study-post-composer-footer">
            <span>Enter 전송 · Shift + Enter 줄바꿈</span>
            <button
              type="submit"
              aria-label="메시지 전송"
              disabled={!content.trim() || submitting}
            >
              {submitting ? (
                <LoaderCircle className="spin" size={17} />
              ) : (
                <Send size={17} />
              )}
            </button>
          </div>
        </form>
      </section>

      <ConfirmModal
        open={Boolean(deleteTarget)}
        title="메시지를 삭제할까요?"
        description="삭제한 메시지는 다시 복구할 수 없습니다."
        confirmLabel="삭제"
        destructive
        loading={Boolean(actionId)}
        onConfirm={confirmDelete}
        onClose={() => !actionId && setDeleteTarget(null)}
      />
    </>
  );
}
