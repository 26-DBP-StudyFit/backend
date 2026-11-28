<%--
  (디자인에 없는 중간 화면) 맞춤형 팀 추천 시작 (UC-C-01/02)
    GET  /recommendations/teams/form  →  TeamRecommendationController#showRecommendationForm
    POST /recommendations/teams       →  #recommendTeams   성공: recommendationList.jsp forward
  전송 파라미터: limit(선택, 기본 5), activityHistoryApplied(선택)
  모델 속성: user:User, studyType:StudyType  (검사 전이면 studyType 이 비어 있음 → 검사 유도)
  ※ 디자인의 '나의 핏 찾기' 는 결과 목록이라, 상단 메뉴에서 바로 들어오면 이 화면을 거친다.
    조건 입력이 필요 없다면 이 JSP 를 건너뛰고 GET 에서 바로 추천을 실행해도 된다.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="나의 fit 찾기"/>
  <jsp:param name="active" value="match"/>
  <jsp:param name="size" value="narrow"/>
</jsp:include>

<form class="form-card" method="post" action="${ctx}/recommendations/teams">
  <div class="col" style="gap:4px">
    <h1>나의 FiT 찾기</h1>
    <div class="sub-text">나의 스터디 타입과 가장 잘 맞는 팀을 점수 순으로 보여드려요.</div>
  </div>

  <div class="row between card" style="background:var(--bg)">
    <div class="col" style="gap:2px">
      <span class="sub-text">My Study Type</span>
      <c:choose>
        <c:when test="${empty studyType}"><b>아직 검사하지 않았어요</b></c:when>
        <c:otherwise><b><c:out value="${studyType.typeName}"/> (<c:out value="${studyType.typeCode}"/>)</b></c:otherwise>
      </c:choose>
    </div>
    <a class="btn outline sm" href="${ctx}/profile/${empty studyType ? 'survey' : 'survey/retake'}">${empty studyType ? '검사하기' : '다시 검사하기'}</a>
  </div>

  <div class="field">
    <label for="limit">추천 개수</label>
    <select id="limit" name="limit">
      <option value="3">3개</option>
      <option value="5" selected>5개</option>
      <option value="10">10개</option>
    </select>
  </div>
  <label class="check"><input type="checkbox" name="activityHistoryApplied" value="true" checked>기존 활동 기록(출석률·목표 달성률)도 반영하기</label>

  <div class="btn-row end">
    <button type="submit" class="btn" ${empty studyType ? 'disabled' : ''}>추천 받기</button>
  </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
