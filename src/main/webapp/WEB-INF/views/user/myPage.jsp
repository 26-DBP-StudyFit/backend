<%--
  15 마이페이지 (UC-A-05)   GET /mypage  →  MyPageController#showMyPage
  내 정보 수정 (UC-A-06)    POST /user/update (userId, name, major + 보존용 studentId, gender)
                            ※ 디자인에서 수정 가능한 항목은 이름·전공뿐. 학번·성별은 프로필에 읽기 전용으로 표시하고,
                              서버가 비어 있는 값으로 덮어쓰지 않도록 hidden 으로 그대로 다시 보낸다.
  비밀번호 변경             POST /user/update (userId, password, currentPassword)
                            ※ 디자인: '저장' → 팝업 '본인 확인(기존 비밀번호)' → '확인' 에서 전송. currentPassword 는 설계서에 없는 파라미터.
                              비밀번호만 보내면 서버는 changePassword 만 호출 (기존 비밀번호 검증 포함).
  성공: redirect /mypage (비밀번호 변경 성공이면 passwordChanged=true 로 '비밀번호를 변경했어요.' 표시),  실패: 이 화면으로 forward (updateFailed)
  회원탈퇴 실패(UC-A-07)    withdrawFailed 로 이 화면에 오류 표시
  모델 속성: user:User (name, studentId, gender, major), studyProfile:StudyProfile (studyType.typeName/typeCode),
             temperature:Double (선택, '온도' — User 도메인에 없어 별도 속성), passwordChanged / updateFailed / withdrawFailed / passwordFailed:boolean
  ※ 로그아웃 POST /user/logout 은 설계서에 없음(임의 지정).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="마이페이지"/>
</jsp:include>

<h1>마이페이지</h1>

<c:if test="${updateFailed}"><div class="alert error" role="alert">정보를 수정하지 못했어요. 입력값을 확인해주세요.</div></c:if>
<c:if test="${passwordFailed}"><div class="alert error" role="alert">기존 비밀번호가 맞지 않아 변경하지 못했어요.</div></c:if>
<c:if test="${withdrawFailed}"><div class="alert error" role="alert">비밀번호가 맞지 않아 탈퇴하지 못했어요.</div></c:if>

<div class="split">
  <aside class="side" style="flex:0 1 320px">
    <div class="card" style="gap:16px;align-items:center">
      <div class="avatar lg" style="width:136px;height:136px" aria-hidden="true"></div>
      <div class="col" style="gap:10px;width:100%;align-items:center">
        <div style="font-size:20px;font-weight:700"><c:out value="${user.name}"/></div>
        <div class="col" style="gap:0;width:100%;font-size:14px">
          <div class="kv" style="padding:4px 0;border:0"><span class="sub-text">학번</span><span style="font-weight:400"><c:out value="${user.studentId}"/></span></div>
          <div class="kv" style="padding:4px 0;border:0"><span class="sub-text">성별</span><span style="font-weight:400">
            <c:choose>
              <c:when test="${user.gender.toString() eq 'M'}">남성 (M)</c:when>
              <c:when test="${user.gender.toString() eq 'F'}">여성 (F)</c:when>
              <c:otherwise>-</c:otherwise>
            </c:choose>
          </span></div>
          <div class="kv" style="padding:4px 0;border:0"><span class="sub-text">전공</span><span style="font-weight:400"><c:out value="${empty user.major ? '-' : user.major}"/></span></div>
        </div>
      </div>

      <c:if test="${not empty temperature}">
        <div class="col" style="gap:6px;width:100%">
          <div class="row between" style="font-size:14px"><span class="sub-text"><c:out value="${user.name}"/> 님 온도</span><b style="color:var(--primary)">${temperature}°C</b></div>
          <div class="bar"><i style="width:${temperature > 100 ? 100 : temperature}%"></i></div>
        </div>
      </c:if>

      <div class="col" style="gap:8px;width:100%;padding:14px 16px;background:var(--soft);border-radius:12px">
        <div class="sub-text" style="font-size:13px">My Study Type</div>
        <c:choose>
          <c:when test="${empty studyProfile}"><div class="sub-text">아직 검사하지 않았어요.</div></c:when>
          <c:otherwise><div style="font-size:17px;font-weight:700;color:var(--primary)"><c:out value="${studyProfile.studyType.typeName}"/> (<c:out value="${studyProfile.studyType.typeCode}"/>)</div></c:otherwise>
        </c:choose>
        <a class="btn outline block" style="background:#fff" href="${ctx}/profile/${empty studyProfile ? 'survey' : 'survey/retake'}">${empty studyProfile ? '검사하기' : '재검사하기'}</a>
      </div>
      <a class="link" style="font-weight:700;display:flex;justify-content:center;width:100%;min-height:44px;align-items:center" href="${ctx}/my/applications">알림</a>
    </div>
  </aside>

  <div class="grow">
    <form class="card" method="post" action="${ctx}/user/update" style="gap:16px">
      <input type="hidden" name="userId" value="${user.userId}">
      <input type="hidden" name="studentId" value="<c:out value='${user.studentId}'/>">
      <input type="hidden" name="gender" value="${user.gender}">
      <div class="card-title">내 정보 수정</div>
      <div class="form-grid">
        <div class="field">
          <label for="n1">이름<span class="req"> *</span></label>
          <input id="n1" name="name" type="text" value="<c:out value='${user.name}'/>" required>
        </div>
        <div class="field">
          <label for="n4">전공<span class="opt"> (선택)</span></label>
          <input id="n4" name="major" type="text" value="<c:out value='${user.major}'/>">
        </div>
      </div>
      <div class="btn-row end"><button type="submit" class="btn">저장</button></div>
    </form>

    <form class="card" method="post" action="${ctx}/user/update" id="pwForm" style="gap:16px">
      <input type="hidden" name="userId" value="${user.userId}">
      <input type="hidden" name="currentPassword" id="curHidden">
      <div class="card-title">비밀번호 변경</div>
      <div class="form-grid">
        <div class="field">
          <label for="e2">비밀번호<span class="req"> *</span></label>
          <input id="e2" name="password" type="password" required>
        </div>
        <div class="field">
          <label for="e3">비밀번호 확인<span class="req"> *</span></label>
          <input id="e3" type="password" required>
          <div class="hint" id="pwMsg">비밀번호와 같은지만 확인해요.</div>
        </div>
      </div>
      <c:if test="${passwordChanged}"><div class="alert" role="status">비밀번호를 변경했어요.</div></c:if>
      <div class="btn-row end"><button type="button" class="btn" id="pwSave">저장</button></div>
    </form>

    <div class="card row-card" style="justify-content:space-between">
      <div class="card-title">계정</div>
      <div class="btn-row">
        <form class="inline" method="post" action="${ctx}/user/logout"><button type="submit" class="btn outline">로그아웃</button></form>
        <a class="btn gray" href="${ctx}/user/withdraw">회원탈퇴</a>
      </div>
    </div>
  </div>
