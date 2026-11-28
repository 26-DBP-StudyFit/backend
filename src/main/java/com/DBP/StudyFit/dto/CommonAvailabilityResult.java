package com.DBP.StudyFit.dto;

import com.DBP.StudyFit.domain.CommonAvailabilityStatus;
import com.DBP.StudyFit.domain.CommonTimeSlot;

import java.util.ArrayList;
import java.util.List;

 // AVAILABLE      : commonTimeSlots 에 교집합, missingMemberIds 는 빈 목록
 // NO_COMMON_TIME : commonTimeSlots, missingMemberIds 모두 빈 목록
 // INCOMPLETE     : commonTimeSlots 는 빈 목록, missingMemberIds 에 미등록 팀원
public class CommonAvailabilityResult {
    private final Long teamId;
    private final List<CommonTimeSlot> commonTimeSlots;
    private final List<Long> missingMemberIds;
    private final CommonAvailabilityStatus calculationStatus;
    private final String message;

    public CommonAvailabilityResult(Long teamId, List<CommonTimeSlot> commonTimeSlots,
                                    List<Long> missingMemberIds,
                                    CommonAvailabilityStatus calculationStatus, String message) {
        this.teamId = teamId;
        this.commonTimeSlots = commonTimeSlots == null ? new ArrayList<>() : commonTimeSlots;
        this.missingMemberIds = missingMemberIds == null ? new ArrayList<>() : missingMemberIds;
        this.calculationStatus = calculationStatus;
        this.message = message;
    }

    public Long getTeamId() {
        return teamId;
    }

    public List<CommonTimeSlot> getCommonTimeSlots() {
        return commonTimeSlots;
    }

    public List<Long> getMissingMemberIds() {
        return missingMemberIds;
    }

    public CommonAvailabilityStatus getCalculationStatus() {
        return calculationStatus;
    }

    // 안내 메시지 (AVAILABLE 이면 null 일 수 있다)
    public String getMessage() {
        return message;
    }
}