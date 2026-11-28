<%--
  07 팀 생성 (UC-B-01)   GET /teams/new  →  TeamController#showTeamCreateForm   (form:TeamCreateForm, 빈 폼)
                         POST /teams      →  성공: 팀 홈으로 redirect,  실패: 이 화면 (createFailed, errorMessage, errors, form)
  전송 파라미터: title, studyCategory, recruitmentCount, regionCode, meetingMode, requiredTemperature,
                 preferredDays(여러 개), preferredStartTime, preferredEndTime, description(모집 소개글), goal(활동 목표), image(선택)
  ※ 대표 사진 때문에 enctype=multipart/form-data (서버에서 MultipartFile 처리, 안 쓰면 image input 삭제).
  ※ studyCategory / regionCode / meetingMode 의 value 는 예시값(CERTIFICATE…, SEOUL…, OFFLINE/ONLINE). enum·코드에 맞게 수정.
  ※ 대면(OFFLINE)이면 활동 지역 필수 (FR-B-01) — 브라우저에서도 required 로 막는다.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀 생성"/>
  <jsp:param name="size" value="narrow"/>
</jsp:include>

<h1>팀 생성</h1>

<form class="form-card" method="post" action="${ctx}/teams" enctype="multipart/form-data" id="teamForm">
  <div class="field">
    <label for="title">팀 제목</label>
    <input id="title" name="title" type="text" placeholder="팀 제목을 입력해주세요." value="<c:out value='${form.title}'/>" required>
  </div>

  <div class="form-grid">
    <div class="field">
      <label for="cat">팀 카테고리</label>
      <select id="cat" name="studyCategory" required>
        <c:forEach var="o" items="${fn:split('CERTIFICATE:자격증,JOB:취업 준비,CONTEST:대회 준비,MAJOR:전공 공부,LANGUAGE:어학,CIVIL_SERVICE:공무원 시험,ENTRANCE:입시,READING:독서,ETC:기타', ',')}">
          <c:set var="v" value="${fn:substringBefore(o, ':')}"/>
          <option value="${v}" ${form.studyCategory.toString() eq v ? 'selected' : ''}>${fn:substringAfter(o, ':')}</option>
        </c:forEach>
      </select>
    </div>
    <div class="field">
      <label for="max">모집 인원 (최대 인원)</label>
      <input id="max" name="recruitmentCount" type="number" min="2" placeholder="예: 5" value="${form.recruitmentCount}" required>
    </div>
  </div>

  <div class="form-grid">
    <div class="field">
      <label for="region">활동 지역</label>
      <select id="region" name="regionCode">
        <c:forEach var="o" items="${fn:split('SEOUL:서울,BUSAN:부산,DAEJEON:대전,DAEGU:대구,INCHEON:인천,GWANGJU:광주,ETC:기타', ',')}">
          <c:set var="v" value="${fn:substringBefore(o, ':')}"/>
          <option value="${v}" ${form.regionCode eq v ? 'selected' : ''}>${fn:substringAfter(o, ':')}</option>
        </c:forEach>
      </select>
    </div>
    <fieldset>
      <legend>진행 방식</legend>
      <div class="check-group" style="min-height:46px;align-items:center">
        <label class="check"><input type="radio" name="meetingMode" value="OFFLINE" ${empty form.meetingMode or form.meetingMode.toString() eq 'OFFLINE' ? 'checked' : ''}>대면</label>
        <label class="check"><input type="radio" name="meetingMode" value="ONLINE" ${form.meetingMode.toString() eq 'ONLINE' ? 'checked' : ''}>비대면</label>
      </div>
    </fieldset>
  </div>

  <div class="field">
    <label for="temp">필요한 온도 (가입 가능한 지원자 온도)</label>
    <input id="temp" name="requiredTemperature" type="number" step="0.1" placeholder="예: 36.5" value="${form.requiredTemperature}">
  </div>

  <div class="form-grid">
    <fieldset>
      <legend>선호 요일</legend>
      <div class="check-group">
        <c:forEach var="o" items="${fn:split('SUNDAY:일,MONDAY:월,TUESDAY:화,WEDNESDAY:수,THURSDAY:목,FRIDAY:금,SATURDAY:토', ',')}">
          <c:set var="v" value="${fn:substringBefore(o, ':')}"/>
          <%-- fn:contains 는 Set<DayOfWeek> 의 문자열 표현("[MONDAY, ...]")에서 값을 찾는다 --%>
          <label class="check"><input type="checkbox" name="preferredDays" value="${v}" ${fn:contains(form.preferredDays, v) ? 'checked' : ''}>${fn:substringAfter(o, ':')}</label>
        </c:forEach>
      </div>
    </fieldset>
    <fieldset>
      <legend>선호 시간대</legend>
      <div class="row" style="gap:8px;flex-wrap:nowrap">
        <label for="t1" class="sr-only">시작 시간</label>
        <select id="t1" name="preferredStartTime">
          <c:forEach var="h" begin="9" end="21" step="2">
            <c:set var="v" value="${h < 10 ? '0' : ''}${h}:00"/>
            <option value="${v}" ${fn:substring(form.preferredStartTime, 0, 5) eq v ? 'selected' : ''}>${v}</option>
          </c:forEach>
        </select>
        <span>~</span>
        <label for="t2" class="sr-only">끝 시간</label>
        <select id="t2" name="preferredEndTime">
          <c:forEach var="h" begin="11" end="23" step="2">
            <c:set var="v" value="${h}:00"/>
            <option value="${v}" ${fn:substring(form.preferredEndTime, 0, 5) eq v ? 'selected' : ''}>${v}</option>
          </c:forEach>
        </select>
      </div>
    </fieldset>
  </div>

  <div class="form-grid">
    <div class="field">
      <label for="intro">모집 소개글</label>
      <textarea id="intro" name="description" placeholder="마음을 사로잡을 소개글을 적어주세요."><c:out value="${param.description}"/></textarea>
    </div>
    <div class="field">
      <label for="image">팀 대표 사진 (선택)</label>
      <div class="thumb" style="width:100%;height:110px">기본 이미지 · 사진을 추가하지 않으면 사용돼요</div>
      <input id="image" name="image" type="file" accept="image/*" style="font:inherit;font-size:14px">
    </div>
  </div>

  <div class="field">
    <label for="goal">활동 목표</label>
    <textarea id="goal" name="goal" placeholder="팀이 함께 이루고 싶은 활동 목표를 적어주세요."><c:out value="${param.goal}"/></textarea>
  </div>

  <div class="btn-row end">
    <a class="btn outline" href="${ctx}/main">닫기</a>
    <button type="submit" class="btn">등록</button>
  </div>
</form>

<script>
  (function () {
    var f = document.getElementById('teamForm');
    function sync() {
      var offline = f.meetingMode.value === 'OFFLINE';
      f.regionCode.required = offline;
    }
    f.querySelectorAll('input[name=meetingMode]').forEach(function (r) { r.addEventListener('change', sync); });
    f.addEventListener('submit', function (e) {
      if (f.preferredStartTime.value >= f.preferredEndTime.value) {
        e.preventDefault();
        alert('끝 시간이 시작 시간보다 늦어야 해요.');
      }
    });
    sync();
  })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
