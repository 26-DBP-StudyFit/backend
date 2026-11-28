<%--
  29 내 활동 점수·지표 (UC-D-10)   GET /activity/score?teamId=   →  ActivityController#showActivityMetrics
  모델 속성: teamId:Long, team:Team(추가),
             metrics:ActivityMetrics — attendanceRate, goalAchievementRate, activityScore (0~100 가정, 계산 불가면 null),
                                       presentCount, lateCount, absentCount, goalDoneCount, goalProgressCount (추가 필요 필드)
             recentAttendance:List<AttendanceRecord> (schedule.startAt, status),
             goals:List<Goal> (title, status)
  ※ 계산할 수 없는 지표(null)는 0점이 아니라 '미집계' 로 보여준다 (디자인 규칙).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="내 활동 점수"/>
  <jsp:param name="active" value="home"/>
</jsp:include>

<div class="page-head">
  <a class="back" href="${ctx}/teams/${teamId}/home"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>내 활동 점수</h1>
  <c:if test="${not empty team}"><span class="chip"><c:out value="${team.title}"/></span></c:if>
</div>

<div class="grid3">
  <section class="card">
    <div class="sub-text">내 출석률</div>
    <c:choose>
      <c:when test="${not empty metrics.attendanceRate}">
        <fmt:formatNumber var="ar" value="${metrics.attendanceRate}" maxFractionDigits="0"/>
        <div class="score">${ar}%</div>
        <div class="bar"><i style="width:${ar > 100 ? 100 : ar}%"></i></div>
      </c:when>
      <c:otherwise><div class="score" style="color:var(--muted)">미집계</div><div class="bar"></div></c:otherwise>
    </c:choose>
    <div class="sub-text" style="font-size:14px">출석 ${metrics.presentCount} · 지각 ${metrics.lateCount} · 결석 ${metrics.absentCount}</div>
  </section>

  <section class="card">
    <div class="sub-text">목표 달성률</div>
    <c:choose>
      <c:when test="${not empty metrics.goalAchievementRate}">
        <fmt:formatNumber var="gr" value="${metrics.goalAchievementRate}" maxFractionDigits="0"/>
        <div class="score">${gr}%</div>
        <div class="bar"><i style="width:${gr > 100 ? 100 : gr}%"></i></div>
        <div class="sub-text" style="font-size:14px">달성 ${metrics.goalDoneCount} / 진행 중 ${metrics.goalProgressCount}</div>
      </c:when>
      <c:otherwise>
        <div class="score" style="color:var(--muted)">미집계</div><div class="bar"></div>
        <div class="sub-text" style="font-size:14px">등록된 목표가 없어요</div>
      </c:otherwise>
    </c:choose>
  </section>

  <section class="card">
    <div class="sub-text">활동 점수</div>
    <c:choose>
      <c:when test="${not empty metrics.activityScore}">
        <fmt:formatNumber var="sc" value="${metrics.activityScore}" maxFractionDigits="0"/>
        <div class="score">${sc}점</div>
        <div class="bar"><i style="width:${sc > 100 ? 100 : sc}%"></i></div>
      </c:when>
      <c:otherwise><div class="score" style="color:var(--muted)">미집계</div><div class="bar"></div></c:otherwise>
    </c:choose>
    <div class="sub-text" style="font-size:14px">출석률과 목표 달성률로 계산해요</div>
  </section>
</div>

<div class="grid2">
  <section class="card" style="gap:4px">
    <div class="card-title">최근 출석 내역</div>
    <c:choose>
      <c:when test="${empty recentAttendance}"><div class="sub-text" style="padding:8px 0">출석 기록이 아직 없어요.</div></c:when>
      <c:otherwise>
        <c:forEach var="r" items="${recentAttendance}">
          <div class="kv">
            <span>${fn:substring(r.schedule.startAt, 0, 4)}/${fn:substring(r.schedule.startAt, 5, 7)}/${fn:substring(r.schedule.startAt, 8, 10)} <span data-wd="${fn:substring(r.schedule.startAt, 0, 10)}"></span></span>
            <span><c:out value="${attKo[r.status.name()]}" default="${r.status}"/></span>
          </div>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </section>

  <section class="card" style="gap:4px">
    <div class="card-title">목표 현황 <a class="link" style="font-weight:400;font-size:14px" href="${ctx}/goal/list?teamId=${teamId}">목표 보기</a></div>
    <c:choose>
      <c:when test="${empty goals}">
        <div class="sub-text" style="padding:8px 0">아직 등록된 목표가 없어서 달성률을 계산할 수 없어요. 계산할 수 없는 지표는 0점이 아니라 미집계로 보여요.</div>
      </c:when>
      <c:otherwise>
        <c:forEach var="g" items="${goals}">
          <div class="kv"><span><c:out value="${g.title}"/></span><span><c:out value="${g.status.toString() eq 'COMPLETED' ? '달성' : '진행 중'}"/></span></div>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </section>
</div>

<script>
  document.querySelectorAll('[data-wd]').forEach(function (el) {
    var d = new Date(el.getAttribute('data-wd') + 'T00:00:00');
    if (!isNaN(d)) el.textContent = '(' + ['일', '월', '화', '수', '목', '금', '토'][d.getDay()] + ')';
  });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
