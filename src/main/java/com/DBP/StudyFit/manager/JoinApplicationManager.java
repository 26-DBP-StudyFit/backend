package com.DBP.StudyFit.manager;

import com.DBP.StudyFit.dao.JoinApplicationDAO;
import com.DBP.StudyFit.domain.JoinApplication;
import org.springframework.stereotype.Service;

@Service
public class JoinApplicationManager {

    private final JoinApplicationDAO joinApplicationDAO;

    public JoinApplicationManager(JoinApplicationDAO joinApplicationDAO) {
        this.joinApplicationDAO = joinApplicationDAO;
    }

    // 팀 가입 신청
    // Manager
    public void createApplication(int teamId, int userId) {
        if (joinApplicationDAO.existingPendingApplication(teamId, userId))
            throw new IllegalStateException("이미 가입 신청한 팀입니다");

        JoinApplication application = new JoinApplication();
        application.setTeamId(teamId);
        application.setUserId(userId);

        if (joinApplicationDAO.create(application) < 0)
            throw new RuntimeException("가입 신청 실패");
    }
}
