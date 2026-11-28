<%--
  28 회원 탈퇴 확인 (UC-A-07)   GET /user/withdraw (확인 화면, 설계서에 GET 없음 → 임의 지정)
                                POST /user/withdraw (userId, password)  →  성공: 세션 종료 후 redirect /user/login
                                실패(비밀번호 불일치): 마이페이지로 돌아가 withdrawFailed 오류 표시
  모델 속성: user:User (세션의 user 로 대체 가능)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="회원 탈퇴"/>
  <jsp:param name="size" value="narrow"/>
</jsp:include>

<form class="form-card" method="post" action="${ctx}/user/withdraw" id="wdForm">
  <input type="hidden" name="userId" value="${empty user ? sessionScope.user.userId : user.userId}">
  <h1>회원 탈퇴</h1>

  <div class="card" style="background:var(--bg)">
    <div class="card-title">탈퇴 전에 확인해 주세요</div>
    <ul style="margin:0;padding-left:20px;display:flex;flex-direction:column;gap:6px;color:var(--muted)">
      <li>탈퇴하면 계정이 비활성화되고 바로 로그아웃돼요.</li>
      <li>가입한 팀의 팀원 목록과 활동 기록에서 내 정보가 더 이상 활성 상태로 보이지 않아요.</li>
      <li>같은 아이디로 다시 가입하려면 새로 가입해야 해요.</li>
    </ul>
  </div>

  <div class="field">
    <label for="w1">비밀번호 확인</label>
    <input id="w1" name="password" type="password" placeholder="현재 비밀번호를 입력해 주세요" required>
  </div>
  <label class="check"><input type="checkbox" id="agree">위 내용을 모두 확인했어요</label>

  <div class="btn-row end">
    <a class="btn outline" href="${ctx}/mypage">취소</a>
    <button type="submit" class="btn" id="wdBtn" disabled>탈퇴하기</button>
  </div>
  <div class="hint">비밀번호가 맞지 않으면 마이페이지로 돌아가 오류 문구가 보여요.</div>
</form>

<script>
  document.getElementById('agree').addEventListener('change', function () {
    document.getElementById('wdBtn').disabled = !this.checked;
  });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
