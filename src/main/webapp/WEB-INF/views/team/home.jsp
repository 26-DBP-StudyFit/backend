<%--
  13 팀 메인   GET /teams/{teamId}/home   (설계서에 'URI 미지정' → TeamController#showTeamHome 가정)
  모델 속성
    team:Team, members:List<TeamMember>(user.name, role), schedules:List<TeamSchedule>(startAt, title),
    goals:List<Goal>(id, title, status), isLeader:boolean,
    applications:List<ApplicationDetailDto>(teamId, applicationId, applicantName) — 팀장일 때 가입 승인 영역,
    posts:List<PostDto>(title, authorName, createdAt) — 게시판(설계서에 기능 없음, 없으면 빈 목록 안내)
  체크리스트 항목을 누르면 POST /goal/achievement (goalId, achieved) — 달성 토글 (UC-D-08)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀 메인"/>
  <jsp:param name="active" value="home"/>
</jsp:include>

<section class="row between" style="gap:16px">
  <div class="row" style="gap:10px">
    <h1><c:out value="${team.title}"/></h1>
    <span class="chip"><c:out value="${catKo[team.studyCategory.name()]}" default="${team.studyCategory}"/></span>
    <span class="chip soft"><c:forEach var="d" items="${team.preferredDays}">${dayKo[d.name()]}</c:forEach> · ${fn:substring(team.preferredStartTime, 0, 5)}~${fn:substring(team.preferredEndTime, 0, 5)}</span>
  </div>
  <div class="btn-row">
    <a class="btn outline sm" href="${ctx}/availability/coordination?teamId=${team.teamId}">팀 일정 조율</a>
    <a class="btn outline sm" href="${ctx}/schedule/list?teamId=${team.teamId}">팀 일정</a>
    <a class="btn outline sm" href="${ctx}/goal/list?teamId=${team.teamId}">목표</a>
    <a class="btn outline sm" href="${ctx}/availability/common?teamId=${team.teamId}">공통 시간</a>
    <a class="btn outline sm" href="${ctx}/activity/score?teamId=${team.teamId}">활동 점수</a>
    <c:if test="${isLeader}"><a class="btn outline sm" href="${ctx}/leader/teams/${team.teamId}/balance/form">팀 균형 분석</a></c:if>
    <a class="btn outline sm" href="${ctx}/attendance?teamId=${team.teamId}">출석 체크</a>
  </div>
</section>

<div class="split">
  <div class="grow">
    <section class="card">
      <div class="card-title">팀 캘린더</div>
      <div id="cal"></div>
      <ul id="cal-events" hidden>
        <c:forEach var="s" items="${schedules}">
          <li data-date="${fn:substring(s.startAt, 0, 10)}"><c:out value="${s.title}"/></li>
        </c:forEach>
      </ul>
    </section>

    <section class="card" style="gap:4px">
      <div class="card-title">게시판 <a class="link" style="font-weight:400;font-size:14px" href="${ctx}/teams/${team.teamId}/posts">전체 보기</a></div>
      <c:choose>
        <c:when test="${empty posts}"><div class="sub-text" style="padding:8px 0">아직 게시글이 없어요.</div></c:when>
        <c:otherwise>
          <c:forEach var="p" items="${posts}">
            <div class="kv"><span style="color:var(--text)"><c:out value="${p.title}"/></span><span class="sub-text" style="font-weight:400"><c:out value="${p.authorName}"/> · ${fn:substring(p.createdAt, 5, 10)}</span></div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </section>
  </div>

  <aside class="side">
    <section class="card" style="gap:4px">
      <div class="card-title">팀 체크리스트
        <a class="icon-btn" href="${ctx}/goal/list?teamId=${team.teamId}&scope=TEAM" aria-label="체크리스트 추가"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg></a>
      </div>
      <c:choose>
        <c:when test="${empty goals}"><div class="sub-text">할 일 항목이 없어요.</div></c:when>
        <c:otherwise>
          <c:forEach var="g" items="${goals}">
            <c:set var="done" value="${g.status.toString() eq 'COMPLETED'}"/>
            <form method="post" action="${ctx}/goal/achievement">
              <input type="hidden" name="goalId" value="${g.id}">
              <input type="hidden" name="achieved" value="${!done}">
              <button type="submit" aria-pressed="${done}" class="check" style="width:100%;border:0;background:transparent;font:inherit;text-align:left;${done ? 'color:var(--muted);text-decoration:line-through' : ''}">
                <input type="checkbox" tabindex="-1" onclick="return false" ${done ? 'checked' : ''}><c:out value="${g.title}"/>
              </button>
            </form>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </section>

    <section class="card">
      <div class="card-title">팀원 <span class="sub-text" style="font-weight:400">${fn:length(members)} / ${team.recruitmentCount}명</span></div>
      <c:forEach var="m" items="${members}">
        <div class="row" style="gap:10px">
          <div class="avatar" aria-hidden="true"></div>
          <div style="flex:1"><c:out value="${m.user.name}"/></div>
          <c:if test="${m.role.toString() eq 'LEADER'}"><span class="chip">팀장</span></c:if>
        </div>
      </c:forEach>
    </section>

    <c:if test="${isLeader}">
      <section class="card">
        <div class="card-title">팀원 가입 승인 <a class="link" style="font-weight:400;font-size:14px" href="${ctx}/my/applications">알림함</a></div>
        <c:choose>
          <c:when test="${empty applications}"><div class="sub-text">대기 중인 가입 신청이 없어요.</div></c:when>
          <c:otherwise>
            <c:forEach var="a" items="${applications}">
              <div class="col" style="gap:8px">
                <a href="${ctx}/teams/${a.teamId}/applications/${a.applicationId}"><c:out value="${a.applicantName}"/> 님의 가입 신청</a>
                <div class="btn-row">
                  <form class="inline" method="post" action="${ctx}/teams/${a.teamId}/applications/${a.applicationId}/approve"><button type="submit" class="btn sm">수락</button></form>
                  <form class="inline" method="post" action="${ctx}/teams/${a.teamId}/applications/${a.applicationId}/reject"><button type="submit" class="btn gray sm">거절</button></form>
                </div>
              </div>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </section>
    </c:if>
  </aside>
</div>

<script src="${ctx}/js/calendar.js"></script>
<script>StudyFitCalendar.init('cal', 'cal-events', { chips: true });</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
