<%--
  16 가입 신청 상세 (UC-B-03/04)   GET /teams/{teamId}/applications/{applicationId}  →  JoinApplicationController#showApplicationDetail
  승인/거절  POST .../approve | .../reject  →  성공: 팀 홈으로 redirect,  실패: 이 화면 (application, errorMessage)
  후보 적합도  GET /leader/teams/{teamId}/candidates/{candidateId}/match   (UC-C-04)
  모델 속성: application:ApplicationDetailDto
    applicationId, teamId, teamTitle, applicantId, applicantName, applicantTemperature(선택), createdAt, status(PENDING/…),
    studyProfile:StudyProfile (studyType 포함), matchPercent(우리 팀과의 FiT 일치도, 0~100)
  승인·거절 권한은 팀장 — 서버(Manager)에서 검증.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="가입 신청 상세"/>
</jsp:include>

<c:set var="sp" value="${application.studyProfile}"/>

<div class="page-head">
  <a class="back" href="${ctx}/my/applications"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>돌아가기</a>
  <h1>가입 신청 상세</h1>
  <span class="chip"><c:out value="${application.teamTitle}"/></span>
</div>

<div class="split">
  <section class="card" style="flex:0 1 300px;gap:16px">
    <div class="card-title">프로필</div>
    <div class="row" style="gap:12px">
      <div class="avatar lg" aria-hidden="true"></div>
      <div style="font-size:18px;font-weight:700"><c:out value="${application.applicantName}"/></div>
    </div>
    <c:if test="${not empty application.applicantTemperature}">
      <div class="col" style="gap:6px">
        <div class="row between"><span class="sub-text"><c:out value="${application.applicantName}"/> 님 온도</span><b style="color:var(--primary)">${application.applicantTemperature}°C</b></div>
        <div class="bar"><i style="width:${application.applicantTemperature > 100 ? 100 : application.applicantTemperature}%"></i></div>
      </div>
    </c:if>
    <div class="divider"></div>
    <div class="col" style="gap:4px">
      <div class="sub-text">Study Type</div>
      <div style="font-size:18px;font-weight:700"><c:out value="${sp.studyType.typeName}"/> (<c:out value="${sp.studyType.typeCode}"/>)</div>
      <div class="sub-text"><c:out value="${ansKo[sp.studyTime.name()]}" default="${sp.studyTime}"/> · <c:out value="${ansKo[sp.planningStyle.name()]}" default="${sp.planningStyle}"/> · <c:out value="${ansKo[sp.studyMethod.name()]}" default="${sp.studyMethod}"/></div>
    </div>
  </section>

  <div class="grow">
    <section class="card" style="gap:4px">
      <div class="card-title">신청 정보</div>
      <div class="kv"><span>신청 팀</span><span><c:out value="${application.teamTitle}"/></span></div>
      <div class="kv"><span>신청 일시</span><span>${fn:replace(fn:substring(application.createdAt, 0, 16), 'T', ' ')}</span></div>
      <div class="kv"><span>처리 상태</span><span><c:out value="${appStatusKo[application.status.name()]}" default="${application.status}"/></span></div>
    </section>

    <section class="card" style="gap:4px">
      <div class="card-title">신청자의 학습 프로필</div>
      <div class="kv"><span>선호 학습 시간</span><span><c:out value="${ansKo[sp.studyTime.name()]}" default="${sp.studyTime}"/></span></div>
      <div class="kv"><span>학습 방식</span><span><c:out value="${ansKo[sp.studyMethod.name()]}" default="${sp.studyMethod}"/></span></div>
      <div class="kv"><span>계획 성향</span><span><c:out value="${ansKo[sp.planningStyle.name()]}" default="${sp.planningStyle}"/></span></div>
      <div class="kv"><span>소통 빈도</span><span><c:out value="${ansKo[sp.communicationStyle.name()]}" default="${sp.communicationStyle}"/></span></div>
      <div class="kv"><span>학습 강도</span><span><c:out value="${ansKo[sp.studyIntensity.name()]}" default="${sp.studyIntensity}"/></span></div>
      <div class="kv"><span>활동 방식</span><span><c:out value="${ansKo[sp.activityMode.name()]}" default="${sp.activityMode}"/></span></div>
    </section>

    <section class="card">
      <div class="row between">
        <div class="col" style="gap:6px;flex:1 1 240px">
          <div class="card-title">우리 팀과의 FiT 일치도</div>
          <div class="bar"><i style="width:${application.matchPercent}%"></i></div>
        </div>
        <div class="row">
          <span class="score">${application.matchPercent}%</span>
          <a class="btn outline sm" href="${ctx}/leader/teams/${application.teamId}/candidates/${application.applicantId}/match">적합도 자세히 보기</a>
        </div>
      </div>
    </section>

    <div class="row between">
      <span class="sub-text">승인·거절은 팀장만 할 수 있어요.</span>
      <c:if test="${application.status.toString() eq 'PENDING'}">
        <div class="btn-row">
          <form class="inline" method="post" action="${ctx}/teams/${application.teamId}/applications/${application.applicationId}/reject"><button type="submit" class="btn outline">거절</button></form>
          <form class="inline" method="post" action="${ctx}/teams/${application.teamId}/applications/${application.applicationId}/approve"><button type="submit" class="btn">승인</button></form>
        </div>
      </c:if>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
