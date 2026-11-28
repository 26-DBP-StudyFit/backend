<%--
  20 팀 공통 가능 시간 (UC-D-02)   GET /availability/common?teamId=   →  AvailabilityController#showCommonAvailability
  모델 속성: team:Team (추가, 제목·teamId),
             status:CommonAvailabilityStatus  AVAILABLE | NO_COMMON_TIME | INCOMPLETE   (상수명은 디자인 문구 기준)
             commonSlots:List<CommonTimeSlot> (dayOfWeek, startTime, endTime) — status=AVAILABLE 일 때
             unregisteredMembers:List<TeamMember> (user.name) — status=INCOMPLETE 일 때
  ※ 디자인의 상태 탭(AVAILABLE/NO_COMMON_TIME/INCOMPLETE)은 시안 확인용이라 JSP 에는 넣지 않고 status 로 분기.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀 공통 가능 시간"/>
  <jsp:param name="active" value="home"/>
</jsp:include>

<c:set var="st" value="${status.toString()}"/>
<c:set var="dayKeys" value="${fn:split('MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY', ',')}"/>

<div class="page-head">
  <a class="back" href="${ctx}/teams/${team.teamId}/home"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>팀 공통 가능 시간</h1>
  <span class="chip"><c:out value="${team.title}"/></span>
</div>

<div class="split">
  <section class="card grow" style="gap:12px">
    <div class="row between">
      <div class="card-title">팀 공통 가능 시간</div>
      <div class="legend"><span>팀원 전체가 가능한 시간</span></div>
    </div>
    <div class="week">
      <div></div>
      <c:forEach var="d" items="${dayKeys}"><div class="h">${dayKo[d]}</div></c:forEach>
      <c:forEach var="h" begin="9" end="20">
        <div class="t">${h < 10 ? '0' : ''}${h}</div>
        <c:forEach var="d" items="${dayKeys}">
          <c:set var="common" value="false"/>
          <c:if test="${st eq 'AVAILABLE'}">
            <c:forEach var="s" items="${commonSlots}">
              <c:if test="${s.dayOfWeek.toString() eq d and fn:substring(s.startTime, 0, 2) le h and h lt fn:substring(s.endTime, 0, 2)}"><c:set var="common" value="true"/></c:if>
            </c:forEach>
          </c:if>
          <div class="c">${common ? '<i class="mine"></i>' : ''}</div>
        </c:forEach>
      </c:forEach>
    </div>
  </section>

  <aside class="side">
    <section class="card" style="gap:12px">
      <div class="card-title">계산 결과</div>
      <c:choose>
        <c:when test="${st eq 'AVAILABLE'}">
          <div class="col" style="gap:6px">
            <div style="font-weight:700">전체 팀원이 모두 가능한 시간이에요</div>
            <c:forEach var="s" items="${commonSlots}">
              <div class="chip soft" style="align-self:flex-start">${dayKo[s.dayOfWeek.name()]}요일 ${fn:substring(s.startTime, 0, 5)} ~ ${fn:substring(s.endTime, 0, 5)}</div>
            </c:forEach>
          </div>
        </c:when>
        <c:when test="${st eq 'NO_COMMON_TIME'}">
          <div class="col" style="gap:4px">
            <div style="font-weight:700">공통으로 가능한 시간이 없어요</div>
            <div class="sub-text">팀원들의 가능 시간이 겹치지 않아요. 팀원들과 시간을 조금씩 조정해 보세요.</div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="col" style="gap:8px">
            <div class="col" style="gap:4px">
              <div style="font-weight:700">아직 등록하지 않은 팀원이 있어요</div>
              <div class="sub-text">모두 등록하면 공통 시간을 계산해 드려요.</div>
            </div>
            <c:forEach var="m" items="${unregisteredMembers}">
              <div class="item"><span><c:out value="${m.user.name}"/></span><span class="chip dashed">미등록</span></div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
      <div class="divider"></div>
      <div class="btn-row">
        <a class="btn outline sm" href="${ctx}/availability/edit?teamId=${team.teamId}">내 가능 시간 수정</a>
        <c:if test="${st eq 'AVAILABLE'}"><a class="btn sm" href="${ctx}/schedule/create?teamId=${team.teamId}">이 시간으로 일정 등록</a></c:if>
      </div>
    </section>
  </aside>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
