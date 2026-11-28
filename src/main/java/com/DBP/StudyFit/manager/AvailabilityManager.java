package com.DBP.StudyFit.manager;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.DBP.StudyFit.dao.AvailabilityDAO;
import com.DBP.StudyFit.dao.TeamMemberDAO;
import com.DBP.StudyFit.domain.Availability;
import com.DBP.StudyFit.domain.CommonAvailabilityStatus;
import com.DBP.StudyFit.domain.CommonTimeSlot;
import com.DBP.StudyFit.dto.AvailabilityForm;
import com.DBP.StudyFit.dto.AvailabilityInput;
import com.DBP.StudyFit.dto.CommonAvailabilityResult;

/**
 * 활동 가능 시간 조회·저장 (FR-D-01), 팀 공통 가능 시간 계산 (FR-D-02).
 *
 * 입력 오류 키 규칙 (View 가 이 키로 오류를 찾는다):
 *   slots                 : 목록 전체 오류 (개수 초과)
 *   slots[i].dayOfWeek    : i번째 줄 요일 오류
 *   slots[i].startTime    : i번째 줄 시작 시간 오류
 *   slots[i].endTime      : i번째 줄 종료 시간 오류
 *   slots[i].time         : i번째 줄 시간 관계 오류 (시작 >= 종료, 같은 요일 겹침)
 * i 는 제출된 원래 줄 번호(0부터)이며, 빈 줄을 건너뛰어도 바뀌지 않는다.
 */
@Service
public class AvailabilityManager {
    static final int MAX_SLOTS = 50;

    private static final DateTimeFormatter INPUT_TIME =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_TIME = DateTimeFormatter.ofPattern("HH:mm");

    static final String MSG_NO_COMMON_TIME = "공통 가능 시간이 없습니다.";

    private final AvailabilityDAO availabilityDAO;
    private final TeamMemberDAO teamMemberDAO;

    public AvailabilityManager(AvailabilityDAO availabilityDAO, TeamMemberDAO teamMemberDAO) {
        this.availabilityDAO = availabilityDAO;
        this.teamMemberDAO = teamMemberDAO;
    }

    /**
     * 기존 등록값을 폼으로 변환한다. 등록 내역이 없으면 slots 가 빈 폼을 돌려준다.
     *
     * @throws IllegalStateException DAO 가 DB 오류로 null 을 돌려준 경우
     */
    public AvailabilityForm getAvailability(long teamId, long userId) {
        List<Availability> saved = availabilityDAO.findByTeamAndUser(teamId, userId);
        if (saved == null) {
            throw new IllegalStateException("활동 가능 시간 조회에 실패했습니다.");
        }

        List<Availability> sorted = new ArrayList<>(saved);
        sorted.sort(Comparator.comparing(Availability::getDayOfWeek)
                .thenComparing(Availability::getStartTime));

        List<AvailabilityInput> slots = new ArrayList<>();
        for (Availability a : sorted) {
            slots.add(new AvailabilityInput(
                    a.getDayOfWeek().name(),
                    a.getStartTime().format(OUTPUT_TIME),
                    a.getEndTime().format(OUTPUT_TIME)));
        }
        return new AvailabilityForm(teamId, slots);
    }

    /**
     * 입력값을 검증하고, 통과하면 기존 등록분을 새 목록으로 교체한다.
     * 삭제와 삽입의 트랜잭션(commit/rollback)은 DAO 가 처리한다.
     *
     * @return 입력 오류 맵. 비어 있으면 저장 성공. 오류가 있으면 DAO 를 호출하지 않았다.
     * @throws IllegalStateException DAO 가 저장 실패(false)를 돌려준 경우 (이미 롤백된 상태)
     */
    public Map<String, String> saveAvailability(long teamId, long userId, List<AvailabilityInput> inputs) {
        Map<String, String> errors = new LinkedHashMap<>();
        List<Availability> slots = validateAndConvert(inputs, errors);
        if (!errors.isEmpty()) {
            return errors;
        }

        if (!availabilityDAO.replaceByTeamAndUser(teamId, userId, slots)) {
            throw new IllegalStateException("활동 가능 시간 저장에 실패했습니다.");
        }
        return errors;
    }

    // ---------------------------------------------------------------- 공통 가능 시간 (FR-D-02)

