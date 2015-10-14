<!DOCTYPE html>
<html>
<head>
	
<title>Home</title>

<%@include file="common/head.jsp" %>
<%@include file="common/js.jsp" %>

</head>

<body role="document">

    <%@include file="common/nav.jsp" %>

 	<div class="container theme-showcase" role="main">
		<div class="jumbotron">
			<h1>
				<spring:message code="label.home.title"></spring:message>  
			</h1>
			<P>  <spring:message code="label.servertime"></spring:message>&nbsp;${serverTime}. </P>
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
		
		<%@include file="common/footer.jsp" %>
		
	</div>
	
</body>
</html>