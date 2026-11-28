<%--
  공통 헤더. 사용법:
    <jsp:include page="/WEB-INF/views/common/header.jsp">
      <jsp:param name="title" value="로그인"/>
      <jsp:param name="active" value="home"/>   상단 메뉴 강조: home | explore | match | saved | attendance (생략 가능)
    </jsp:include>
  인증·검사 화면은 메뉴 없는 바를 쓴다:  nav=none, sub=오른쪽 안내 문구(선택), login=true(로그인 링크)
  <main> 까지 열고, footer.jsp 가 닫는다. 본문 폭: size=narrow(760px) | center(가운데 정렬)

  모델 속성 message / errorMessage / errors(Map<String,String>) 는 여기서 한 번에 알림으로 출력한다.
  (각 페이지에서 따로 출력하지 않는다)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${empty param.title ? 'Study FIT' : param.title}"/> · Study FIT</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/studyfit.css">
</head>
<body>
<c:choose>
  <c:when test="${param.nav eq 'none'}">
    <header class="topbar">
      <a class="brand" href="${ctx}/">Study FIT</a>
      <c:if test="${not empty param.sub}"><span class="sub"><c:out value="${param.sub}"/></span></c:if>
      <c:if test="${param.login eq 'true'}"><span class="sub"><a href="${ctx}/user/login">로그인</a></span></c:if>
    </header>
  </c:when>
  <c:otherwise>
    <header class="topbar">
      <a class="brand" href="${ctx}/main">Study FIT</a>
      <nav class="nav" aria-label="주 메뉴">
        <a href="${ctx}/main" class="${param.active eq 'home' ? 'active' : ''}">Go home</a>
        <a href="${ctx}/teams" class="${param.active eq 'explore' ? 'active' : ''}">팀 살펴보기</a>
        <a href="${ctx}/recommendations/teams/form" class="${param.active eq 'match' ? 'active' : ''}">나의 fit 찾기</a>
        <a href="${ctx}/my/bookmarks" class="${param.active eq 'saved' ? 'active' : ''}">내가 찜한 fit</a>
        <a href="${ctx}/attendance" class="${param.active eq 'attendance' ? 'active' : ''}">출석체크</a>
      </nav>
      <div class="icons">
        <a href="${ctx}/my/applications" aria-label="알림함"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 8 3 8H3s3-1 3-8"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/></svg></a>
        <a href="${ctx}/mypage" aria-label="마이페이지"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></svg></a>
      </div>
    </header>
  </c:otherwise>
</c:choose>
<main class="main ${param.size}">
  <c:if test="${not empty message}">
    <div class="alert" role="status"><c:out value="${message}"/></div>
  </c:if>
  <c:if test="${not empty errorMessage}">
    <div class="alert error" role="alert"><c:out value="${errorMessage}"/></div>
  </c:if>
  <c:if test="${not empty errors}">
    <div class="alert error" role="alert">
      <ul>
        <c:forEach var="e" items="${errors}"><li><c:out value="${e.value}"/></li></c:forEach>
      </ul>
    </div>
  </c:if>
