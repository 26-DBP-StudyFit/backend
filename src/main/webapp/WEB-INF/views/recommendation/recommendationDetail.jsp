<%--
  11 추천 근거 확인 (UC-C-03)   GET /recommendations/{recommendationId}/matches/{matchResultId}
                                →  RecommendationDetailController#showRecommendationDetail
  모델 속성: matchResult:MatchResult (team, totalScore, summary),
             criterionScores:List<CriterionScore> (criterion, score(0~100 가정), weight, reason),
             recommendationId:Long (목록으로 돌아가기용, 추가 속성), rank:int (선택, '추천 1위'), studyType (선택)
  ※ 항목별 '나 밤형 · 팀 밤형 위주' 같은 문구는 CriterionScore.reason 에 서버가 만들어 담는다.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="추천 근거 확인"/>
  <jsp:param name="active" value="match"/>
</jsp:include>

<fmt:formatNumber var="total" value="${matchResult.totalScore}" maxFractionDigits="0"/>

<div class="page-head">
  <a class="back" href="javascript:history.back()"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>추천 근거 확인</h1>
  <c:if test="${not empty studyType}"><span class="chip">My Study Type <c:out value="${studyType.typeCode}"/></span></c:if>
</div>

<section class="card row-card" style="gap:20px">
  <div class="thumb">대표 사진</div>
  <div class="col grow" style="gap:6px;flex:1 1 240px">
    <div class="row" style="gap:8px">
      <b style="font-size:18px"><c:out value="${matchResult.team.title}"/></b>
      <c:if test="${not empty rank}"><span class="chip">추천 ${rank}위</span></c:if>
    </div>
    <div class="sub-text">
      <c:out value="${catKo[matchResult.team.studyCategory.name()]}" default="${matchResult.team.studyCategory}"/> ·
      <c:forEach var="d" items="${matchResult.team.preferredDays}">${dayKo[d.name()]}</c:forEach>
      ${fn:substring(matchResult.team.preferredStartTime, 0, 5)}~${fn:substring(matchResult.team.preferredEndTime, 0, 5)} ·
      <c:out value="${regionKo[matchResult.team.regionCode]}" default="${matchResult.team.regionCode}"/>
    </div>
  </div>
  <div class="col" style="gap:2px;align-items:flex-end">
    <span class="sub-text">전체 FiT 일치도</span>
    <span class="score">${total}%</span>
  </div>
</section>

<section class="card">
  <div class="card-title">항목별 근거</div>
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
  <a class="btn outline" href="${ctx}/recommendations/teams/form">추천 목록으로</a>
  <div class="btn-row">
    <form class="inline" method="post" action="${ctx}/teams/${matchResult.team.teamId}/bookmarks"><button type="submit" class="btn outline">찜 신청</button></form>
    <a class="btn" href="${ctx}/teams/${matchResult.team.teamId}">팀 상세 보기</a>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
