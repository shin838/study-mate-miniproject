import { createContext, useCallback, useContext, useState } from "react";
import { getMyParticipatingStudies } from "../api/myPageApi";
import { useAuth } from "./AuthContext";

const StudyContext = createContext(null);

export function StudyProvider({ children }) {
  const { isAuthenticated } = useAuth();
  const [participatingStudies, setParticipatingStudies] = useState([]);
  const [sidebarLoading, setSidebarLoading] = useState(false);
  const [sidebarError, setSidebarError] = useState("");

  const refreshParticipatingStudies = useCallback(async () => {
    if (!isAuthenticated) {
      setParticipatingStudies([]);
      return;
    }

    setSidebarLoading(true);
    setSidebarError("");
    try {
      const data = await getMyParticipatingStudies();
      setParticipatingStudies(Array.isArray(data) ? data : []);
    } catch {
      setSidebarError("참여 스터디를 불러오지 못했습니다.");
    } finally {
      setSidebarLoading(false);
    }
  }, [isAuthenticated]);

  return (
    <StudyContext.Provider
      value={{
        participatingStudies,
        sidebarLoading,
        sidebarError,
        refreshParticipatingStudies,
      }}
    >
      {children}
    </StudyContext.Provider>
  );
}

export function useStudies() {
  return useContext(StudyContext);
}
