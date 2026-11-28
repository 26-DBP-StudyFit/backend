<%--
  12 팀원 후보 적합도 (UC-C-04, 팀장 전용)   GET /leader/teams/{teamId}/candidates/{candidateId}/match
                                             →  CandidateMatchController#showCandidateMatch
  모델 속성: candidate:User (name, studyType 포함 StudyProfile 가정), team:Team,
             matchResult:MatchResult (totalScore, summary),
             criterionScores:List<CriterionScore> (criterion, score, reason)
  링크: 신청 상세로 돌아가기(applicationId 는 추가 모델 속성, 없으면 알림함), 팀 균형 분석하기 GET /leader/teams/{teamId}/balance/form?candidateId=
  권한(팀장)은 Manager 에서 검증하므로 화면은 '팀장 전용' 표시만 한다.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀원 후보 적합도"/>
</jsp:include>

<c:set var="backUrl" value="${ctx}/my/applications"/>
<c:if test="${not empty applicationId}"><c:set var="backUrl" value="${ctx}/teams/${team.teamId}/applications/${applicationId}"/></c:if>
<fmt:formatNumber var="total" value="${matchResult.totalScore}" maxFractionDigits="0"/>

<div class="page-head">
  <a class="back" href="${backUrl}"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>팀원 후보 적합도</h1>
  <span class="chip">팀장 전용</span>
</div>

<section class="card row-card" style="gap:20px">
  <div class="avatar lg" aria-hidden="true"></div>
  <div class="col grow" style="gap:4px;flex:1 1 240px">
    <b style="font-size:18px"><c:out value="${candidate.name}"/></b>
    <div class="sub-text">
      <c:out value="${candidate.studyType.typeName}"/> (<c:out value="${candidate.studyType.typeCode}"/>) · <c:out value="${team.title}"/> 팀 가입 후보
    </div>
  </div>
  <div class="col" style="gap:2px;align-items:flex-end">
    <span class="sub-text">전체 FiT 일치도</span>
    <span class="score">${total}%</span>
  </div>
</section>

<section class="card">
  <div class="card-title">항목별 적합도</div>
  <c:if test="${not empty matchResult.summary}"><div class="sub-text"><c:out value="${matchResult.summary}"/></div></c:if>
  <div>
    <c:forEach var="c" items="${criterionScores}">
      <fmt:formatNumber var="pct" value="${c.score}" maxFractionDigits="0"/>
      <div class="crit">
        <div class="name"><c:out value="${critKo[c.criterion.name()]}" default="${c.criterion}"/></div>
        <div>
          <div class="bar"><i style="width:${pct > 100 ? 100 : pct}%"></i></div>
          <div class="note"><c:out value="${c.reason}"/></div>
        </div>
        <div class="pct">${pct}%</div>
      </div>
    </c:forEach>
  </div>
</section>

<div class="row between">
  <a class="btn outline" href="${backUrl}">신청 상세로</a>
  <a class="btn" href="${ctx}/leader/teams/${team.teamId}/balance/form?candidateId=${candidate.userId}">팀 균형 분석하기</a>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
