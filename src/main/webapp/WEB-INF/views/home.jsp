<!DOCTYPE html>
<html>
<head>
	
<title>Home</title>

<%@include file="common/head.jsp" %>
<%@include file="common/js.jsp" %>

</head>

<body role="document">

<sec:authorize var="isAuth" access="isAuthenticated()"></sec:authorize>
<sec:authorize var="isAdmin" access="hasAnyRole('ROLE_ADMIN')"></sec:authorize>
<sec:authorize var="isEditor" access="hasAnyRole('EDITOR','ADMIN')"></sec:authorize>
<sec:authorize var="isAuthor" access="hasAnyRole('AUTHOR','EDITOR','ADMIN')"></sec:authorize>
<sec:authorize var="isGuest" access="hasAnyRole('GUEST','EDITOR','AUTHOR','ADMIN')"></sec:authorize>

    <%@include file="common/nav.jsp" %>

 	<div class="container" role="main">
		<div class="jumbotron">
			<h1><spring:message code="label.home.title"></spring:message></h1>
			<p><spring:message code="label.servertime"></spring:message>&nbsp;${serverTime}.</p>
		</div>
		<div>
			<c:if test="${not empty username}">
				<p>Logged in as ${username} / ${password} / ${auth} </p>
			</c:if>
		</div>
		<div>
			<p>${create}</p>
		</div>
		<div>
			<p>${last}</p>
		</div>
		<div>
			<p>${inactive}</p>
		</div>
		<div>
			<p>${sessID}</p>
		</div>
		<div>
			<p>${sess}</p>
		</div>
		<div>
			<p>${country}</p>
		</div>
		<div>
			<p>${language}</p>
		</div>
		
<p></p>
<p></p>
<p></p>
<c:if test="${isAuth}">User is authorized</c:if>
<c:if test="${isGuest}">User is guest</c:if>
<c:if test="${isAuthor}">User is author</c:if>
<c:if test="${isEditor}">User is editor</c:if>
<c:if test="${isAdmin}">User is admin</c:if> 
<p></p>
<p></p>

<sec:authorize access="hasAuthority('ROLE_ADMIN')">
<p>Admin access - hasAuthority:ROLE_ADMIN</p>
</sec:authorize>
<sec:authorize access="hasAuthority('ADMIN')">
<p>Admin access - hasAuthority:ADMIN</p>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_ADMIN')">
<p>Admin access - hasRole:ROLE_ADMIN</p>
</sec:authorize>
<sec:authorize access="hasRole('ADMIN')">
<p>Admin access - hasRole:ADMIN</p>
</sec:authorize>

<sec:authorize access="hasRole('ROLE_EDITOR')">
Editor access
</sec:authorize>
<sec:authorize access="hasRole('ROLE_AUTHOR')">
Author access
</sec:authorize>
<sec:authorize access="hasRole('ROLE_GUEST')">
Guest access
</sec:authorize>
		
		
		<%@include file="common/footer.jsp" %>
		
	</div>
	
</body>
</html>