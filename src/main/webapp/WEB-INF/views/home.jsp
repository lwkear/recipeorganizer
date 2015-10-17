<!DOCTYPE html>
<html>
<head>
	
<title>Home</title>

<%@include file="common/head.jsp" %>

</head>

<body role="document">
	<div id="wrap">
	
	    <%@include file="common/nav.jsp" %>
	
	 	<div class="container" style="opacity:1; color:#FFFFFF">
			<div class="page-header"> 		
				<h1><spring:message code="label.home.title"></spring:message></h1>
				<h3><spring:message code="label.servertime"></spring:message>&nbsp;&nbsp;${serverTime}.</h3>
				<div>
					<c:if test="${isAuth}">
						<p>Logged in as ${email} / ${firstname}&nbsp;${lastname}</p>
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
				
			</div>	
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>
</html>
