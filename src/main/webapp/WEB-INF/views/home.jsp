<!DOCTYPE html>
<html>
<head>
	<%@include file="common/js.jsp" %>
	<%-- <%@include file="common/head.jsp" %> --%>
	
	<title>Home</title>

<%-- <%@ page session="false" %> --%>
	
</head>

<body role="document">
<body>

    <%@include file="common/nav.jsp" %>

	<div class="container theme-showcase" role="main">
		<div class="jumbotron">
			<h1>
				Welcome to the RecipeOrganizer!  
			</h1>
			<p>The time on the server is ${serverTime}. </p>
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
	</div>
</body>
</html>