    /**
     * 팀 전원의 활동 가능 시간의 교집합을 요일별로 계산한다.
     * 팀원 여부 확인(TeamManager.validateMember)은 호출하는 Controller 에서 한다.
     *
     * AVAILABLE      : 전원이 등록했고 교집합이 있다. commonTimeSlots 에 (요일, 시작 순) 교집합.
     * NO_COMMON_TIME : 전원이 등록했지만 교집합이 없다. 두 목록 모두 빈 목록.
     * INCOMPLETE     : 가능 시간을 등록하지 않은 팀원이 있다. 공통 시간은 확정하지 않고
     *                  commonTimeSlots 는 빈 목록, missingMemberIds 에 미등록 팀원.
     * 맞닿는 구간(9-10, 10-11)은 하나로 합쳐서 돌려준다.
     *
     * @throws IllegalStateException DAO 가 DB 오류로 null 을 돌려준 경우
     */
    public CommonAvailabilityResult calculateCommonAvailability(long teamId) {
        List<Long> memberIds = teamMemberDAO.findMemberIdsByTeamId(teamId);
        if (memberIds == null) {
            throw new IllegalStateException("팀원 조회에 실패했습니다.");
        }
        List<Availability> saved = availabilityDAO.findByTeam(teamId);
        if (saved == null) {
            throw new IllegalStateException("활동 가능 시간 조회에 실패했습니다.");
        }

        // 팀원별 가능 시간 (등록하지 않은 팀원은 키가 없다)
        Map<Long, List<CommonTimeSlot>> slotsByMember = new LinkedHashMap<>();
        for (Availability a : saved) {
            slotsByMember
                    .computeIfAbsent(a.getTeamMember().getMemberId(), k -> new ArrayList<>())
                    .add(CommonTimeSlot.from(a));
        }

        List<Long> missing = new ArrayList<>();
        for (Long memberId : memberIds) {
            if (!slotsByMember.containsKey(memberId)) {
                missing.add(memberId);
            }
        }
        if (!missing.isEmpty()) {
            return new CommonAvailabilityResult(teamId, new ArrayList<>(), missing,
                    CommonAvailabilityStatus.INCOMPLETE,
                    "가능 시간을 등록하지 않은 팀원이 있어 공통 가능 시간을 확정할 수 없습니다. (미등록 "
                            + missing.size() + "명)");
        }

        // 팀원 한 명씩 교집합을 좁혀 간다. 도중에 비면 더 계산할 필요가 없다.
        List<CommonTimeSlot> common = null;
        for (Long memberId : memberIds) {
            List<CommonTimeSlot> mine = mergeAdjacent(slotsByMember.get(memberId));
            common = (common == null) ? mine : intersect(common, mine);
            if (common.isEmpty()) {
                break;
            }
        }

        if (common == null || common.isEmpty()) {   // common == null: 팀원이 한 명도 없는 경우
            return new CommonAvailabilityResult(teamId, new ArrayList<>(), new ArrayList<>(),
                    CommonAvailabilityStatus.NO_COMMON_TIME, MSG_NO_COMMON_TIME);
        }
        return new CommonAvailabilityResult(teamId, common, new ArrayList<>(),
                CommonAvailabilityStatus.AVAILABLE, null);
    }

    /** 두 목록의 모든 구간 쌍에서 교집합을 모아 (요일, 시작 순)으로 정리한다. */
    private static List<CommonTimeSlot> intersect(List<CommonTimeSlot> a, List<CommonTimeSlot> b) {
        List<CommonTimeSlot> result = new ArrayList<>();
        for (CommonTimeSlot x : a) {
            for (CommonTimeSlot y : b) {
                CommonTimeSlot overlap = x.intersect(y);
                if (overlap != null) {
                    result.add(overlap);
                }
            }
        }
        return mergeAdjacent(result);
    }

    /** (요일, 시작 순)으로 정렬하고, 같은 요일에서 겹치거나 맞닿은 구간은 하나로 합친다. */
    private static List<CommonTimeSlot> mergeAdjacent(List<CommonTimeSlot> slots) {
        List<CommonTimeSlot> sorted = new ArrayList<>(slots);
        sorted.sort(Comparator.comparing(CommonTimeSlot::getDayOfWeek)
                .thenComparing(CommonTimeSlot::getStartTime));

        List<CommonTimeSlot> merged = new ArrayList<>();
        for (CommonTimeSlot slot : sorted) {
            int last = merged.size() - 1;
            if (last >= 0 && merged.get(last).canMergeWith(slot)) {
                merged.set(last, merged.get(last).mergeWith(slot));
            } else {
                merged.add(slot);
            }
        }
        return merged;
    }

