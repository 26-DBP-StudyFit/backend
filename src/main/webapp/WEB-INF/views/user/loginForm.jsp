<%--
  02 로그인 (UC-A-03)   GET /user/login  →  UserController#showLoginForm
                        POST /user/login (userId, password)  →  성공: redirect /main,  실패: 이 화면으로 forward
  모델 속성: loginFailed:boolean (실패 시)
  ※ 입력란 label 은 '이메일', name 은 설계서대로 userId (이메일이 로그인 아이디).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="로그인"/>
  <jsp:param name="nav" value="none"/>
  <jsp:param name="size" value="center"/>
</jsp:include>

<form class="form-card auth" method="post" action="${ctx}/user/login">
  <div class="col" style="gap:4px">
    <h1 style="font-size:28px">로그인</h1>
    <div class="sub-text" style="font-size:15px">계정으로 Study FIT을 시작해요.</div>
  </div>

  <c:if test="${loginFailed}">
    <div class="alert error" role="alert">이메일 또는 비밀번호가 올바르지 않아요.</div>
  </c:if>

  <div class="field">
    <label for="email">이메일</label>
    <input id="email" name="userId" type="email" placeholder="이메일 주소" value="<c:out value='${param.userId}'/>" required autofocus>
  </div>
  <div class="field">
    <label for="pw">비밀번호</label>
    <input id="pw" name="password" type="password" placeholder="비밀번호" required>
  </div>
  <button type="submit" class="btn block">로그인</button>
  <div class="row" style="justify-content:center;gap:8px;font-size:14px">
    <span class="sub-text">아직 계정이 없나요?</span>
    <a class="link" href="${ctx}/user/join" style="font-weight:700">회원가입</a>
  </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
