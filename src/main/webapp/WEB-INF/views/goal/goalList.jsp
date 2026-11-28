<%--
  25 팀·개인 목표 (UC-D-07/08)
    GET  /goal/list?teamId=&scope=TEAM|PERSONAL   →  GoalController#listGoals   (scope 없으면 TEAM)
    POST /goal/create (teamId, scope, title, description, dueDate)       성공: redirect /goal/list?teamId=&scope=,  오류: 이 화면(errors/message, form 값 유지)
    POST /goal/achievement (goalId, achieved)                            성공: redirect /goal/list?teamId=&scope=,  저장 오류: /error/message.jsp
  모델 속성: teamId:Long, team:Team(추가), scope:GoalScope, goals:List<Goal>, form:GoalForm, canCreate:boolean, errors:Map, message
    Goal: id, title, description(추가 필요), dueDate:LocalDate, status (COMPLETED = 달성 — 상수명은 GoalStatus 에 맞게 수정)
  ※ 달성 토글은 목표 한 줄 = form 하나. achieved 에는 '바뀔 값'(현재 반대)을 보낸다.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀·개인 목표"/>
  <jsp:param name="active" value="home"/>
</jsp:include>

<c:set var="sc" value="${empty scope ? 'TEAM' : scope.toString()}"/>
<c:set var="scLabel" value="${sc eq 'TEAM' ? '팀 목표' : '개인 목표'}"/>
<c:set var="doneCnt" value="0"/>
<c:forEach var="g" items="${goals}"><c:if test="${g.status.toString() eq 'COMPLETED'}"><c:set var="doneCnt" value="${doneCnt + 1}"/></c:if></c:forEach>
<c:set var="total" value="${fn:length(goals)}"/>

<div class="page-head">
  <a class="back" href="${ctx}/teams/${teamId}/home"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>팀 · 개인 목표</h1>
  <c:if test="${not empty team}"><span class="chip"><c:out value="${team.title}"/></span></c:if>
</div>

<div class="pills" role="tablist" aria-label="목표 종류">
  <a class="pill" role="tab" aria-pressed="${sc eq 'TEAM'}" aria-selected="${sc eq 'TEAM'}" href="${ctx}/goal/list?teamId=${teamId}&scope=TEAM">팀 목표</a>
  <a class="pill" role="tab" aria-pressed="${sc eq 'PERSONAL'}" aria-selected="${sc eq 'PERSONAL'}" href="${ctx}/goal/list?teamId=${teamId}&scope=PERSONAL">개인 목표</a>
</div>

<div class="split">
  <section class="card grow" style="gap:12px">
    <div class="row between">
      <div class="card-title">${scLabel} 목록</div>
      <div class="row" style="gap:6px"><span class="sub-text">달성</span><b style="color:var(--primary)">${doneCnt} / ${total}</b></div>
    </div>
    <div class="bar"><i style="width:${total eq 0 ? 0 : doneCnt * 100 / total}%"></i></div>

    <c:choose>
      <c:when test="${empty goals}"><div class="empty" style="padding:24px 0">아직 등록된 ${scLabel}가 없어요.</div></c:when>
      <c:otherwise>
        <div class="col" style="gap:0">
          <c:forEach var="g" items="${goals}">
            <c:set var="done" value="${g.status.toString() eq 'COMPLETED'}"/>
            <form class="inline" method="post" action="${ctx}/goal/achievement">
              <input type="hidden" name="goalId" value="${g.id}">
              <input type="hidden" name="achieved" value="${!done}">
              <button type="submit" aria-pressed="${done}" class="item" style="width:100%;border:0;border-bottom:1px solid var(--soft);background:transparent;font:inherit;text-align:left;cursor:pointer;flex-wrap:nowrap;gap:12px">
                <input type="checkbox" tabindex="-1" onclick="return false" ${done ? 'checked' : ''} aria-hidden="true">
                <span class="grow col" style="gap:2px">
                  <span style="font-weight:700;${done ? 'color:var(--muted);text-decoration:line-through' : ''}"><c:out value="${g.title}"/></span>
                  <span class="sub-text" style="font-size:13px"><c:out value="${g.description}"/></span>
                </span>
                <span class="sub-text" style="white-space:nowrap">~ ${fn:substring(g.dueDate, 5, 7)}/${fn:substring(g.dueDate, 8, 10)}</span>
                <span class="chip ${done ? '' : 'dashed'}">${done ? '달성' : '진행 중'}</span>
              </button>
            </form>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
    <div class="sub-text" style="font-size:14px">목표를 누르면 달성 여부가 바뀌어요.</div>
  </section>

  <aside class="side">
    <c:choose>
      <c:when test="${canCreate}">
        <form class="card" method="post" action="${ctx}/goal/create" style="gap:12px">
          <input type="hidden" name="teamId" value="${teamId}">
          <input type="hidden" name="scope" value="${sc}">
          <div class="card-title">${scLabel} 추가</div>
          <div class="field">
            <label for="g1">제목</label>
            <input id="g1" name="title" type="text" placeholder="예) 모의고사 3회 풀기" value="<c:out value='${form.title}'/>" required>
            <c:if test="${not empty errors.title}"><div class="hint" style="color:#b3261e"><c:out value="${errors.title}"/></div></c:if>
          </div>
          <div class="field">
            <label for="g2">설명</label>
            <textarea id="g2" name="description" rows="3" placeholder="목표를 간단히 적어주세요"><c:out value="${form.description}"/></textarea>
          </div>
          <div class="field">
            <label for="g3">마감일</label>
            <input id="g3" name="dueDate" type="date" value="${form.dueDate}" required>
            <c:if test="${not empty errors.dueDate}"><div class="hint" style="color:#b3261e"><c:out value="${errors.dueDate}"/></div></c:if>
          </div>
          <div class="btn-row end"><button type="submit" class="btn">목표 추가</button></div>
        </form>
      </c:when>
      <c:otherwise>
        <div class="card"><div class="sub-text">팀 목표는 팀장만 추가할 수 있어요.</div></div>
      </c:otherwise>
    </c:choose>
  </aside>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
