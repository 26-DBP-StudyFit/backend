<%--
  06 알림함 (UC-B-03/04 받은 팀 가입 신청 목록)   GET /my/applications  →  JoinApplicationController#listReceivedApplications
  수락/거절  POST /teams/{teamId}/applications/{applicationId}/approve | reject
  모두 읽음  POST /my/notifications/read-all  (설계서에 없음, 임의 지정)
  모델 속성: notifications:List<NotificationDto>
    category   APPLICATION | SCHEDULE | ATTENDANCE   (탭 필터, '전체' 는 모두)
    actionType DECIDE(수락/거절) | TEAM_HOME(팀 홈 가기) | VIEW_SCHEDULE(일정 보기) | CHECK_IN(출석하기)
    message, timeLabel('방금 전' 등), read:boolean, teamId, applicationId(선택), scheduleId(선택)
  ※ 설계서의 applications:List<ApplicationDetailDto> 는 category=APPLICATION · actionType=DECIDE 알림으로 담아주면 된다.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="알림함"/>
</jsp:include>

<div class="row between">
  <h1>알림함</h1>
  <form class="inline" method="post" action="${ctx}/my/notifications/read-all"><button type="submit" class="btn gray sm">모두 읽음 처리</button></form>
</div>

<div class="pills" id="tabs" role="group" aria-label="알림 종류">
  <button type="button" class="pill" aria-pressed="true" data-cat="">전체</button>
  <button type="button" class="pill" aria-pressed="false" data-cat="APPLICATION">가입 신청</button>
  <button type="button" class="pill" aria-pressed="false" data-cat="SCHEDULE">일정</button>
  <button type="button" class="pill" aria-pressed="false" data-cat="ATTENDANCE">출석</button>
</div>

<c:choose>
  <c:when test="${empty notifications}"><div class="card empty">새 알림이 없어요.</div></c:when>
  <c:otherwise>
    <div class="col" id="list">
      <c:forEach var="n" items="${notifications}">
        <div class="card row-card noti" data-cat="${n.category}">
          <span class="unread" style="${n.read ? 'visibility:hidden' : ''}" aria-label="${n.read ? '읽음' : '안 읽음'}"></span>
          <div class="avatar" aria-hidden="true"></div>
          <div class="col grow" style="gap:2px;flex:1 1 240px;min-width:0">
            <div>
              <c:choose>
                <c:when test="${n.category.toString() eq 'APPLICATION' and not empty n.applicationId}">
                  <a class="link" style="text-decoration:none;color:var(--text);font-weight:500" href="${ctx}/teams/${n.teamId}/applications/${n.applicationId}"><c:out value="${n.message}"/></a>
                </c:when>
                <c:otherwise><span style="font-weight:500"><c:out value="${n.message}"/></span></c:otherwise>
              </c:choose>
            </div>
            <div class="sub-text" style="font-size:13px">
              <c:choose>
                <c:when test="${n.category.toString() eq 'APPLICATION'}">가입 신청</c:when>
                <c:when test="${n.category.toString() eq 'SCHEDULE'}">일정</c:when>
                <c:otherwise>출석</c:otherwise>
              </c:choose>
              · <c:out value="${n.timeLabel}"/>
            </div>
          </div>
          <div class="btn-row">
            <c:choose>
              <c:when test="${n.actionType.toString() eq 'DECIDE'}">
                <form class="inline" method="post" action="${ctx}/teams/${n.teamId}/applications/${n.applicationId}/approve"><button type="submit" class="btn sm">수락</button></form>
                <form class="inline" method="post" action="${ctx}/teams/${n.teamId}/applications/${n.applicationId}/reject"><button type="submit" class="btn gray sm">거절</button></form>
              </c:when>
              <c:when test="${n.actionType.toString() eq 'VIEW_SCHEDULE'}"><a class="btn outline sm" href="${ctx}/schedule/list?teamId=${n.teamId}">일정 보기</a></c:when>
              <c:when test="${n.actionType.toString() eq 'CHECK_IN'}"><a class="btn outline sm" href="${ctx}/attendance?teamId=${n.teamId}">출석하기</a></c:when>
              <c:when test="${n.actionType.toString() eq 'TEAM_HOME'}"><a class="btn outline sm" href="${ctx}/teams/${n.teamId}/home">팀 홈 가기</a></c:when>
            </c:choose>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

<script>
  (function () {
    var tabs = document.querySelectorAll('#tabs .pill');
    tabs.forEach(function (t) {
      t.addEventListener('click', function () {
        var cat = t.getAttribute('data-cat');
        tabs.forEach(function (o) { o.setAttribute('aria-pressed', o === t ? 'true' : 'false'); });
        document.querySelectorAll('.noti').forEach(function (n) {
          n.style.display = (!cat || n.getAttribute('data-cat') === cat) ? '' : 'none';
        });
      });
    });
  })();
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
