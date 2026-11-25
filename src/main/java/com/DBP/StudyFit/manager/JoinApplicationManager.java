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
    public int createApplication(JoinApplication joinApplication) {
        if (joinApplication == null || joinApplication.getUserId() < 0 || joinApplication.getTeamId() < 0) {
            return -1;
        }

        if (joinApplicationDAO.existingPendingApplication(joinApplication.getTeamId(), joinApplication.getUserId())) {
            return -2;
        }
        return joinApplicationDAO.create(joinApplication);
    }
}
