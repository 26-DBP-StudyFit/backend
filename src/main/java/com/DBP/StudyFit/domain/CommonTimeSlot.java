package com.DBP.StudyFit.domain;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

// 계산 결과를 담는 값 객체이므로 DB에 저장하지 않고, 생성 후 값을 바꾸지 않는다.
public class CommonTimeSlot {

    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public CommonTimeSlot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (dayOfWeek == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("요일과 시간은 필수입니다.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 팀원 한 명의 활동 가능 시간(Availability)을 같은 구간의 CommonTimeSlot 으로 변환한다.
    public static CommonTimeSlot from(Availability availability) {
        return new CommonTimeSlot(availability.getDayOfWeek(),
                availability.getStartTime(), availability.getEndTime());
    }

    // 같은 요일이고 시간이 한 순간이라도 겹치는지 확인한다. 맞닿는 구간(9-10, 10-11)은 겹치지 않는다.
    public boolean overlaps(CommonTimeSlot other) {
        return dayOfWeek == other.dayOfWeek
                && startTime.isBefore(other.endTime)
                && other.startTime.isBefore(endTime);
    }

    // 두 구간의 교집합. 요일이 다르거나 겹치지 않으면 null.
    public CommonTimeSlot intersect(CommonTimeSlot other) {
        if (!overlaps(other)) {
            return null;
        }
        LocalTime start = startTime.isAfter(other.startTime) ? startTime : other.startTime;
        LocalTime end = endTime.isBefore(other.endTime) ? endTime : other.endTime;
        return new CommonTimeSlot(dayOfWeek, start, end);
    }

    // 같은 요일에서 겹치거나 맞닿아 하나로 합칠 수 있는지 확인한다.
    public boolean canMergeWith(CommonTimeSlot other) {
        return dayOfWeek == other.dayOfWeek
                && !startTime.isAfter(other.endTime)
                && !other.startTime.isAfter(endTime);
    }

    // 합칠 수 있는 두 구간을 하나로 합친다. 합칠 수 없으면 IllegalArgumentException.
    public CommonTimeSlot mergeWith(CommonTimeSlot other) {
        if (!canMergeWith(other)) {
            throw new IllegalArgumentException("겹치거나 맞닿은 같은 요일의 구간만 합칠 수 있습니다.");
        }
        LocalTime start = startTime.isBefore(other.startTime) ? startTime : other.startTime;
        LocalTime end = endTime.isAfter(other.endTime) ? endTime : other.endTime;
        return new CommonTimeSlot(dayOfWeek, start, end);
    }

    // 구간 길이(분)
    public long getDurationMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommonTimeSlot)) {
            return false;
        }
        CommonTimeSlot that = (CommonTimeSlot) o;
        return dayOfWeek == that.dayOfWeek
                && startTime.equals(that.startTime)
                && endTime.equals(that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime);
    }

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime + "~" + endTime;
    }
}