<%--
  03 회원가입 (UC-A-01)   GET /user/join  →  UserController#showJoinForm
                          POST /user/join →  UserController#joinUser   성공: redirect /profile/survey,  실패: 이 화면으로 forward
  전송 파라미터(설계서): userId, password, email, name, gender, major, studentId
  모델 속성: joinFailed:boolean (실패 시), errors / errorMessage (선택, 헤더에서 출력)
  ※ 이메일이 로그인 아이디라서 userId 는 제출할 때 이메일 값으로 채운다. passwordConfirm 은 브라우저 검증용.
  ※ 성별 option 값은 M / F (화면 문구 '남성 (M)', '여성 (F)'). Gender enum 상수명에 맞게 수정.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="회원가입"/>
  <jsp:param name="nav" value="none"/>
  <jsp:param name="size" value="center"/>
</jsp:include>

<form class="form-card auth" style="max-width:520px" method="post" action="${ctx}/user/join" id="joinForm">
  <input type="hidden" name="userId" id="userId">
  <div class="col" style="gap:4px">
    <h1 style="font-size:28px">회원가입</h1>
    <div class="sub-text" style="font-size:15px">가입 후 스터디 타입 검사를 진행해요.</div>
  </div>

  <c:if test="${joinFailed}">
    <div class="alert error" role="alert">회원가입에 실패했어요. 이미 가입된 이메일·학번이거나 비밀번호 형식이 올바르지 않을 수 있어요.</div>
  </c:if>

  <div class="col" style="gap:16px">
    <div class="field">
      <label for="email">이메일<span class="req"> *</span></label>
      <input id="email" name="email" type="email" placeholder="이메일 주소" value="<c:out value='${param.email}'/>" required>
      <div class="hint">로그인 아이디로 사용돼요. 이미 가입된 이메일은 쓸 수 없어요.</div>
    </div>
    <div class="field">
      <label for="name">이름<span class="req"> *</span></label>
      <input id="name" name="name" type="text" placeholder="이름" value="<c:out value='${param.name}'/>" required>
    </div>
    <div class="field">
      <label for="pw">비밀번호<span class="req"> *</span></label>
      <input id="pw" name="password" type="password" placeholder="비밀번호" required>
    </div>
    <div class="field">
      <label for="pw2">비밀번호 확인<span class="req"> *</span></label>
      <input id="pw2" type="password" placeholder="비밀번호 확인" required>
      <div class="hint" id="pwMsg">비밀번호와 같은지만 확인해요.</div>
    </div>
    <div class="field">
      <label for="studentId">학번<span class="req"> *</span></label>
      <input id="studentId" name="studentId" type="text" placeholder="학번" value="<c:out value='${param.studentId}'/>" required>
      <div class="hint">이미 가입된 학번은 쓸 수 없어요.</div>
    </div>
    <div class="field">
      <label for="gender">성별<span class="opt"> (선택)</span></label>
      <select id="gender" name="gender">
        <option value="">선택 안 함</option>
        <option value="M" ${param.gender eq 'M' ? 'selected' : ''}>남성 (M)</option>
        <option value="F" ${param.gender eq 'F' ? 'selected' : ''}>여성 (F)</option>
      </select>
    </div>
    <div class="field">
      <label for="major">전공<span class="opt"> (선택)</span></label>
      <input id="major" name="major" type="text" placeholder="전공" value="<c:out value='${param.major}'/>">
    </div>
  </div>

  <button type="submit" class="btn block">가입하고 스터디 타입 검사하기</button>
  <div class="row" style="justify-content:center;gap:8px;font-size:14px">
    <span class="sub-text">이미 계정이 있나요?</span>
    <a class="link" href="${ctx}/user/login" style="font-weight:700">로그인</a>
  </div>
</form>

<script>
  (function () {
    var f = document.getElementById('joinForm');
    f.addEventListener('submit', function (e) {
      if (f.password.value !== document.getElementById('pw2').value) {
        e.preventDefault();
        document.getElementById('pwMsg').textContent = '비밀번호가 서로 달라요. 다시 확인해주세요.';
        document.getElementById('pw2').focus();
        return;
      }
      document.getElementById('userId').value = f.email.value; // 이메일 = 로그인 아이디
    });
  })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
