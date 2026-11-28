<%--
  01 도입 화면   GET /   (설계서에 URI 없음 → 임의 지정)
  모델 속성: 없음
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="나와 맞는 스터디"/>
  <jsp:param name="nav" value="none"/>
  <jsp:param name="login" value="true"/>
</jsp:include>

<section class="hero">
  <div class="col" style="gap:16px">
    <span class="sub-text">Team = FiT (Fit Team)</span>
    <div>
      <h1>나와 맞는 스터디,</h1>
      <h1>Study FIT</h1>
    </div>
    <p class="sub-text" style="font-size:16px">스터디 타입 검사로 나의 공부 스타일을 알아보고, 나와 잘 맞는 팀을 찾아 함께 공부해요.</p>
    <div><a class="btn" href="${ctx}/user/login">Study FIT 시작하기!</a></div>
  </div>
  <div class="art">도입 화면 일러스트 / 이미지</div>
</section>

<section class="grid3">
  <div class="card">
    <div class="card-title">스터디 타입 검사</div>
    <div class="sub-text">6가지 질문으로 나의 학습 시간대, 방식, 강도를 확인해요.</div>
  </div>
  <div class="card">
    <div class="card-title">나의 FiT 찾기</div>
    <div class="sub-text">나와 잘 맞는 팀을 5위까지 추천받고 마음에 드는 팀은 찜해요.</div>
  </div>
  <div class="card">
    <div class="card-title">일정 조율 · 출석 체크</div>
    <div class="sub-text">팀원들과 가능한 시간을 맞추고 출석까지 한곳에서 관리해요.</div>
  </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
