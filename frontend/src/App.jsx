import { Navigate, Route, Routes } from "react-router-dom";
import MainLayout from "./layouts/MainLayout";
import ProtectedRoute from "./components/common/ProtectedRoute";
import AdminRoute from "./components/common/AdminRoute";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import StudyBoardPage from "./pages/StudyBoardPage";
import StudyDetailPage from "./pages/StudyDetailPage";
import StudyFormPage from "./pages/StudyFormPage";
import MyStudyDetailPage from "./pages/MyStudyDetailPage";
import MyCreatedStudiesPage from "./pages/MyCreatedStudiesPage";
import NotFoundPage from "./pages/NotFoundPage";
import AdminDashboardPage from "./pages/AdminDashboardPage";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route index element={<Navigate to="/studies" replace />} />
          <Route path="/studies" element={<StudyBoardPage />} />
          <Route path="/studies/new" element={<StudyFormPage />} />
          <Route path="/studies/:studyId" element={<StudyDetailPage />} />
          <Route path="/studies/:studyId/edit" element={<StudyFormPage />} />
          <Route path="/my/studies" element={<MyCreatedStudiesPage />} />
          <Route
            path="/my/applications/:studyId"
            element={<MyStudyDetailPage />}
          />
          <Route element={<AdminRoute />}>
            <Route path="/admin" element={<AdminDashboardPage />} />
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Route>
    </Routes>
  );
}
