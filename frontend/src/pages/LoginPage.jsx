import { useEffect, useState } from "react";
import { ArrowRight, BookOpenCheck, LockKeyhole, Mail, Sparkles } from "lucide-react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { getErrorMessage } from "../api/apiClient";

export default function LoginPage() {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/studies", { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      await login(email, password);
      const destination = location.state?.from?.pathname || "/studies";
      navigate(destination, { replace: true });
    } catch (requestError) {
      setError(getErrorMessage(requestError, requestError.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page">
      <section className="login-story">
        <div className="login-brand">
          <span className="brand-mark large">
            <Sparkles size={24} fill="currentColor" />
          </span>
          <strong>Study Mate</strong>
        </div>
        <div className="story-copy">
          <span className="eyebrow">GROW BETTER, TOGETHER</span>
          <h1>
            배우는 순간을
            <br />
            함께 만드는 공간
          </h1>
          <p>
            관심사가 같은 사람들과 스터디를 시작하고,
            <br />
            작은 성장을 매일 이어가세요.
          </p>
        </div>
        <div className="story-card">
          <span className="story-icon">
            <BookOpenCheck size={22} />
          </span>
          <div>
            <strong>오늘도 12개의 스터디가 진행 중이에요</strong>
            <p>스터디 메이트와 함께 목표에 한 걸음 더 가까워져 보세요.</p>
          </div>
        </div>
      </section>

      <section className="login-panel">
        <form className="login-form" onSubmit={handleSubmit}>
          <span className="eyebrow">WELCOME BACK</span>
          <h2>다시 만나서 반가워요</h2>
          <p className="form-intro">Study Mate 계정으로 계속하세요.</p>

          {location.state?.message && (
            <div className="form-success">{location.state.message}</div>
          )}
          {error && <div className="form-error">{error}</div>}

          <label className="field">
            <span>이메일</span>
            <div className="input-with-icon">
              <Mail size={18} />
              <input
                type="email"
                value={email}
                autoComplete="email"
                placeholder="you@example.com"
                required
                onChange={(event) => setEmail(event.target.value)}
              />
            </div>
          </label>

          <label className="field">
            <span>비밀번호</span>
            <div className="input-with-icon">
              <LockKeyhole size={18} />
              <input
                type="password"
                value={password}
                autoComplete="current-password"
                placeholder="비밀번호를 입력하세요"
                required
                onChange={(event) => setPassword(event.target.value)}
              />
            </div>
          </label>

          <button className="button primary login-submit" disabled={loading}>
            {loading ? "로그인 중..." : "로그인"}
            {!loading && <ArrowRight size={18} />}
          </button>
          <div className="auth-switch">
            <span>아직 계정이 없나요?</span>
            <Link className="auth-switch-link" to="/register">
              회원가입
            </Link>
          </div>
        </form>
      </section>
    </main>
  );
}
