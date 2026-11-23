package com.DBP.StudyFit;

import java.util.HashMap;
import java.util.Map;

/**
 * 최종 학습 유형
 *
 * 세 가지 축의 점수(0/1)를 조합해 typeCode(예: "111")를 만들고,
 * 코드에 해당하는 유형 이름/설명을 아래 TYPES 표에서 찾아 채움.
 *   - timeScore     : 아침형 1 / 밤형 0       (studyTime)
 *   - planningScore : 유연형 1 / 계획형 0     (planningStyle)
 *   - methodScore   : 독립형 1 / 함께형 0     (studyMethod)
 *   → "111" = 아침형·유연형·독립형 = 느긋한 토끼
 *
 * 유형 8개는 고정이라 DB 테이블/DAO 없이 코드에 정의함.
 * DB(study_profile.type_code)에는 typeCode 문자열만 저장하고,
 * 읽을 때는 fromCode()로 다시 객체를 만든다.
 */
public class StudyType {
    // 설문 선택지 코드 (DB 컬럼이 CHAR(1)이므로 한 글자)
    // survey.jsp의 <option value="...">와 같아야 함
    public static final String MORNING = "M", NIGHT = "N";      // study_time
    public static final String PLANNED = "P", FLEXIBLE = "F";   // planning_style
    public static final String TOGETHER = "T", ALONE = "A";     // study_method

    /** typeCode → {유형 이름, 설명} */
    private static final Map<String, String[]> TYPES = new HashMap<String, String[]>();
    static {
        //        코드    시간 계획 관계
        TYPES.put("100", new String[] { "부지런한 다람쥐", "아침에 계획을 세워 함께 공부하는 유형" });   // 아침 계획 함께
        TYPES.put("101", new String[] { "꼼꼼한 비버",     "아침에 계획대로 혼자 차근차근 공부하는 유형" }); // 아침 계획 독립
        TYPES.put("110", new String[] { "활기찬 수달",     "아침에 유연하게 함께 어울리며 공부하는 유형" }); // 아침 유연 함께
        TYPES.put("111", new String[] { "느긋한 토끼",     "아침에 자기 속도대로 혼자 공부하는 유형" });     // 아침 유연 독립
        TYPES.put("000", new String[] { "든든한 늑대",     "밤에 계획을 세워 함께 공부하는 유형" });         // 밤 계획 함께
        TYPES.put("001", new String[] { "집중하는 판다",   "밤에 계획대로 혼자 깊게 몰입하는 유형" });       // 밤 계획 독립
        TYPES.put("010", new String[] { "열정적인 부엉이", "밤에 유연하게 함께 열정적으로 공부하는 유형" }); // 밤 유연 함께
        TYPES.put("011", new String[] { "자유로운 고양이", "밤에 자유롭게 혼자 공부하는 유형" });           // 밤 유연 독립
    }

    private String typeCode;
    private String typeName;
    private String description;
    private int timeScore;
    private int planningScore;
    private int methodScore;

    public StudyType() { }

    /** 설문 응답으로 점수 계산 → 코드 생성 → 이름/설명 채움  */
    public void calculateType(StudyProfile p) {
        if (p == null || !p.isComplete()) {
            throw new IllegalArgumentException("설문에 응답하지 않은 항목이 있습니다.");
        }
        this.timeScore     = MORNING.equals(p.getStudyTime()) ? 1 : 0;
        this.planningScore = FLEXIBLE.equals(p.getPlanningStyle()) ? 1 : 0;
        this.methodScore   = ALONE.equals(p.getStudyMethod()) ? 1 : 0;

        this.typeCode = generateTypeCode();
        fillNameAndDescription();
    }

    /** 점수 세 개를 이어 붙여 코드 생성. 예) 1,1,1 → "111" */
    public String generateTypeCode() {
        return "" + timeScore + planningScore + methodScore;
    }

    /** DB에 저장된 코드("101" 등)로 객체 복원. 잘못된 코드면 null */
    public static StudyType fromCode(String code) {
        if (code == null || !TYPES.containsKey(code)) return null;
        StudyType t = new StudyType();
        t.timeScore     = code.charAt(0) - '0';
        t.planningScore = code.charAt(1) - '0';
        t.methodScore   = code.charAt(2) - '0';
        t.typeCode = code;
        t.fillNameAndDescription();
        return t;
    }

    private void fillNameAndDescription() {
        String[] info = TYPES.get(typeCode);
        this.typeName = info[0];
        this.description = info[1];
    }

    public String getTypeCode() { return typeCode; }
    public String getTypeName() { return typeName; }
    public String getDescription() { return description; }
    public int getTimeScore() { return timeScore; }
    public int getPlanningScore() { return planningScore; }
    public int getMethodScore() { return methodScore; }

    @Override
    public String toString() {
        return "StudyType [typeCode=" + typeCode + ", typeName=" + typeName + "]";
    }
}
