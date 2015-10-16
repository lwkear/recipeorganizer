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
		<c:if test="${isAuth}"><p>User is authorized</p></c:if>
		<c:if test="${isGuest}"><p>User is guest</p></c:if>
		<c:if test="${isAuthor}"><p>User is author</p></c:if>
		<c:if test="${isEditor}"><p>User is editor</p></c:if>
		<c:if test="${isAdmin}"><p>User is admin</p></c:if> 
		<p></p>
		<p></p>

		<%@include file="common/footer.jsp" %>
		
	</div>
</body>
</html>