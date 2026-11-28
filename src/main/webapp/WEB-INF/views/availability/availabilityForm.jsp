<%--
  19 활동 가능 시간 등록·수정 (UC-D-01)
    GET  /availability/edit?teamId=   →  AvailabilityController#showAvailabilityForm   (기존 등록값 표시, 없으면 빈 폼 1줄)
    POST /availability/save           →  #saveAvailability   성공: redirect /availability/edit?teamId=,  입력 오류: 이 화면 forward
  전송 파라미터: teamId, slots[i].dayOfWeek, slots[i].startTime, slots[i].endTime   (slots:List<AvailabilityInput>)
  모델 속성: teamId:Long, team:Team (제목 표시용, 추가 속성), form:AvailabilityForm (slots 목록), errorMessage (오류 시)
  ※ 행 추가/삭제 때마다 name 의 인덱스를 0부터 다시 매겨서 List 바인딩이 끊기지 않게 한다.
  ※ 오른쪽 '주간 미리보기' 는 화면에서 고른 값으로 브라우저가 바로 다시 그린다 (서버 계산 아님).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="활동 가능 시간 등록·수정"/>
  <jsp:param name="active" value="home"/>
</jsp:include>

<c:set var="tid" value="${empty teamId ? param.teamId : teamId}"/>

<div class="page-head">
  <a class="back" href="${ctx}/teams/${tid}/home"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>활동 가능 시간 등록 · 수정</h1>
  <c:if test="${not empty team}"><span class="chip"><c:out value="${team.title}"/></span></c:if>
</div>

<div class="split">
  <form class="card grow" id="availForm" method="post" action="${ctx}/availability/save" style="gap:16px">
    <input type="hidden" name="teamId" value="${tid}">
    <div class="col" style="gap:4px">
      <div class="card-title">내 활동 가능 시간</div>
      <div class="sub-text">로그인한 내 시간만 저장돼요. 저장하면 이 화면으로 다시 돌아와요.</div>
    </div>

    <div id="rows" class="col" style="gap:10px">
      <c:forEach var="s" items="${form.slots}">
        <div class="slot-row">
          <select name="slots[0].dayOfWeek" aria-label="요일">
            <c:forEach var="d" items="${fn:split('MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY', ',')}">
              <option value="${d}" ${s.dayOfWeek.toString() eq d ? 'selected' : ''}>${dayKo[d]}요일</option>
            </c:forEach>
          </select>
          <select name="slots[0].startTime" aria-label="시작 시간">
            <c:forEach var="h" begin="6" end="22">
              <c:set var="v" value="${h < 10 ? '0' : ''}${h}:00"/>
              <option value="${v}" ${fn:substring(s.startTime, 0, 5) eq v ? 'selected' : ''}>${v}</option>
            </c:forEach>
          </select>
          <span>~</span>
          <select name="slots[0].endTime" aria-label="종료 시간">
            <c:forEach var="h" begin="7" end="23">
              <c:set var="v" value="${h < 10 ? '0' : ''}${h}:00"/>
              <option value="${v}" ${fn:substring(s.endTime, 0, 5) eq v ? 'selected' : ''}>${v}</option>
            </c:forEach>
          </select>
          <button type="button" class="btn gray sm" data-del>삭제</button>
        </div>
      </c:forEach>
    </div>

    <div><button type="button" class="btn outline sm" id="addRow">+ 시간대 추가</button></div>
    <div class="divider"></div>
    <div class="row between">
      <a class="link" href="${ctx}/availability/common?teamId=${tid}">팀 공통 시간 보기</a>
      <button type="submit" class="btn">저장</button>
    </div>
  </form>

  <section class="card" style="flex:1 1 380px;gap:12px">
    <div class="row between">
      <div class="card-title">주간 미리보기</div>
      <div class="legend"><span>내 가능 시간</span></div>
    </div>
    <div id="preview" class="week"></div>
  </section>
</div>

<%-- 새 행 템플릿 (JS 가 복제) --%>
<template id="rowTpl">
  <div class="slot-row">
    <select name="slots[0].dayOfWeek" aria-label="요일">
      <c:forEach var="d" items="${fn:split('MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY', ',')}"><option value="${d}">${dayKo[d]}요일</option></c:forEach>
    </select>
    <select name="slots[0].startTime" aria-label="시작 시간">
      <c:forEach var="h" begin="6" end="22"><c:set var="v" value="${h < 10 ? '0' : ''}${h}:00"/><option value="${v}" ${h eq 18 ? 'selected' : ''}>${v}</option></c:forEach>
    </select>
    <span>~</span>
    <select name="slots[0].endTime" aria-label="종료 시간">
      <c:forEach var="h" begin="7" end="23"><c:set var="v" value="${h < 10 ? '0' : ''}${h}:00"/><option value="${v}" ${h eq 20 ? 'selected' : ''}>${v}</option></c:forEach>
    </select>
    <button type="button" class="btn gray sm" data-del>삭제</button>
  </div>
</template>

<script>
  (function () {
    var rows = document.getElementById('rows');
    var tpl = document.getElementById('rowTpl');
    var preview = document.getElementById('preview');
    var DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
    var LABEL = ['월', '화', '수', '목', '금', '토', '일'];

    function reindex() {
      Array.prototype.forEach.call(rows.children, function (row, i) {
        Array.prototype.forEach.call(row.querySelectorAll('select'), function (sel) {
          sel.name = sel.name.replace(/slots\[\d+\]/, 'slots[' + i + ']');
        });
      });
    }
    function hourOf(v) { return parseInt(String(v).substring(0, 2), 10); }

    function draw() {
      var covered = {};
      Array.prototype.forEach.call(rows.children, function (row) {
        var sel = row.querySelectorAll('select');
        var d = DAYS.indexOf(sel[0].value), s = hourOf(sel[1].value), e = hourOf(sel[2].value);
        for (var h = s; h < e; h++) covered[d + '-' + h] = true;
      });
      var html = '<div></div>' + LABEL.map(function (l) { return '<div class="h">' + l + '</div>'; }).join('');
      for (var h = 9; h <= 20; h++) {
        html += '<div class="t">' + (h < 10 ? '0' : '') + h + '</div>';
        for (var d = 0; d < 7; d++) html += '<div class="c">' + (covered[d + '-' + h] ? '<i class="mine"></i>' : '') + '</div>';
      }
      preview.innerHTML = html;
    }
    function add() {
      rows.appendChild(tpl.content.cloneNode(true));
      reindex(); draw();
    }

    document.getElementById('addRow').addEventListener('click', add);
    rows.addEventListener('click', function (e) {
      if (e.target.matches('[data-del]')) { e.target.closest('.slot-row').remove(); reindex(); draw(); }
    });
    rows.addEventListener('change', draw);
    document.getElementById('availForm').addEventListener('submit', function (e) {
      var ok = true;
      Array.prototype.forEach.call(rows.children, function (row) {
        var sel = row.querySelectorAll('select');
        if (hourOf(sel[1].value) >= hourOf(sel[2].value)) ok = false;
      });
      if (!ok) { e.preventDefault(); alert('끝 시간이 시작 시간보다 늦어야 해요.'); }
    });

    if (rows.children.length === 0) add(); else { reindex(); draw(); }
  })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
