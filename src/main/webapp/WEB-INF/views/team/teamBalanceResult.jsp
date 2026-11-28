<%--
  18 팀 구성 균형 분석 — 결과 (UC-C-06)   POST /leader/teams/{teamId}/balance 성공 시 forward
  모델 속성: balanceResult:TeamBalanceResult (team, candidate(없으면 null), balanceScore, summary),
             balanceItems:List<BalanceItem> (criterion, score, description),
             team:Team, memberCount:int (추가, 현재 인원)
  BalanceItem 에 아래 값이 필요 (도메인에 없으면 추가 — 화면의 '아침형 1 / 밤형 3' 막대용):
     labelA, labelB      두 선택지 이름 (예: 아침형 / 밤형)
     currentA, currentB  현재 팀 인원 수
     afterA, afterB      후보 합류 후 인원 수 (후보 없으면 0 또는 null → 열 숨김)
  판정: score >= 70 이면 '균형', 아니면 '쏠림' (기준값은 서버 정책에 맞게 수정)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀 균형 분석 결과"/>
</jsp:include>

<c:set var="team" value="${balanceResult.team}"/>
<c:set var="cand" value="${balanceResult.candidate}"/>
<c:set var="hasCand" value="${not empty cand}"/>
<c:set var="againUrl" value="${ctx}/leader/teams/${team.teamId}/balance/form"/>
<c:if test="${hasCand}"><c:set var="againUrl" value="${againUrl}?candidateId=${cand.userId}"/></c:if>

<div class="page-head">
  <a class="back" href="${againUrl}"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>팀 균형 분석 결과</h1>
  <span class="chip"><c:out value="${team.title}"/></span>
</div>

<section class="card row-card" style="gap:16px">
  <div class="col grow" style="gap:4px;flex:1 1 300px">
    <div class="card-title" style="font-size:18px">
      <c:choose>
        <c:when test="${hasCand}"><c:out value="${cand.name}"/> 님이 합류하면 팀 균형 점수는 <fmt:formatNumber value="${balanceResult.balanceScore}" maxFractionDigits="0"/>점이에요</c:when>
        <c:otherwise>현재 팀의 균형 점수는 <fmt:formatNumber value="${balanceResult.balanceScore}" maxFractionDigits="0"/>점이에요</c:otherwise>
      </c:choose>
    </div>
    <div class="sub-text"><c:out value="${balanceResult.summary}"/></div>
  </div>
  <span class="chip ${hasCand ? '' : 'soft'}">${hasCand ? '후보 포함 분석' : '현재 팀만 분석'}</span>
</section>

<section class="card" style="gap:12px">
  <div class="row between">
    <div class="card-title">항목별 균형</div>
    <div class="legend"><span>앞 항목</span><span class="team">뒤 항목</span></div>
  </div>

  <div style="overflow-x:auto">
    <div class="col" style="gap:0;min-width:560px">
      <div class="crit" style="grid-template-columns:130px 1fr ${hasCand ? '1fr' : ''} 64px;color:var(--muted);font-size:14px">
        <div>항목</div>
        <div>현재 팀 (${memberCount}명)</div>
        <c:if test="${hasCand}"><div>후보 합류 후 (${memberCount + 1}명)</div></c:if>
        <div style="text-align:center">판정</div>
      </div>

      <c:forEach var="it" items="${balanceItems}">
        <c:set var="curTotal" value="${it.currentA + it.currentB}"/>
        <c:set var="aftTotal" value="${it.afterA + it.afterB}"/>
        <div class="crit" style="grid-template-columns:130px 1fr ${hasCand ? '1fr' : ''} 64px">
          <div class="name"><c:out value="${critKo[it.criterion.name()]}" default="${it.criterion}"/></div>
          <div>
            <div class="row" style="gap:0;flex-wrap:nowrap;height:28px;border-radius:6px;overflow:hidden;font-size:12px">
              <div style="flex:${it.currentA} 1 0;background:var(--primary);color:#fff;display:flex;align-items:center;padding:0 8px;white-space:nowrap">${it.labelA} ${it.currentA}</div>
              <div style="flex:${it.currentB} 1 0;background:var(--bar);color:var(--text);display:flex;align-items:center;padding:0 8px;white-space:nowrap">${it.labelB} ${it.currentB}</div>
            </div>
          </div>
          <c:if test="${hasCand}">
            <div>
              <div class="row" style="gap:0;flex-wrap:nowrap;height:28px;border-radius:6px;overflow:hidden;font-size:12px">
                <div style="flex:${it.afterA} 1 0;background:var(--primary);color:#fff;display:flex;align-items:center;padding:0 8px;white-space:nowrap">${it.labelA} ${it.afterA}</div>
                <div style="flex:${it.afterB} 1 0;background:var(--bar);color:var(--text);display:flex;align-items:center;padding:0 8px;white-space:nowrap">${it.labelB} ${it.afterB}</div>
              </div>
            </div>
          </c:if>
          <div style="text-align:center">
            <c:choose>
              <c:when test="${it.score >= 70}"><span class="chip soft">균형</span></c:when>
              <c:otherwise><span class="chip dashed">쏠림</span></c:otherwise>
            </c:choose>
          </div>
        </div>
        <c:if test="${not empty it.description}"><div class="note sub-text" style="font-size:13px;padding:0 0 8px"><c:out value="${it.description}"/></div></c:if>
      </c:forEach>
    </div>
  </div>
</section>

<div class="row between">
  <a class="btn outline" href="${againUrl}">다시 분석</a>
  <c:choose>
    <c:when test="${hasCand}"><a class="btn" href="${ctx}/my/applications">신청 승인하러 가기</a></c:when>
    <c:otherwise><a class="btn" href="${ctx}/teams/${team.teamId}/home">팀 홈으로</a></c:otherwise>
  </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
