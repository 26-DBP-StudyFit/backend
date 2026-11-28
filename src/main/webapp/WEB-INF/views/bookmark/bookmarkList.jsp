<%--
  14 나의 찜 핏   GET /my/bookmarks   (설계서에 없음 → BookmarkController#listBookmarks 가정)
  찜 해제          POST /teams/{teamId}/bookmarks/delete   (임의 지정)
  모델 속성: teams:List<Team> (내가 찜한 팀)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/taglibs.jsp" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
  <jsp:param name="title" value="나의 찜 핏"/>
  <jsp:param name="active" value="saved"/>
</jsp:include>

<div class="row between">
  <div class="col" style="gap:4px">
    <h1>나의 찜 핏</h1>
    <div class="sub-text">내가 북마크한 팀입니다.</div>
  </div>
  <a class="btn outline" href="${ctx}/main">돌아가기</a>
</div>

<c:choose>
  <c:when test="${empty teams}"><div class="card empty">찜한 팀이 없어요. 마음에 드는 팀을 찜해보세요.</div></c:when>
  <c:otherwise>
    <div class="col">
      <c:forEach var="t" items="${teams}">
        <div class="card row-card">
          <div class="thumb">대표 사진</div>
          <div class="col grow" style="gap:4px;flex:1 1 220px;min-width:0">
            <a href="${ctx}/teams/${t.teamId}" style="font-size:17px;font-weight:700"><c:out value="${t.title}"/></a>
            <div><span class="chip"><c:out value="${catKo[t.studyCategory.name()]}" default="${t.studyCategory}"/></span></div>
            <div class="sub-text">
              <c:forEach var="d" items="${t.preferredDays}">${dayKo[d.name()]}</c:forEach>
              ${fn:substring(t.preferredStartTime, 0, 5)}~${fn:substring(t.preferredEndTime, 0, 5)} ·
              <c:out value="${regionKo[t.regionCode]}" default="${t.regionCode}"/>
            </div>
          </div>
          <div class="btn-row">
            <a class="btn outline sm" href="${ctx}/teams/${t.teamId}">상세 보기</a>
            <form class="inline" method="post" action="${ctx}/teams/${t.teamId}/bookmarks/delete">
              <button type="submit" class="icon-btn" aria-label="찜 해제" title="찜 해제">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2" stroke-linejoin="round" aria-hidden="true"><path d="M6 3h12v18l-6-4-6 4z"/></svg>
              </button>
            </form>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

<div><a class="link" href="${ctx}/teams">더 둘러보기</a></div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
