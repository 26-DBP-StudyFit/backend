<%--
  21 팀 일정 조율 (디자인의 통합 화면: 내 시간 등록 + 주간 가능 시간표)
    GET  /availability/coordination?teamId=   (설계서에 없음 → AvailabilityController#showCoordination 가정)
    POST /availability/save                   (내 기존 시간 전부 + 새 시간 1줄을 slots[i] 로 함께 전송 — 저장은 '전체 교체' 방식이라서)
  모델 속성: team:Team, isLeader:boolean,
             mySlots:List<AvailabilityInput> (내 가능 시간; dayOfWeek, startTime, endTime),
             teamSlots:List<AvailabilityInput> (다른 팀원들의 가능 시간 전체)
  ※ hidden returnTo=coordination 을 보냄 — 컨트롤러가 이 값이면 저장 후 이 화면으로 redirect (없으면 /availability/edit).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀 일정 조율"/>
  <jsp:param name="active" value="home"/>
</jsp:include>

<c:set var="dayKeys" value="${fn:split('MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY', ',')}"/>
<c:set var="newIdx" value="${fn:length(mySlots)}"/>

<div class="page-head">
  <a class="back" href="${ctx}/teams/${team.teamId}/home"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>팀 일정 조율</h1>
  <span class="chip"><c:out value="${team.title}"/></span>
</div>

<div class="split">
  <form class="card" id="slotForm" method="post" action="${ctx}/availability/save" style="flex:0 1 320px;gap:16px">
    <input type="hidden" name="teamId" value="${team.teamId}">
    <input type="hidden" name="returnTo" value="coordination">
    <c:forEach var="s" items="${mySlots}" varStatus="st">
      <input type="hidden" name="slots[${st.index}].dayOfWeek" value="${s.dayOfWeek}">
      <input type="hidden" name="slots[${st.index}].startTime" value="${fn:substring(s.startTime, 0, 5)}">
      <input type="hidden" name="slots[${st.index}].endTime" value="${fn:substring(s.endTime, 0, 5)}">
    </c:forEach>

    <div class="card-title">내 시간 등록</div>
    <div class="field">
      <label for="day">가능 요일</label>
      <select id="day" name="slots[${newIdx}].dayOfWeek">
        <c:forEach var="d" items="${dayKeys}"><option value="${d}">${dayKo[d]}요일</option></c:forEach>
      </select>
    </div>
    <fieldset>
      <legend>가능 시간</legend>
      <div class="slot-row" style="flex-wrap:nowrap">
        <label for="st" class="sr-only">시작 시간</label>
        <select id="st" name="slots[${newIdx}].startTime">
          <c:forEach var="h" begin="6" end="22"><c:set var="v" value="${h < 10 ? '0' : ''}${h}:00"/><option value="${v}" ${h eq 18 ? 'selected' : ''}>${v}</option></c:forEach>
        </select>
        <span>~</span>
        <label for="et" class="sr-only">끝 시간</label>
        <select id="et" name="slots[${newIdx}].endTime">
          <c:forEach var="h" begin="7" end="23"><c:set var="v" value="${h < 10 ? '0' : ''}${h}:00"/><option value="${v}" ${h eq 20 ? 'selected' : ''}>${v}</option></c:forEach>
        </select>
      </div>
    </fieldset>
    <button type="submit" class="btn block">일정 추가하기</button>
    <div class="divider"></div>
    <button type="button" class="btn outline block" id="toggleTeam" aria-pressed="true">팀원 일정 숨기기</button>
    <div class="legend"><span>내 가능 시간</span><span class="team">팀원 가능 시간</span></div>
    <a class="link" style="font-size:14px" href="${ctx}/availability/edit?teamId=${team.teamId}">내 시간 상세 편집</a>
  </form>

  <section class="card grow" style="gap:12px">
    <div class="row between">
      <div class="card-title">주간 가능 시간표</div>
      <c:if test="${isLeader}"><a class="btn outline sm" href="${ctx}/schedule/create?teamId=${team.teamId}">스터디 시간 결정 (팀장)</a></c:if>
    </div>
    <div class="week" id="grid">
      <div></div>
      <c:forEach var="d" items="${dayKeys}"><div class="h">${dayKo[d]}</div></c:forEach>
      <c:forEach var="h" begin="9" end="20">
        <div class="t">${h < 10 ? '0' : ''}${h}</div>
        <c:forEach var="d" items="${dayKeys}">
          <c:set var="mine" value="false"/>
          <c:set var="team2" value="false"/>
          <c:forEach var="s" items="${mySlots}">
            <c:if test="${s.dayOfWeek.toString() eq d and fn:substring(s.startTime, 0, 2) le h and h lt fn:substring(s.endTime, 0, 2)}"><c:set var="mine" value="true"/></c:if>
          </c:forEach>
          <c:forEach var="s" items="${teamSlots}">
            <c:if test="${s.dayOfWeek.toString() eq d and fn:substring(s.startTime, 0, 2) le h and h lt fn:substring(s.endTime, 0, 2)}"><c:set var="team2" value="true"/></c:if>
          </c:forEach>
          <div class="c"><c:if test="${team2}"><i class="team"></i></c:if><c:if test="${mine}"><i class="mine"></i></c:if></div>
        </c:forEach>
      </c:forEach>
    </div>
  </section>
</div>

<script>
  (function () {
    var btn = document.getElementById('toggleTeam'), grid = document.getElementById('grid');
    btn.addEventListener('click', function () {
      var hidden = grid.classList.toggle('hide-team');
      btn.textContent = hidden ? '팀원 일정 보기' : '팀원 일정 숨기기';
      btn.setAttribute('aria-pressed', hidden ? 'false' : 'true');
    });
    document.getElementById('slotForm').addEventListener('submit', function (e) {
      if (document.getElementById('st').value >= document.getElementById('et').value) {
        e.preventDefault(); alert('끝 시간이 시작 시간보다 늦어야 해요.');
      }
    });
  })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