    // ---------------------------------------------------------------- 검증

    /** 줄별 오류는 errors 에 담고, 오류가 없는 줄만 변환해서 (요일, 시작 순) 정렬해 돌려준다. */
    private List<Availability> validateAndConvert(List<AvailabilityInput> inputs, Map<String, String> errors) {
        List<ParsedSlot> parsed = new ArrayList<>();
        int filled = 0;

        if (inputs != null) {
            for (int i = 0; i < inputs.size(); i++) {
                AvailabilityInput input = inputs.get(i);
                if (input == null || input.isBlank()) {
                    continue; // 빈 줄은 무시
                }
                filled++;

                DayOfWeek day = parseDay(input.getDayOfWeek(), i, errors);
                LocalTime start = parseTime(input.getStartTime(), "시작", "slots[" + i + "].startTime", errors);
                LocalTime end = parseTime(input.getEndTime(), "종료", "slots[" + i + "].endTime", errors);

                if (day != null && start != null && end != null) {
                    if (start.isBefore(end)) {
                        parsed.add(new ParsedSlot(i, day, start, end));
                    } else {
                        errors.put("slots[" + i + "].time", "시작 시간은 종료 시간보다 빨라야 합니다.");
                    }
                }
            }
        }

        if (filled > MAX_SLOTS) {
            errors.put("slots", "활동 가능 시간은 최대 " + MAX_SLOTS + "개까지 등록할 수 있습니다.");
        }

        sortSlots(parsed);
        checkOverlap(parsed, errors);

        List<Availability> result = new ArrayList<>();
        for (ParsedSlot p : parsed) {
            // id, teamMember 는 DAO 가 teamId/userId 로 처리하므로 null
            result.add(new Availability(null, null, p.day, p.start, p.end));
        }
        return result;
    }

    private static void sortSlots(List<ParsedSlot> parsed) {
        parsed.sort(Comparator.<ParsedSlot, DayOfWeek>comparing(p -> p.day)
                .thenComparing(p -> p.start)
                .thenComparingInt(p -> p.index));
    }

    /** 같은 요일에서 앞 구간과 겹치는 줄에 오류를 붙인다. 맞닿는 구간(9-10, 10-11)은 허용. */
    private void checkOverlap(List<ParsedSlot> sorted, Map<String, String> errors) {
        DayOfWeek currentDay = null;
        LocalTime latestEnd = null;
        for (ParsedSlot p : sorted) {
            if (p.day != currentDay) {
                currentDay = p.day;
                latestEnd = p.end;
                continue;
            }
            if (p.start.isBefore(latestEnd) && !errors.containsKey("slots[" + p.index + "].time")) {
                errors.put("slots[" + p.index + "].time", "같은 요일의 다른 시간대와 겹칩니다.");
            }
            if (p.end.isAfter(latestEnd)) {
                latestEnd = p.end;
            }
        }
    }

    private DayOfWeek parseDay(String value, int index, Map<String, String> errors) {
        String key = "slots[" + index + "].dayOfWeek";
        if (value == null || value.trim().isEmpty()) {
            errors.put(key, "요일을 선택해 주세요.");
            return null;
        }
        try {
            return DayOfWeek.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            errors.put(key, "올바른 요일이 아닙니다.");
            return null;
        }
    }

    private LocalTime parseTime(String value, String label, String key, Map<String, String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.put(key, label + " 시간을 입력해 주세요.");
            return null;
        }
        try {
            return LocalTime.parse(value.trim(), INPUT_TIME);
        } catch (DateTimeParseException e) {
            errors.put(key, label + " 시간은 HH:mm 형식(예: 09:30)이어야 합니다.");
            return null;
        }
    }

    /** 검증을 통과한 한 줄 (원래 줄 번호 포함) */
    private static final class ParsedSlot {
        final int index;
        final DayOfWeek day;
        final LocalTime start;
        final LocalTime end;

        ParsedSlot(int index, DayOfWeek day, LocalTime start, LocalTime end) {
            this.index = index;
            this.day = day;
            this.start = start;
            this.end = end;
        }
    }
}