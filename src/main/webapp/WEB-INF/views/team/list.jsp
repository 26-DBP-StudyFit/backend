<%--
  08 팀 살펴보기   GET /teams?keyword=&studyCategory=&meetingMode=&mood=   (설계서에 없음 → TeamController#listTeams 가정)
  찜 신청          POST /teams/{teamId}/bookmarks   (설계서에 없음, 임의 지정)
  모델 속성: teams:List<Team>  (Team 에 meetingMode 필요)
  필터는 GET 파라미터로 서버에 전달 (체크 변경 시 바로 검색). mood(분위기)는 도메인에 없는 항목이라 서버에서 무시해도 됨.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀 살펴보기"/>
  <jsp:param name="active" value="explore"/>
</jsp:include>

<c:set var="fCat" value="${fn:join(paramValues.studyCategory, ',')}"/>
<c:set var="fMode" value="${fn:join(paramValues.meetingMode, ',')}"/>

<h1>팀 살펴보기</h1>

<form method="get" action="${ctx}/teams" class="col" style="gap:20px">
  <div class="search">
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#4A5578" stroke-width="2" stroke-linecap="round" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="M21 21l-5-5"/></svg>
    <label for="q" class="sr-only">팀 검색</label>
    <input id="q" name="keyword" type="search" placeholder="팀 이름, 분야, 지역으로 검색" value="<c:out value='${param.keyword}'/>">
  </div>

  <div class="split">
    <aside class="card" style="flex:0 1 240px;gap:20px">
      <fieldset>
        <legend>분야</legend>
        <c:forEach var="o" items="${fn:split('STUDY:스터디,HOBBY:취미,EXERCISE:운동,ETC:기타', ',')}">
          <c:set var="v" value="${fn:substringBefore(o, ':')}"/>
          <label class="check"><input type="checkbox" name="studyCategory" value="${v}" ${fn:contains(fCat, v) ? 'checked' : ''} onchange="this.form.submit()">${fn:substringAfter(o, ':')}</label>
        </c:forEach>
      </fieldset>
      <fieldset>
        <legend>진행 방식</legend>
        <label class="check"><input type="checkbox" name="meetingMode" value="OFFLINE" ${fn:contains(fMode, 'OFFLINE') ? 'checked' : ''} onchange="this.form.submit()">대면</label>
        <label class="check"><input type="checkbox" name="meetingMode" value="ONLINE" ${fn:contains(fMode, 'ONLINE') ? 'checked' : ''} onchange="this.form.submit()">비대면</label>
      </fieldset>
      <fieldset>
        <legend>분위기</legend>
        <label class="check"><input type="checkbox" name="mood" value="QUIET" ${param.mood eq 'QUIET' ? 'checked' : ''} onchange="this.form.submit()">조용함</label>
      </fieldset>
    </aside>

    <section class="grow" style="gap:12px">
      <c:choose>
        <c:when test="${empty teams}"><div class="card empty">조건에 맞는 팀이 없어요. 검색 조건을 바꿔보세요.</div></c:when>
        <c:otherwise>
          <c:forEach var="team" items="${teams}">
            <div class="card row-card">
              <div class="thumb">대표 사진</div>
              <div class="col" style="gap:6px;flex:1 1 220px;min-width:0">
                <a href="${ctx}/teams/${team.teamId}" style="font-size:17px;font-weight:700"><c:out value="${team.title}"/></a>
                <div class="row" style="gap:6px">
                  <span class="chip"><c:out value="${catKo[team.studyCategory.name()]}" default="${team.studyCategory}"/></span>
                  <span class="chip soft"><c:out value="${ansKo[team.meetingMode.name()]}" default="${team.meetingMode}"/></span>
                </div>
                <div class="sub-text">
                  <c:forEach var="d" items="${team.preferredDays}">${dayKo[d.name()]}</c:forEach>
                  ${fn:substring(team.preferredStartTime, 0, 5)}~${fn:substring(team.preferredEndTime, 0, 5)} ·
                  <c:out value="${regionKo[team.regionCode]}" default="${team.regionCode}"/> · 필요한 온도 ${team.requiredTemperature}°C
                </div>
              </div>
              <form class="inline" method="post" action="${ctx}/teams/${team.teamId}/bookmarks">
                <button type="submit" class="btn outline">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round" aria-hidden="true"><path d="M6 3h12v18l-6-4-6 4z"/></svg>찜 신청
                </button>
              </form>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </section>
  </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
