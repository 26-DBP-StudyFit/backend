<%--
  04-2 스터디 타입 검사 결과   POST /profile/survey 성공 시 forward (UC-A-02, 성공 화면은 forward 기준)
  모델 속성: studyType:StudyType(typeCode, typeName, description),
             studyProfile:StudyProfile(studyTime, studyMethod, planningStyle, communicationStyle, studyIntensity, activityMode)
  화면의 '밤형 · 계획형 · 독립' 태그는 studyProfile 의 시간·계획·방식 응답에서 만든다.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="스터디 타입 검사 결과"/>
  <jsp:param name="nav" value="none"/>
  <jsp:param name="sub" value="회원가입 완료 · 스터디 타입 검사 결과"/>
  <jsp:param name="size" value="narrow"/>
</jsp:include>

<section class="card row-card" style="padding:28px;gap:24px">
  <div class="avatar lg" aria-hidden="true"></div>
  <div class="col" style="gap:8px;flex:1 1 300px">
    <div class="sub-text">나의 Study Type</div>
    <h1 style="font-size:24px">당신의 Study Type은 <span style="color:var(--primary)">‘<c:out value="${studyType.typeName}"/>’</span>입니다.</h1>
    <p class="sub-text" style="font-size:15px"><c:out value="${studyType.description}"/></p>
    <div class="row" style="gap:8px">
      <span class="chip soft"><c:out value="${ansKo[studyProfile.studyTime.name()]}" default="${studyProfile.studyTime}"/></span>
      <span class="chip soft"><c:out value="${ansKo[studyProfile.planningStyle.name()]}" default="${studyProfile.planningStyle}"/></span>
      <span class="chip soft"><c:out value="${ansKo[studyProfile.studyMethod.name()]}" default="${studyProfile.studyMethod}"/></span>
      <span class="chip dashed">유형 코드 <c:out value="${studyType.typeCode}"/></span>
    </div>
  </div>
</section>

<section class="card">
  <div class="row between">
    <div class="card-title">나의 Study Profile</div>
    <div class="sub-text">마이페이지에서 다시 검사할 수 있어요.</div>
  </div>
  <div class="grid2">
    <div class="col" style="gap:0">
      <div style="font-weight:500;margin-bottom:4px">Study Type에 반영된 응답</div>
      <div class="kv"><span>선호 학습 시간</span><span><c:out value="${ansKo[studyProfile.studyTime.name()]}" default="${studyProfile.studyTime}"/></span></div>
      <div class="kv"><span>학습 방식</span><span><c:out value="${ansKo[studyProfile.studyMethod.name()]}" default="${studyProfile.studyMethod}"/></span></div>
      <div class="kv"><span>계획 성향</span><span><c:out value="${ansKo[studyProfile.planningStyle.name()]}" default="${studyProfile.planningStyle}"/></span></div>
    </div>
    <div class="col" style="gap:0">
      <div style="font-weight:500;margin-bottom:4px">팀 추천에 활용되는 응답</div>
      <div class="kv"><span>소통 빈도</span><span><c:out value="${ansKo[studyProfile.communicationStyle.name()]}" default="${studyProfile.communicationStyle}"/></span></div>
      <div class="kv"><span>학습 강도</span><span><c:out value="${ansKo[studyProfile.studyIntensity.name()]}" default="${studyProfile.studyIntensity}"/></span></div>
      <div class="kv"><span>활동 방식</span><span><c:out value="${ansKo[studyProfile.activityMode.name()]}" default="${studyProfile.activityMode}"/></span></div>
    </div>
  </div>
</section>

<div class="row between">
  <a class="btn outline" href="${ctx}/profile/survey/retake">다시 검사하기</a>
  <div class="btn-row">
    <a class="btn outline" href="${ctx}/recommendations/teams/form">나와 맞는 팀 찾기</a>
    <a class="btn" href="${ctx}/main">홈으로 가기</a>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
