<%--
  24 팀 일정 등록·수정 (FR-D-03, 팀장)
    등록: GET /schedule/create?teamId=   POST /schedule/create (teamId, title, startAt, endAt, location, scheduleType, recurrenceRule)  성공: redirect /schedule/list?teamId=
    수정: GET /schedule/edit?scheduleId= POST /schedule/update (scheduleId + 위 값)    진행 중·종료 일정은 서버가 409 → /error/message.jsp
  모델 속성: teamId:Long, team:Team(제목, 추가), scheduleId:Long (수정일 때만 존재 → 화면 모드 구분),
             form:ScheduleForm (title, scheduleType, recurrenceRule, startAt, endAt, location), errors:Map<String,String>, message:String
  ※ datetime-local 은 'yyyy-MM-ddTHH:mm' 형식이라 form.startAt 을 앞 16자로 자른다.
  ※ 반복 규칙 값: '' (없음) / WEEKLY / BIWEEKLY / MONTHLY — 실제 값에 맞게 수정.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<c:set var="isEdit" value="${not empty scheduleId}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="${isEdit ? '팀 일정 수정' : '팀 일정 등록'}"/>
  <jsp:param name="active" value="home"/>
  <jsp:param name="size" value="narrow"/>
</jsp:include>

<c:set var="tid" value="${empty teamId ? param.teamId : teamId}"/>

<div class="page-head">
  <a class="back" href="${ctx}/schedule/list?teamId=${tid}"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>${isEdit ? '팀 일정 수정' : '팀 일정 등록'}</h1>
  <c:if test="${not empty team}"><span class="chip"><c:out value="${team.title}"/></span></c:if>
  <span class="chip soft">팀장</span>
</div>

<c:if test="${isEdit}"><div class="alert" role="note">수정 모드: 선택한 일정의 기존 값을 불러와 보여줘요. 진행 중이거나 종료된 일정은 수정할 수 없어요.</div></c:if>

<form class="form-card" method="post" action="${ctx}/schedule/${isEdit ? 'update' : 'create'}">
  <input type="hidden" name="teamId" value="${tid}">
  <c:if test="${isEdit}"><input type="hidden" name="scheduleId" value="${scheduleId}"></c:if>

  <div class="field">
    <label for="t1">제목</label>
    <input id="t1" name="title" type="text" placeholder="예) 정기 스터디" value="<c:out value='${form.title}'/>" required>
    <c:if test="${not empty errors.title}"><div class="hint" style="color:#b3261e"><c:out value="${errors.title}"/></div></c:if>
  </div>

  <div class="form-grid">
    <div class="field">
      <label for="t2">일정 유형</label>
      <select id="t2" name="scheduleType">
        <c:forEach var="o" items="${fn:split('REGULAR:정기,MAKEUP:보강,SPECIAL:특강·행사,ETC:기타', ',')}">
          <c:set var="v" value="${fn:substringBefore(o, ':')}"/>
          <option value="${v}" ${form.scheduleType.toString() eq v ? 'selected' : ''}>${fn:substringAfter(o, ':')}</option>
        </c:forEach>
      </select>
    </div>
    <div class="field">
      <label for="t3">반복 규칙 <span class="opt">(선택)</span></label>
      <select id="t3" name="recurrenceRule">
        <c:forEach var="o" items="${fn:split(':반복 없음,WEEKLY:매주,BIWEEKLY:격주,MONTHLY:매월', ',')}">
          <c:set var="v" value="${fn:substringBefore(o, ':')}"/>
          <option value="${v}" ${form.recurrenceRule eq v ? 'selected' : ''}>${fn:substringAfter(o, ':')}</option>
        </c:forEach>
      </select>
    </div>
  </div>

  <div class="form-grid">
    <div class="field">
      <label for="t4">시작 일시</label>
      <input id="t4" name="startAt" type="datetime-local" value="${fn:substring(form.startAt, 0, 16)}" required>
      <c:if test="${not empty errors.startAt}"><div class="hint" style="color:#b3261e"><c:out value="${errors.startAt}"/></div></c:if>
    </div>
    <div class="field">
      <label for="t5">종료 일시</label>
      <input id="t5" name="endAt" type="datetime-local" value="${fn:substring(form.endAt, 0, 16)}" required>
      <c:if test="${not empty errors.endAt}"><div class="hint" style="color:#b3261e"><c:out value="${errors.endAt}"/></div></c:if>
    </div>
  </div>

  <div class="field">
    <label for="t6">장소</label>
    <input id="t6" name="location" type="text" placeholder="예) 도서관 스터디룸 · 온라인 링크" value="<c:out value='${form.location}'/>">
  </div>

  <div class="btn-row end">
    <a class="btn outline" href="${ctx}/schedule/list?teamId=${tid}">취소</a>
    <button type="submit" class="btn">저장</button>
  </div>
</form>

<script>
  document.querySelector('form.form-card').addEventListener('submit', function (e) {
    var a = this.startAt.value, b = this.endAt.value;
    if (a && b && a >= b) { e.preventDefault(); alert('종료 일시가 시작 일시보다 늦어야 해요.'); }
  });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
