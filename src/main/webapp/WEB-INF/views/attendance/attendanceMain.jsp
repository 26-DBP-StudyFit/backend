<%--
  26 출석 체크 (팀원 본인용)   GET /attendance?teamId=   (설계서에 없음 → AttendanceController#showMyAttendance 가정)
  출석하기                     POST /attendance/checkin (scheduleId)   (설계서에 없음, 임의 지정)
  모델 속성: teams:List<Team> (내가 속한 팀), team:Team (선택된 팀 — teamId 없으면 첫 번째),
             summary:{present, late, absent} (내 출석 집계 — Map 또는 DTO),
             todaySchedule:TeamSchedule (오늘 일정, 없으면 null; id, startAt), alreadyChecked:boolean,
             myRecords:List<AttendanceRecord> (schedule.startAt, status)
  ※ 팀장이 모임 전체를 한 번에 기록하는 화면은 attendanceForm.jsp (GET /attendance/edit?scheduleId=).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="출석 체크"/>
  <jsp:param name="active" value="attendance"/>
</jsp:include>

<h1>출석 체크 (Attendance)</h1>

<c:choose>
  <c:when test="${empty teams}">
    <div class="card empty">참여 중인 스터디가 없어요. <a class="link" href="${ctx}/teams">팀 살펴보기</a>에서 가입해보세요.</div>
  </c:when>
  <c:otherwise>
    <div class="split">
      <aside class="card" style="flex:0 1 280px;gap:4px;padding:12px">
        <c:forEach var="t" items="${teams}">
          <c:set var="on" value="${t.teamId eq team.teamId}"/>
          <a href="${ctx}/attendance?teamId=${t.teamId}" class="col" style="gap:2px;padding:10px 12px;border-radius:10px;text-decoration:none;color:var(--text);${on ? 'background:var(--soft);border:1px solid var(--primary)' : ''}">
            <b><c:out value="${t.title}"/></b>
            <span class="sub-text" style="font-size:13px">
              <c:out value="${catKo[t.studyCategory.name()]}" default="${t.studyCategory}"/> ·
              <c:forEach var="d" items="${t.preferredDays}">${dayKo[d.name()]}</c:forEach> ·
              ${fn:substring(t.preferredStartTime, 0, 5)}~${fn:substring(t.preferredEndTime, 0, 5)}
            </span>
          </a>
        </c:forEach>
      </aside>

      <section class="card grow" style="gap:16px">
        <div class="row between">
          <div class="col" style="gap:2px">
            <h2><c:out value="${team.title}"/></h2>
            <div class="sub-text">
              <c:out value="${catKo[team.studyCategory.name()]}" default="${team.studyCategory}"/> ·
              <c:forEach var="d" items="${team.preferredDays}">${dayKo[d.name()]}</c:forEach> ·
              ${fn:substring(team.preferredStartTime, 0, 5)}~${fn:substring(team.preferredEndTime, 0, 5)}
            </div>
          </div>
          <div class="row" style="gap:6px">
            <span class="chip">출석 ${summary.present}</span>
            <span class="chip soft">지각 ${summary.late}</span>
            <span class="chip dashed">결석 ${summary.absent}</span>
          </div>
        </div>

        <c:if test="${not empty todaySchedule}">
          <div class="card row-card" style="background:var(--bg)">
            <div class="col grow" style="gap:2px">
              <span class="sub-text">오늘의 스터디</span>
              <b>${fn:substring(todaySchedule.startAt, 0, 4)}/${fn:substring(todaySchedule.startAt, 5, 7)}/${fn:substring(todaySchedule.startAt, 8, 10)}
                 <span data-wd="${fn:substring(todaySchedule.startAt, 0, 10)}"></span>
                 · ${fn:substring(todaySchedule.startAt, 11, 16)}</b>
            </div>
            <c:choose>
              <c:when test="${alreadyChecked}"><span class="chip">출석 완료</span></c:when>
              <c:otherwise>
                <form class="inline" method="post" action="${ctx}/attendance/checkin">
                  <input type="hidden" name="scheduleId" value="${todaySchedule.id}">
                  <button type="submit" class="btn">출석하기</button>
                </form>
              </c:otherwise>
            </c:choose>
          </div>
        </c:if>

        <div class="col" style="gap:0">
          <div class="kv" style="color:var(--muted);font-size:14px"><span>날짜</span><span>상태</span></div>
          <c:choose>
            <c:when test="${empty myRecords}"><div class="sub-text" style="padding:16px 0">아직 출석 기록이 없어요.</div></c:when>
            <c:otherwise>
              <c:forEach var="r" items="${myRecords}">
                <div class="kv">
                  <span>${fn:substring(r.schedule.startAt, 0, 4)}/${fn:substring(r.schedule.startAt, 5, 7)}/${fn:substring(r.schedule.startAt, 8, 10)} <span data-wd="${fn:substring(r.schedule.startAt, 0, 10)}"></span></span>
                  <span><span class="chip ${r.status.toString() eq 'PRESENT' ? '' : (r.status.toString() eq 'LATE' ? 'soft' : 'dashed')}"><c:out value="${attKo[r.status.name()]}" default="${r.status}"/></span></span>
                </div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </div>
        <div class="btn-row end"><a class="btn outline" href="${ctx}/main">닫기</a></div>
      </section>
    </div>
  </c:otherwise>
</c:choose>

<script>
  document.querySelectorAll('[data-wd]').forEach(function (el) {
    var d = new Date(el.getAttribute('data-wd') + 'T00:00:00');
    if (!isNaN(d)) el.textContent = '(' + ['일', '월', '화', '수', '목', '금', '토'][d.getDay()] + ')';
  });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
