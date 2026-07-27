import { useEffect, useState } from "react";
import {
  ArrowLeft,
  ArrowRight,
  BookOpenCheck,
  LockKeyhole,
  Mail,
  Sparkles,
  UserRound,
} from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import { registerMember } from "../api/authApi";
import { getErrorMessage } from "../api/apiClient";
import { useAuth } from "../context/AuthContext";

const initialForm = {
  email: "",
  password: "",
  passwordConfirm: "",
  name: "",
  nickname: "",
};

export default function RegisterPage() {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/studies", { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (form.password !== form.passwordConfirm) {
      setError("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      return;
    }

    setLoading(true);

    try {
      const result = await registerMember({
        email: form.email.trim(),
        password: form.password,
        name: form.name.trim(),
        nickname: form.nickname.trim(),
      });

      if (result.result !== "success") {
        throw new Error("회원가입에 실패했습니다. 이미 사용 중인 이메일인지 확인해주세요.");
      }

      navigate("/login", {
        replace: true,
        state: { message: "회원가입이 완료되었습니다. 새 계정으로 로그인해주세요." },
      });
    } catch (requestError) {
      setError(getErrorMessage(requestError, requestError.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page register-page">
      <section className="login-story">
        <div className="login-brand">
          <span className="brand-mark large">
            <Sparkles size={24} fill="currentColor" />
          </span>
          <strong>Study Mate</strong>
        </div>
        <div className="story-copy">
          <span className="eyebrow">START GROWING TOGETHER</span>
          <h1>
            함께할수록
            <br />
            배움은 더 커지니까
          </h1>
          <p>
            나에게 꼭 맞는 스터디를 발견하고,
            <br />
            새로운 메이트들과 오늘부터 시작하세요.
          </p>
        </div>
        <div className="story-card">
          <span className="story-icon">
            <BookOpenCheck size={22} />
          </span>
          <div>
            <strong>한 계정으로 모든 스터디를 관리하세요</strong>
            <p>모집부터 참여, 스터디원 관리까지 한곳에서 이어집니다.</p>
          </div>
        </div>
      </section>

      <section className="login-panel">
        <form className="login-form register-form" onSubmit={handleSubmit}>
          <Link className="auth-back-link" to="/login">
            <ArrowLeft size={15} />
            로그인으로 돌아가기
          </Link>
          <span className="eyebrow">CREATE ACCOUNT</span>
          <h2>Study Mate 시작하기</h2>
          <p className="form-intro">스터디에서 사용할 정보를 입력해주세요.</p>

          {error && <div className="form-error">{error}</div>}

          <div className="register-field-grid">
            <label className="field">
              <span>이름</span>
              <div className="input-with-icon">
                <UserRound size={18} />
                <input
                  name="name"
                  value={form.name}
                  autoComplete="name"
                  placeholder="이름"
                  required
                  onChange={handleChange}
                />
              </div>
            </label>

            <label className="field">
              <span>닉네임</span>
              <div className="input-with-icon">
                <Sparkles size={18} />
                <input
                  name="nickname"
                  value={form.nickname}
                  autoComplete="nickname"
                  placeholder="스터디 닉네임"
                  required
                  onChange={handleChange}
                />
              </div>
            </label>
          </div>

          <label className="field">
            <span>이메일</span>
            <div className="input-with-icon">
              <Mail size={18} />
              <input
                type="email"
                name="email"
                value={form.email}
                autoComplete="email"
                placeholder="you@example.com"
                required
                onChange={handleChange}
              />
            </div>
          </label>

          <div className="register-field-grid">
            <label className="field">
              <span>비밀번호</span>
              <div className="input-with-icon">
                <LockKeyhole size={18} />
                <input
                  type="password"
                  name="password"
                  value={form.password}
                  autoComplete="new-password"
                  placeholder="비밀번호"
                  required
                  onChange={handleChange}
                />
              </div>
            </label>

            <label className="field">
              <span>비밀번호 확인</span>
              <div className="input-with-icon">
                <LockKeyhole size={18} />
                <input
                  type="password"
                  name="passwordConfirm"
                  value={form.passwordConfirm}
                  autoComplete="new-password"
                  placeholder="한 번 더 입력"
                  required
                  onChange={handleChange}
                />
              </div>
            </label>
          </div>

          <button className="button primary login-submit" disabled={loading}>
            {loading ? "가입 중..." : "회원가입"}
            {!loading && <ArrowRight size={18} />}
          </button>

          <div className="auth-switch">
            <span>이미 계정이 있나요?</span>
            <Link className="auth-switch-link" to="/login">
              로그인
            </Link>
          </div>
        </form>
      </section>
    </main>
  );
}
