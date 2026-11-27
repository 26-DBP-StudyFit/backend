package com.DBP.StudyFit.manager;

import java.sql.SQLException;

import com.DBP.StudyFit.dao.StudyProfileDAO;
import com.DBP.StudyFit.domain.StudyProfile;
import com.DBP.StudyFit.domain.StudyType;

/*
 * 학습 성향 설문 관련 비즈니스 로직
 * 담당 기능: 학습 성향 설문, 설문 재검사, 마이페이지 조회(설문 결과)
 */
public class StudyProfileManager {
    private static StudyProfileManager profileMan = new StudyProfileManager();
    private StudyProfileDAO profileDAO;

    private StudyProfileManager() {
        profileDAO = new StudyProfileDAO();
    }

    public static StudyProfileManager getInstance() {
        return profileMan;
    }

    /* =========================== 학습 성향 설문 =========================== */

    public StudyType createProfile(StudyProfile profile) throws SQLException {
        /* 첫 설문 결과 저장
         * - 이미 설문을 했으면 IllegalStateException (재검사로 안내)
         * - 미응답 항목이 있으면 IllegalArgumentException
         * 계산된 StudyType을 반환함
         */
        if (hasProfile(profile.getUserId())) {
            throw new IllegalStateException("이미 학습 성향 설문을 완료했습니다. 재검사를 이용해 주세요.");
        }

        StudyType type = new StudyType();
        type.calculateType(profile);		// 미응답 검사 + 유형 계산
        profile.setStudyType(type);

        if (profileDAO.create(profile) == 0) {
            throw new SQLException("설문 결과 저장 중 오류가 발생했습니다.");
        }
        return type;
    }

    /* =========================== 설문 재검사 =========================== */

    public StudyType retakeSurvey(int userId, String studyTime, String activityMode,
                                  String planningStyle, String studyIntensity,
                                  String communicationStyle, String studyMethod)
            throws SQLException {
        /* 새 응답으로 유형을 다시 계산해서 덮어씀
         * - 처음 설문을 아직 안 했으면 IllegalStateException
         * - 미응답 항목이 있으면 IllegalArgumentException
         * 새로 계산된 StudyType을 반환함
         */
        if (!hasProfile(userId)) {
            throw new IllegalStateException("아직 학습 성향 설문을 하지 않았습니다.");
        }

        StudyProfile profile = new StudyProfile();
        profile.setUserId(userId);
        profile.updateProfile(studyTime, activityMode, planningStyle,
                studyIntensity, communicationStyle, studyMethod);	// 값 갱신 + 유형 재계산

        if (profileDAO.update(profile) == 0) {
            throw new SQLException("재검사 결과 저장 중 오류가 발생했습니다.");
        }
        return profile.getStudyType();
    }

    /* =========================== 조회 =========================== */

    public StudyProfile findProfile(int userId) {
        /* 회원번호로 설문 결과 조회 (설문 결과 화면, 마이페이지)
         * 설문을 아직 하지 않았으면 null을 반환함
         */
        return profileDAO.findProfileByUserId(userId);
    }

    public boolean hasProfile(int userId) {
        /* 설문 완료 여부 (로그인 후 설문 화면으로 보낼지 판단할 때도 사용) */
        return findProfile(userId) != null;
    }
}
