<%--
  10 나의 핏 찾기 — 추천 결과 (UC-C-02)   POST /recommendations/teams 성공 시 forward
  모델 속성: recommendation:Recommendation (id, matchResults:List<MatchResult> — 총점 내림차순),
             studyType:StudyType (추가 속성, 상단 'My Study Type')
    MatchResult: id, team:Team, totalScore:double(0~100 가정), summary
  링크: 추천 근거  GET /recommendations/{recommendationId}/matches/{matchResultId}   (UC-C-03)
        팀 보기    GET /teams/{teamId},   찜 신청 POST /teams/{teamId}/bookmarks (임의 지정)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="나의 fit 찾기"/>
  <jsp:param name="active" value="match"/>
</jsp:include>

<div class="row between">
  <div class="col" style="gap:4px">
    <h1>나와 잘 맞는 FiT들이에요!</h1>
    <div class="sub-text">나의 스터디 타입과 가장 잘 맞는 팀을 ${fn:length(recommendation.matchResults)}위까지 보여드려요.</div>
  </div>
  <div class="row" style="gap:12px">
    <c:if test="${not empty studyType}"><span class="chip">My Study Type <c:out value="${studyType.typeCode}"/></span></c:if>
    <a class="link" href="${ctx}/profile/survey/retake">다시 검사하기</a>
  </div>
</div>

<c:choose>
  <c:when test="${empty recommendation.matchResults}">
    <div class="card empty">지금은 추천할 수 있는 팀이 없어요. <a class="link" href="${ctx}/teams">팀 살펴보기</a>에서 직접 찾아보세요.</div>
  </c:when>
  <c:otherwise>
    <div class="col">
      <c:forEach var="r" items="${recommendation.matchResults}" varStatus="st">
        <fmt:formatNumber var="pct" value="${r.totalScore}" maxFractionDigits="0"/>
        <div class="card row-card">
          <div class="rank">${st.count}</div>
          <div class="thumb">대표 사진</div>
          <div class="col grow" style="gap:6px;flex:1 1 240px;min-width:0">
            <a href="${ctx}/teams/${r.team.teamId}" style="font-size:17px;font-weight:700"><c:out value="${r.team.title}"/></a>
            <div class="sub-text">
              <c:out value="${catKo[r.team.studyCategory.name()]}" default="${r.team.studyCategory}"/> ·
              <c:forEach var="d" items="${r.team.preferredDays}">${dayKo[d.name()]}</c:forEach>
              ${fn:substring(r.team.preferredStartTime, 0, 5)}~${fn:substring(r.team.preferredEndTime, 0, 5)} ·
              <c:out value="${regionKo[r.team.regionCode]}" default="${r.team.regionCode}"/>
            </div>
            <div class="row" style="gap:10px;flex-wrap:nowrap">
              <span class="sub-text" style="white-space:nowrap">FiT 일치도</span>
              <div class="bar" style="flex:1;max-width:240px"><i style="width:${pct > 100 ? 100 : pct}%"></i></div>
              <b style="color:var(--primary)">${pct}%</b>
            </div>
          </div>
          <div class="btn-row">
            <a class="btn outline sm" href="${ctx}/recommendations/${recommendation.id}/matches/${r.id}">추천 근거</a>
            <form class="inline" method="post" action="${ctx}/teams/${r.team.teamId}/bookmarks"><button type="submit" class="btn outline sm">찜 신청</button></form>
            <a class="btn sm" href="${ctx}/teams/${r.team.teamId}">팀 보기</a>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
