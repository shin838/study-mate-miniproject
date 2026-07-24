# Study Mate Frontend

## 실행

백엔드(Spring Boot)를 `localhost:8080`에서 먼저 실행합니다.

```bash
cd frontend
npm install
npm run dev
```

브라우저에서 `http://localhost:5173`으로 접속합니다.

## 빌드

```bash
npm run build
```

개발 환경에서는 Vite가 `/api` 요청을 `localhost:8080`으로 전달합니다.
운영 빌드는 Spring Boot와 같은 origin에서 제공하는 구성을 기준으로 합니다.