</div>

<%-- 본인 확인 팝업: 기존 비밀번호를 받아 pwForm 의 hidden currentPassword 로 넘기고 제출 --%>
<div id="pwDialog" role="dialog" aria-modal="true" aria-labelledby="pwTitle" hidden
     style="position:fixed;inset:0;z-index:50;display:none;align-items:center;justify-content:center;padding:16px;box-sizing:border-box;background:rgba(15,27,61,.55)">
  <div class="card" style="width:100%;max-width:420px;padding:28px;gap:16px">
    <div class="col" style="gap:6px">
      <div id="pwTitle" style="font-size:18px;font-weight:700">본인 확인</div>
      <div class="sub-text">비밀번호를 변경하려면 기존 비밀번호를 입력해주세요.</div>
    </div>
    <div class="field">
      <label for="curPw">기존 비밀번호<span class="req"> *</span></label>
      <input id="curPw" type="password" placeholder="기존 비밀번호를 입력해주세요." autocomplete="current-password">
    </div>
    <div class="btn-row end">
      <button type="button" class="btn outline" id="pwCancel">취소</button>
      <button type="button" class="btn" id="pwOk">확인</button>
    </div>
  </div>
</div>

<script>
  (function () {
    var form = document.getElementById('pwForm'), dlg = document.getElementById('pwDialog');
    var cur = document.getElementById('curPw');
    function open() { dlg.hidden = false; dlg.style.display = 'flex'; cur.value = ''; cur.focus(); }
    function close() { dlg.hidden = true; dlg.style.display = 'none'; }
    document.getElementById('pwSave').addEventListener('click', function () {
      if (form.password.value === '' ) { form.password.focus(); return; }
      if (form.password.value !== document.getElementById('e3').value) {
        document.getElementById('pwMsg').textContent = '비밀번호가 서로 달라요.';
        return;
      }
      open();
    });
    document.getElementById('pwCancel').addEventListener('click', close);
    document.getElementById('pwOk').addEventListener('click', function () {
      if (cur.value === '') { cur.focus(); return; }
      document.getElementById('curHidden').value = cur.value;
      form.submit();
    });
    document.addEventListener('keydown', function (e) { if (e.key === 'Escape') close(); });
  })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
