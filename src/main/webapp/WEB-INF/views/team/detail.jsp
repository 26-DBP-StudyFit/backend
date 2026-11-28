<%--
  09 팀 상세 (가입 전) (UC-B-02)   GET /teams/{teamId}  →  TeamController#showTeamDetail
  팀 가입 신청                     POST /teams/{teamId}/applications  →  성공: 팀 홈으로 redirect(성공 메시지), 실패: 이 화면(errorMessage)
  찜 신청                          POST /teams/{teamId}/bookmarks (임의 지정)
  모델 속성: team:Team (+ meetingMode, memberCount, description, goal — 도메인에 추가 필요),
             members:List<TeamMember>(user.name, role)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="팀 상세"/>
  <jsp:param name="active" value="explore"/>
</jsp:include>

<a class="back" href="${ctx}/teams"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 18l-6-6 6-6"/></svg>팀 살펴보기</a>

<div class="split">
  <section class="card grow" style="gap:20px">
    <div class="thumb big">팀 대표 사진 (없으면 기본 이미지)</div>
    <div class="col" style="gap:8px">
      <h1><c:out value="${team.title}"/></h1>
      <div class="row" style="gap:6px">
        <span class="chip"><c:out value="${catKo[team.studyCategory.name()]}" default="${team.studyCategory}"/></span>
        <span class="chip soft"><c:out value="${ansKo[team.meetingMode.name()]}" default="${team.meetingMode}"/></span>
      </div>
    </div>
    <div class="col" style="gap:0">
      <div class="kv"><span>팀 소개</span><span style="font-weight:400;text-align:left;flex:1;margin-left:24px"><c:out value="${team.description}"/></span></div>
      <div class="kv"><span>활동 목표</span><span style="font-weight:400;text-align:left;flex:1;margin-left:24px"><c:out value="${team.goal}"/></span></div>
      <div class="kv"><span>활동 지역</span><span><c:out value="${regionKo[team.regionCode]}" default="${team.regionCode}"/></span></div>
      <div class="kv">
        <span>스터디 시간대</span>
        <span>
          <c:forEach var="d" items="${team.preferredDays}">${dayKo[d.name()]} </c:forEach>· ${fn:substring(team.preferredStartTime, 0, 5)} ~ ${fn:substring(team.preferredEndTime, 0, 5)}
          <a class="link" style="margin-left:8px" href="${ctx}/availability/common?teamId=${team.teamId}">팀 일정 조율 보기</a>
        </span>
      </div>
      <div class="kv"><span>필요한 온도</span><span>${team.requiredTemperature}°C 이상</span></div>
    </div>
  </section>

  <aside class="side">
    <div class="card">
      <div class="card-title">팀원 정보 <span class="sub-text" style="font-weight:400">${team.memberCount} / ${team.recruitmentCount}명</span></div>
      <c:choose>
        <c:when test="${empty members}"><div class="sub-text">표시할 팀원이 없어요.</div></c:when>
        <c:otherwise>
          <c:forEach var="m" items="${members}">
            <div class="row" style="gap:10px">
              <div class="avatar" aria-hidden="true"></div>
              <div style="flex:1"><c:out value="${m.user.name}"/></div>
              <c:if test="${m.role.toString() eq 'LEADER'}"><span class="chip">팀장</span></c:if>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
    <div class="col" style="gap:8px">
      <form method="post" action="${ctx}/teams/${team.teamId}/applications"><button type="submit" class="btn block">팀 가입 하기</button></form>
      <form method="post" action="${ctx}/teams/${team.teamId}/bookmarks">
        <button type="submit" class="btn outline block" style="height:46px;font-size:15px">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round" aria-hidden="true"><path d="M6 3h12v18l-6-4-6 4z"/></svg>찜 신청
        </button>
      </form>
    </div>
  </aside>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
