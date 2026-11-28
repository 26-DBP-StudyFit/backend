<%--
  17 팀 구성 균형 분석 — 입력 (UC-C-05, 팀장 전용)   GET /leader/teams/{teamId}/balance/form  →  TeamBalanceController#showTeamBalanceForm
  분석 실행                                           POST /leader/teams/{teamId}/balance (candidateId 선택)  →  teamBalanceResult.jsp
  모델 속성: team:Team, members:List<TeamMember> (user.name, user.studyType, role),
             candidates:List<User> (가입 신청 후보; name, studyType), candidateId (선택, ?candidateId= 로 들어오면 미리 선택)
  후보를 고르지 않으면 candidateId 를 보내지 않고 현재 팀만 분석 (value="" 라디오는 전송은 되지만 서버에서 빈 값 무시).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀 구성 균형 분석"/>
</jsp:include>

<c:set var="selected" value="${empty candidateId ? param.candidateId : candidateId}"/>

<div class="page-head">
  <a class="back" href="${ctx}/teams/${team.teamId}/home"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>팀 구성 균형 분석</h1>
  <span class="chip"><c:out value="${team.title}"/></span>
  <span class="chip soft">팀장 전용</span>
</div>

<form method="post" action="${ctx}/leader/teams/${team.teamId}/balance">
  <div class="split">
    <section class="card" style="flex:0 1 340px">
      <div class="card-title">현재 팀원 <span class="sub-text" style="font-weight:400">${fn:length(members)}명</span></div>
      <c:forEach var="m" items="${members}">
        <div class="row" style="gap:10px;flex-wrap:nowrap">
          <div class="avatar" aria-hidden="true"></div>
          <div class="col" style="gap:0;flex:1">
            <span><c:out value="${m.user.name}"/></span>
            <span class="sub-text" style="font-size:13px"><c:out value="${m.user.studyType.typeName}"/> (<c:out value="${m.user.studyType.typeCode}"/>)</span>
          </div>
          <c:if test="${m.role.toString() eq 'LEADER'}"><span class="chip">팀장</span></c:if>
        </div>
      </c:forEach>
    </section>

    <section class="card grow" style="gap:16px">
      <div class="col" style="gap:4px">
        <div class="card-title">후보 선택 (선택 사항)</div>
        <div class="sub-text">후보를 고르면 합류했을 때의 균형도 함께 분석해요. 고르지 않으면 현재 팀만 분석해요.</div>
      </div>
      <div class="col" style="gap:8px">
        <label class="choice">
          <input type="radio" name="candidateId" value="" ${empty selected ? 'checked' : ''}>
          <span class="box"><span class="radio"></span><span><b>후보 없이 현재 팀만 분석</b><small>candidateId 없음</small></span></span>
        </label>
        <c:forEach var="u" items="${candidates}">
          <label class="choice">
            <input type="radio" name="candidateId" value="${u.userId}" ${selected eq u.userId.toString() ? 'checked' : ''}>
            <span class="box"><span class="radio"></span><span><b><c:out value="${u.name}"/></b><small><c:out value="${u.studyType.typeName}"/> (<c:out value="${u.studyType.typeCode}"/>) · 가입 신청 후보</small></span></span>
          </label>
        </c:forEach>
      </div>
      <div class="btn-row end"><button type="submit" class="btn">분석하기</button></div>
    </section>
  </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
