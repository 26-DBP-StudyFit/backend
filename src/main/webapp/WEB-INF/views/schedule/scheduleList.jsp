<%--
  23 팀 일정 목록 (FR-D-03)   GET /schedule/list?teamId=   →  ScheduleController#listSchedules
  삭제 (UC-D-06)              POST /schedule/delete (scheduleId, teamId)  성공: redirect /schedule/list?teamId=,  제한 위반: /error/message.jsp (HTTP 409)
  모델 속성: team:Team, schedules:List<TeamSchedule>, isLeader:boolean (둘 다 추가 속성)
    TeamSchedule: id, title, startAt/endAt(LocalDateTime 가정), locationOrLink, status(UPCOMING/IN_PROGRESS/ENDED),
                  scheduleType(REGULAR/MAKEUP/SPECIAL/ETC — 도메인엔 없지만 등록 파라미터에 있어서 필요)
  규칙: 진행 중·종료 일정은 수정/삭제 불가(서버가 409) → 화면에서는 버튼을 비활성으로 보여준다. 등록·수정·삭제·출석 기록은 팀장만.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀 일정"/>
  <jsp:param name="active" value="home"/>
</jsp:include>

<div class="row between">
  <div class="page-head">
    <a class="back" href="${ctx}/teams/${team.teamId}/home"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
    <h1>팀 일정</h1>
    <span class="chip"><c:out value="${team.title}"/></span>
    <c:if test="${isLeader}"><span class="chip soft">팀장</span></c:if>
  </div>
  <c:if test="${isLeader}"><a class="btn" href="${ctx}/schedule/create?teamId=${team.teamId}">+ 일정 등록</a></c:if>
</div>

<c:choose>
  <c:when test="${empty schedules}">
    <div class="card empty">등록된 일정이 없어요.<c:if test="${isLeader}"> 오른쪽 위 ‘일정 등록’으로 첫 일정을 만들어보세요.</c:if></div>
  </c:when>
  <c:otherwise>
    <div class="card" style="gap:0;padding-top:8px;padding-bottom:8px">
      <c:forEach var="s" items="${schedules}">
        <c:set var="stt" value="${s.status.toString()}"/>
        <c:set var="editable" value="${stt eq 'UPCOMING'}"/>
        <div class="item" style="padding:14px 0;flex-wrap:nowrap;gap:16px">
          <div class="date-block">
            <small>${fn:substring(s.startAt, 5, 7) + 0}월</small>
            <b>${fn:substring(s.startAt, 8, 10)}</b>
            <small data-wd="${fn:substring(s.startAt, 0, 10)}"></small>
          </div>
          <div class="grow col" style="gap:4px">
            <div class="row" style="gap:6px">
              <b style="font-size:17px"><c:out value="${s.title}"/></b>
              <span class="chip soft"><c:out value="${schTypeKo[s.scheduleType.name()]}" default="${s.scheduleType}"/></span>
              <span class="chip ${stt eq 'IN_PROGRESS' ? '' : 'dashed'}"><c:out value="${schStatusKo[stt]}" default="${stt}"/></span>
            </div>
            <div class="sub-text">${fn:substring(s.startAt, 11, 16)} ~ ${fn:substring(s.endAt, 11, 16)} · <c:out value="${s.locationOrLink}"/></div>
          </div>
          <c:if test="${isLeader}">
            <div class="btn-row" style="flex-wrap:nowrap">
              <a class="btn outline sm" href="${ctx}/attendance/edit?scheduleId=${s.id}">출석 기록</a>
              <c:choose>
                <c:when test="${editable}">
                  <a class="btn outline sm" href="${ctx}/schedule/edit?scheduleId=${s.id}">수정</a>
                  <form class="inline" method="post" action="${ctx}/schedule/delete" onsubmit="return confirm('이 일정을 삭제할까요?');">
                    <input type="hidden" name="scheduleId" value="${s.id}">
                    <input type="hidden" name="teamId" value="${team.teamId}">
                    <button type="submit" class="btn gray sm">삭제</button>
                  </form>
                </c:when>
                <c:otherwise>
                  <span class="btn outline sm disabled" aria-disabled="true">수정</span>
                  <span class="btn outline sm disabled" aria-disabled="true">삭제</span>
                </c:otherwise>
              </c:choose>
            </div>
          </c:if>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

<div class="sub-text" style="font-size:14px">진행 중이거나 종료된 일정은 수정·삭제할 수 없어요.</div>

<script>
  document.querySelectorAll('[data-wd]').forEach(function (el) {
    var d = new Date(el.getAttribute('data-wd') + 'T00:00:00');
    if (!isNaN(d)) el.textContent = ['일', '월', '화', '수', '목', '금', '토'][d.getDay()];
  });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
