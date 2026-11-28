<%--
  04-1 스터디 타입 검사 (6문항)
    UC-A-02  GET  /profile/survey          →  StudyProfileController#showSurveyForm   (가입 직후)
             POST /profile/survey          →  #submitSurvey   성공: joinComplete.jsp forward / 실패: 이 화면 (surveyFailed)
    UC-A-04  GET  /profile/survey/retake   →  #showRetakeForm (기존 응답 표시)
             POST /profile/survey/retake   →  #submitRetake   성공: redirect /mypage
  전송 파라미터: userId, studyTime, studyMethod, planningStyle, communicationStyle, studyIntensity, activityMode
  모델 속성: studyProfile:StudyProfile (재검사일 때 기존 응답), surveyFailed:boolean
  ※ <form> 에 action 이 없어서 '현재 URL'로 POST 한다 (가입 직후 / 재검사 둘 다 같은 JSP).
  ※ radio value 는 enum 상수명과 같아야 한다. 아래는 예시값(MORNING/NIGHT, TOGETHER/INDEPENDENT, PLANNED/FLEXIBLE,
     FREQUENT/OCCASIONAL, HIGH/LOW, OFFLINE/ONLINE) — 실제 enum 에 맞게 수정.
  ※ Study Type 에는 시간·방식·계획 3문항만 반영, 소통·강도·활동 방식 3문항은 팀 추천에만 사용.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<c:set var="retake" value="${not empty studyProfile}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="스터디 타입 검사"/>
  <jsp:param name="nav" value="none"/>
  <jsp:param name="sub" value="${retake ? '재검사' : '회원가입 2 / 2 · 스터디 타입 검사'}"/>
</jsp:include>

<c:set var="uid" value="${empty sessionScope.user ? param.userId : sessionScope.user.userId}"/>

