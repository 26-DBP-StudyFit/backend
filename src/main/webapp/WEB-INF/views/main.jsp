<%--
  05 홈   로그인 성공 후 redirect /main   (설계서에 컨트롤러 미정 → MainController#showMain 가정)
  검색:  GET /main?keyword=&mine=true  (제목 검색 · '내가 가입한 팀만' 은 서버에서 필터)
  모델 속성
    teams:List<Team>             참여 중인 팀 (Team 에 memberCount:int 필요 — '팀원 3 / 5명')
    schedules:List<TeamSchedule> 캘린더 점(•) 표시용 (startAt)
    goals:List<Goal>             '모든 체크리스트' (title, status)
    bookmarkedTeams:List<Team>   즐겨찾기
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="홈"/>
  <jsp:param name="active" value="home"/>
</jsp:include>

<div class="split">
  <section class="grow">
    <div class="row between">
      <h1>참여 중인 스터디</h1>
      <a class="btn" href="${ctx}/teams/new">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>팀 생성
      </a>
    </div>

    <form class="row" method="get" action="${ctx}/main" style="gap:12px 20px">
      <input type="hidden" name="submitted" value="1">
      <div class="search" style="flex:1 1 280px">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#4A5578" stroke-width="2" stroke-linecap="round" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="M21 21l-5-5"/></svg>
        <label for="q" class="sr-only">스터디 검색</label>
        <input id="q" name="keyword" type="search" placeholder="스터디 검색" value="<c:out value='${param.keyword}'/>">
      </div>
      <label class="check">
        <input type="checkbox" name="mine" value="true" ${empty param.submitted or param.mine eq 'true' ? 'checked' : ''} onchange="this.form.submit()">내가 가입한 팀만
      </label>
    </form>

    <c:choose>
      <c:when test="${empty teams}">
        <div class="card empty">아직 참여 중인 스터디가 없어요. <a class="link" href="${ctx}/teams">팀 살펴보기</a> 또는 <a class="link" href="${ctx}/recommendations/teams/form">나의 fit 찾기</a>로 시작해보세요.</div>
      </c:when>
      <c:otherwise>
        <div class="col">
          <c:forEach var="team" items="${teams}">
            <div class="card row-card">
              <div class="thumb">대표 사진</div>
              <div class="col grow" style="gap:4px;flex:1 1 200px;min-width:0">
                <a href="${ctx}/teams/${team.teamId}/home" style="font-size:17px;font-weight:700"><c:out value="${team.title}"/></a>
                <div class="sub-text">
                  <c:out value="${catKo[team.studyCategory.name()]}" default="${team.studyCategory}"/> ·
                  <c:forEach var="d" items="${team.preferredDays}">${dayKo[d.name()]}</c:forEach> ·
                  ${fn:substring(team.preferredStartTime, 0, 5)}~${fn:substring(team.preferredEndTime, 0, 5)}
                </div>
                <div class="sub-text">팀원 ${team.memberCount} / ${team.recruitmentCount}명</div>
              </div>
              <a class="btn outline" href="${ctx}/teams/${team.teamId}/home">팀 홈</a>
            </div>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>

  <aside class="side">
    <div class="card">
      <div class="card-title">캘린더</div>
      <div id="cal"></div>
      <ul id="cal-events" hidden>
        <c:forEach var="s" items="${schedules}">
          <li data-date="${fn:substring(s.startAt, 0, 10)}"><c:out value="${s.title}"/></li>
        </c:forEach>
      </ul>
    </div>

    <div class="card" style="gap:8px">
      <div class="card-title">
        모든 체크리스트
        <c:if test="${not empty teams}">
          <a class="icon-btn" href="${ctx}/goal/list?teamId=${teams[0].teamId}&scope=PERSONAL" aria-label="체크리스트 추가"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg></a>
        </c:if>
      </div>
      <c:choose>
        <c:when test="${empty goals}"><div class="sub-text">체크리스트 항목이 없어요.</div></c:when>
        <c:otherwise>
          <c:forEach var="g" items="${goals}">
            <c:set var="done" value="${g.status.toString() eq 'COMPLETED'}"/>
            <label class="check" style="${done ? 'color:var(--muted);text-decoration:line-through' : ''}">
              <input type="checkbox" disabled ${done ? 'checked' : ''}><c:out value="${g.title}"/>
            </label>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="card" style="gap:4px">
      <div class="card-title" style="margin-bottom:4px">즐겨찾기</div>
      <c:choose>
        <c:when test="${empty bookmarkedTeams}"><div class="sub-text">찜한 팀이 없어요.</div></c:when>
        <c:otherwise>
          <c:forEach var="t" items="${bookmarkedTeams}">
            <a class="link" style="display:flex;align-items:center;min-height:40px;text-decoration:none" href="${ctx}/teams/${t.teamId}"><c:out value="${t.title}"/></a>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
  </aside>
</div>

<script src="${ctx}/js/calendar.js"></script>
<script>StudyFitCalendar.init('cal', 'cal-events');</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
