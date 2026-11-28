<%--
  모든 JSP 상단에서 static include 로 가져오는 공통 선언.
    <%@ include file="/WEB-INF/views/common/taglibs.jsp" %>

  ※ Spring Boot 3 / Tomcat 10 (jakarta) 환경이면 아래 uri 를
     jakarta.tags.core / jakarta.tags.functions / jakarta.tags.fmt 로 바꾸세요. (이 파일 한 곳만 수정)
  ※ EL 3.0(Tomcat 8+) 문법 사용: 맵 리터럴, 메소드 호출(x.toString()).
  ※ enum 비교는 x.toString() eq 'VALUE' 로 통일 (상수명이 달라도 예외 없이 false 처리).
  ※ 아래 *Ko 맵은 enum 상수명 → 화면 문구. 사용 예:
       <c:out value="${ansKo[p.studyTime.name()]}" default="${p.studyTime}"/>
     실제 enum 상수명과 다르면 default 로 상수명이 그대로 출력되니, 상수명에 맞게 키를 고치면 된다.
--%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="dayKo" value="${ {'MONDAY':'월','TUESDAY':'화','WEDNESDAY':'수','THURSDAY':'목','FRIDAY':'금','SATURDAY':'토','SUNDAY':'일'} }"/>
<c:set var="ansKo" value="${ {'MORNING':'아침형','NIGHT':'밤형','TOGETHER':'함께','INDEPENDENT':'독립','PLANNED':'계획형','FLEXIBLE':'유연형','FREQUENT':'자주','OCCASIONAL':'가끔','HIGH':'높음','LOW':'낮음','OFFLINE':'대면','ONLINE':'비대면','HYBRID':'혼합형'} }"/>
<c:set var="catKo" value="${ {'CERTIFICATE':'자격증','JOB':'취업 준비','CONTEST':'대회 준비','MAJOR':'전공 공부','LANGUAGE':'어학','CIVIL_SERVICE':'공무원 시험','ENTRANCE':'입시','READING':'독서','STUDY':'스터디','HOBBY':'취미','EXERCISE':'운동','ETC':'기타'} }"/>
<c:set var="regionKo" value="${ {'SEOUL':'서울','BUSAN':'부산','DAEJEON':'대전','DAEGU':'대구','INCHEON':'인천','GWANGJU':'광주','ETC':'기타'} }"/>
<c:set var="schStatusKo" value="${ {'UPCOMING':'예정','IN_PROGRESS':'진행 중','ENDED':'종료'} }"/>
<c:set var="schTypeKo" value="${ {'REGULAR':'정기','MAKEUP':'보강','SPECIAL':'특강·행사','ETC':'기타'} }"/>
<c:set var="attKo" value="${ {'PRESENT':'출석','LATE':'지각','ABSENT':'결석'} }"/>
<c:set var="appStatusKo" value="${ {'PENDING':'대기 중','APPROVED':'승인','REJECTED':'거절'} }"/>
<%-- 추천 항목(MatchCriterion)·균형 항목(BalanceCriterion) 한글명. enum 상수명은 예시 — 실제 상수명에 맞게 수정 --%>
<c:set var="critKo" value="${ {'STUDY_TIME':'선호 학습 시간','STUDY_METHOD':'학습 방식','PLANNING_STYLE':'계획 성향','COMMUNICATION_STYLE':'소통 빈도','STUDY_INTENSITY':'학습 강도','ACTIVITY_MODE':'활동 방식','ACTIVITY_METRICS':'팀 활동 지표','ACTIVITY':'팀 활동 지표'} }"/>
