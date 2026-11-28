<%--
  27 모임 출석 기록 (UC-D-09, 팀장)
    GET  /attendance/edit?scheduleId=   →  AttendanceController#showAttendanceForm   (기존 출석값 표시, 기록 없는 회원은 '미기록')
    POST /attendance/save (scheduleId, attendanceEntries[i].memberId, attendanceEntries[i].status)
         성공: redirect /attendance/edit?scheduleId=,  오류: 이 화면 forward (errors / message, 제출값 유지)
  모델 속성: schedule:TeamSchedule (id, title, startAt, endAt, team), members:List<TeamMember> (teamMemberId, user.name, role),
             form:AttendanceForm (attendanceEntries:List<AttendanceInput> — memberId, status), errors:Map, message:String
  ※ memberId 는 TeamMember.teamMemberId 로 가정 (설계서의 '회원 ID' 가 userId 면 user.userId 로 바꾼다).
  ※ 상태를 고르지 않은 팀원은 memberId 만 가고 status 가 없다 → 서버가 '미기록'으로 보고 건너뛴다.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="모임 출석 기록"/>
  <jsp:param name="active" value="attendance"/>
  <jsp:param name="size" value="narrow"/>
</jsp:include>

<div class="page-head">
  <a class="back" href="${ctx}/schedule/list?teamId=${schedule.team.teamId}"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>모임 출석 기록</h1>
  <span class="chip"><c:out value="${schedule.team.title}"/></span>
  <span class="chip soft">팀장</span>
</div>

<form class="card" method="post" action="${ctx}/attendance/save" id="attForm" style="gap:16px">
  <input type="hidden" name="scheduleId" value="${schedule.id}">

  <div class="row between">
    <div class="col" style="gap:2px">
      <span class="sub-text">기록할 모임</span>
      <b>${fn:substring(schedule.startAt, 0, 4)}/${fn:substring(schedule.startAt, 5, 7)}/${fn:substring(schedule.startAt, 8, 10)}
         <span data-wd="${fn:substring(schedule.startAt, 0, 10)}"></span>
         <c:out value="${schedule.title}"/> · ${fn:substring(schedule.startAt, 11, 16)} ~ ${fn:substring(schedule.endAt, 11, 16)}</b>
    </div>
    <div class="row" style="gap:6px">
      <span class="chip">출석 <span id="cP">0</span></span>
      <span class="chip soft">지각 <span id="cL">0</span></span>
      <span class="chip dashed">결석 <span id="cA">0</span></span>
      <span class="chip dashed">미기록 <span id="cN">0</span></span>
    </div>
  </div>

  <div class="col" style="gap:0">
    <c:forEach var="m" items="${members}" varStatus="st">
      <%-- 이 팀원의 기존/제출 상태 찾기 --%>
      <c:set var="cur" value=""/>
      <c:forEach var="e" items="${form.attendanceEntries}">
        <c:if test="${e.memberId eq m.teamMemberId}"><c:set var="cur" value="${e.status}"/></c:if>
      </c:forEach>
      <c:set var="curS" value="${empty cur ? '' : cur.toString()}"/>
      <div class="item" style="padding:12px 0;flex-wrap:nowrap;gap:12px">
        <input type="hidden" name="attendanceEntries[${st.index}].memberId" value="${m.teamMemberId}">
        <div class="avatar" aria-hidden="true"></div>
        <div class="grow row" style="gap:6px">
          <span><c:out value="${m.user.name}"/></span>
          <c:if test="${m.role.toString() eq 'LEADER'}"><span class="chip">팀장</span></c:if>
        </div>
        <div class="seg" role="radiogroup" aria-label="${m.user.name} 출석 상태">
          <c:forEach var="o" items="${fn:split('PRESENT:출석,LATE:지각,ABSENT:결석', ',')}">
            <c:set var="v" value="${fn:substringBefore(o, ':')}"/>
            <label><input type="radio" name="attendanceEntries[${st.index}].status" value="${v}" ${curS eq v ? 'checked' : ''}><span>${fn:substringAfter(o, ':')}</span></label>
          </c:forEach>
        </div>
      </div>
    </c:forEach>
  </div>

  <div class="sub-text" style="font-size:14px">기록이 없는 팀원은 미기록으로 보여요. 저장하면 이 화면으로 다시 돌아와요.</div>
  <div class="row between">
    <a class="btn outline" href="${ctx}/schedule/list?teamId=${schedule.team.teamId}">일정 목록으로</a>
    <button type="submit" class="btn">출석 저장</button>
  </div>
</form>

<script>
  (function () {
    var f = document.getElementById('attForm');
    function count() {
      var rows = f.querySelectorAll('.item'), p = 0, l = 0, a = 0, n = 0;
      rows.forEach(function (r) {
        var c = r.querySelector('input[type=radio]:checked');
        if (!c) n++; else if (c.value === 'PRESENT') p++; else if (c.value === 'LATE') l++; else a++;
      });
      document.getElementById('cP').textContent = p; document.getElementById('cL').textContent = l;
      document.getElementById('cA').textContent = a; document.getElementById('cN').textContent = n;
    }
    f.addEventListener('change', count); count();
    document.querySelectorAll('[data-wd]').forEach(function (el) {
      var d = new Date(el.getAttribute('data-wd') + 'T00:00:00');
      if (!isNaN(d)) el.textContent = '(' + ['일', '월', '화', '수', '목', '금', '토'][d.getDay()] + ')';
    });
  })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
