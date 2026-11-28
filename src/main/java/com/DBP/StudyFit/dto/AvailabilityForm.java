package com.DBP.StudyFit.dto;

import java.util.ArrayList;
import java.util.List;

/** 활동 가능 시간 편집 폼. 사용자 ID 는 받지 않는다 (세션의 로그인 사용자만 사용). */
public class AvailabilityForm {
    private Integer teamId;
    private List<AvailabilityInput> slots = new ArrayList<>();

    public AvailabilityForm() {
    }

    public AvailabilityForm(Integer teamId, List<AvailabilityInput> slots) {
        this.teamId = teamId;
        this.slots = slots;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public List<AvailabilityInput> getSlots() {
        return slots;
    }

    public void setSlots(List<AvailabilityInput> slots) {
        this.slots = slots;
    }
}