<%-- 문항 정의: 질문 번호별로 (필드명, 라벨, 질문, 보기 2개 "값|제목|설명") --%>
<form method="post" id="surveyForm" class="col" style="gap:24px">
  <input type="hidden" name="userId" value="<c:out value='${uid}'/>">

  <section class="card" style="gap:16px">
    <div class="row between">
      <div class="col" style="gap:4px">
        <h1>스터디 타입 검사</h1>
        <div class="sub-text">6가지 질문에 답하면 나의 Study Type을 알려드려요. 가장 가까운 것 하나씩 골라주세요.</div>
      </div>
      <div style="font-size:15px">답변 <b id="cnt" style="color:var(--primary)">0 / 6</b></div>
    </div>
    <div class="bar"><i id="barFill" style="width:0"></i></div>
  </section>

  <c:if test="${surveyFailed}">
    <div class="alert error" role="alert">검사 결과를 저장하지 못했어요. 모든 문항에 답했는지 확인한 뒤 다시 시도해주세요.</div>
  </c:if>

  <div class="grid2">
    <section class="card q" style="gap:12px">
      <div class="row" style="gap:8px"><span class="q-no">1</span><span class="sub-text">선호 학습 시간</span></div>
      <h2>주로 언제 공부할 때 집중이 잘 되나요?</h2>
      <label class="choice"><input type="radio" name="studyTime" value="MORNING" ${studyProfile.studyTime.toString() eq 'MORNING' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>아침형</b><small>오전에 집중이 잘 돼요</small></span></span></label>
      <label class="choice"><input type="radio" name="studyTime" value="NIGHT" ${studyProfile.studyTime.toString() eq 'NIGHT' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>밤형</b><small>늦은 시간에 집중이 잘 돼요</small></span></span></label>
    </section>

    <section class="card q" style="gap:12px">
      <div class="row" style="gap:8px"><span class="q-no">2</span><span class="sub-text">학습 방식</span></div>
      <h2>어떤 방식으로 공부하는 게 더 잘 맞나요?</h2>
      <label class="choice"><input type="radio" name="studyMethod" value="TOGETHER" ${studyProfile.studyMethod.toString() eq 'TOGETHER' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>함께</b><small>같은 시간에 모여 함께 공부해요</small></span></span></label>
      <label class="choice"><input type="radio" name="studyMethod" value="INDEPENDENT" ${studyProfile.studyMethod.toString() eq 'INDEPENDENT' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>독립</b><small>각자 공부하고 필요할 때 공유해요</small></span></span></label>
    </section>

    <section class="card q" style="gap:12px">
      <div class="row" style="gap:8px"><span class="q-no">3</span><span class="sub-text">계획 성향</span></div>
      <h2>공부 계획은 어떻게 세우는 편인가요?</h2>
      <label class="choice"><input type="radio" name="planningStyle" value="PLANNED" ${studyProfile.planningStyle.toString() eq 'PLANNED' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>계획형</b><small>일정과 분량을 미리 정해요</small></span></span></label>
      <label class="choice"><input type="radio" name="planningStyle" value="FLEXIBLE" ${studyProfile.planningStyle.toString() eq 'FLEXIBLE' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>유연형</b><small>상황에 맞춰 유연하게 조절해요</small></span></span></label>
    </section>

    <section class="card q" style="gap:12px">
      <div class="row" style="gap:8px"><span class="q-no">4</span><span class="sub-text">소통 빈도</span></div>
      <h2>팀원들과 얼마나 자주 소통하고 싶나요?</h2>
      <label class="choice"><input type="radio" name="communicationStyle" value="FREQUENT" ${studyProfile.communicationStyle.toString() eq 'FREQUENT' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>자주</b><small>자주 대화하고 활발하게 공유해요</small></span></span></label>
      <label class="choice"><input type="radio" name="communicationStyle" value="OCCASIONAL" ${studyProfile.communicationStyle.toString() eq 'OCCASIONAL' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>가끔</b><small>필요할 때 가끔 연락해요</small></span></span></label>
    </section>

    <section class="card q" style="gap:12px">
      <div class="row" style="gap:8px"><span class="q-no">5</span><span class="sub-text">학습 강도</span></div>
      <h2>어느 정도 강도로 공부하고 싶나요?</h2>
      <label class="choice"><input type="radio" name="studyIntensity" value="HIGH" ${studyProfile.studyIntensity.toString() eq 'HIGH' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>높음</b><small>몰입해서 높은 강도로 진행해요</small></span></span></label>
      <label class="choice"><input type="radio" name="studyIntensity" value="LOW" ${studyProfile.studyIntensity.toString() eq 'LOW' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>낮음</b><small>부담 없이 여유롭게 진행해요</small></span></span></label>
    </section>

    <section class="card q" style="gap:12px">
      <div class="row" style="gap:8px"><span class="q-no">6</span><span class="sub-text">활동 방식</span></div>
      <h2>스터디는 어떤 방식으로 하고 싶나요?</h2>
      <label class="choice"><input type="radio" name="activityMode" value="OFFLINE" ${studyProfile.activityMode.toString() eq 'OFFLINE' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>대면</b><small>직접 만나서 함께 공부해요</small></span></span></label>
      <label class="choice"><input type="radio" name="activityMode" value="ONLINE" ${studyProfile.activityMode.toString() eq 'ONLINE' ? 'checked' : ''}><span class="box"><span class="radio"></span><span><b>비대면</b><small>온라인으로 편하게 공부해요</small></span></span></label>
    </section>
  </div>

  <div class="row between">
    <a class="btn outline" href="${ctx}${retake ? '/mypage' : '/user/join'}">이전</a>
    <div class="row">
      <span class="sub-text" id="hint">아직 답하지 않은 질문이 6개 있어요</span>
      <button type="submit" class="btn" id="submitBtn">결과 보기</button>
    </div>
  </div>
</form>

<script>
  (function () {
    var cards = document.querySelectorAll('.q');
    var names = ['studyTime', 'studyMethod', 'planningStyle', 'communicationStyle', 'studyIntensity', 'activityMode'];
    var form = document.getElementById('surveyForm');
    function update() {
      var n = 0;
      cards.forEach(function (c, i) {
        var done = form.querySelector('input[name=' + names[i] + ']:checked') !== null;
        c.querySelector('.q-no').classList.toggle('done', done);
        if (done) n++;
      });
      document.getElementById('cnt').textContent = n + ' / 6';
      document.getElementById('barFill').style.width = Math.round(n * 100 / 6) + '%';
      document.getElementById('hint').textContent = n === 6 ? '모든 질문에 답했어요' : '아직 답하지 않은 질문이 ' + (6 - n) + '개 있어요';
    }
    form.addEventListener('change', update);
    form.addEventListener('submit', function (e) {
      for (var i = 0; i < names.length; i++) {
        if (!form.querySelector('input[name=' + names[i] + ']:checked')) {
          e.preventDefault();
          cards[i].scrollIntoView({ behavior: 'smooth', block: 'center' });
          document.getElementById('hint').textContent = (i + 1) + '번 질문에 답해주세요';
          return;
        }
      }
    });
    update();
  })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
