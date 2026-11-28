<%--
  30 오류 안내   변경 제한(409)·삭제/저장 오류(500) 등에서 forward 하는 공통 화면 (/error/message.jsp)
  모델 속성: status:int (HTTP 상태 코드, 없으면 500 표시), message:String, backUri:String (이전 화면 주소)
  ※ 안내 문구는 서버가 message 로 주고, 제목은 코드에 맞춰 화면이 정한다.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="오류 안내"/>
  <jsp:param name="size" value="center"/>
</jsp:include>

<c:set var="code" value="${empty status ? 500 : status}"/>
<c:set var="backHref" value="javascript:history.back()"/>
<c:if test="${not empty backUri}"><c:set var="backHref" value="${ctx}${backUri}"/></c:if>
<c:set var="titles" value="${ {'400':'요청 값을 확인해 주세요','403':'접근 권한이 없어요','404':'페이지를 찾을 수 없어요','409':'지금은 변경할 수 없어요','500':'처리 중 문제가 생겼어요'} }"/>

<section class="card" style="gap:20px;text-align:center;align-items:center;padding:40px 24px">
  <div style="width:64px;height:64px;border-radius:50%;background:var(--soft);display:flex;align-items:center;justify-content:center;color:var(--primary)" aria-hidden="true">
    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v6M12 16.5v.5"/></svg>
  </div>
  <div class="col" style="gap:6px;align-items:center">
    <div class="sub-text">HTTP ${code}</div>
    <h1><c:out value="${titles[code.toString()]}" default="오류가 발생했어요"/></h1>
    <div class="sub-text" style="font-size:15px"><c:out value="${empty message ? '잠시 후 다시 시도해 주세요.' : message}"/></div>
  </div>
  <div class="btn-row" style="justify-content:center">
    <a class="btn outline" href="${ctx}/main">홈으로</a>
    <a class="btn" href="${backHref}">이전 화면으로 돌아가기</a>
  </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
