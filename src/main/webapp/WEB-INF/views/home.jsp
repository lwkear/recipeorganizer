<!DOCTYPE html>
<html>
<head>
	
<title>Home</title>

<%@include file="common/head.jsp" %>

<link href="<c:url value="/resources/css/layout.css" />" rel="stylesheet">

</head>

<body class="body-image" role="document">
	<div id="wrap">
	
<%@include file="common/nav.jsp" %>	
	
	 	<div class="container" style="opacity:1; color:#FFFFFF">
			<div class="page-header"> 		
				<h1><spring:message code="home.title"></spring:message></h1>
			</div>
			<div class="lead text-center"> 		
				<h3><spring:message code="home.description1"></spring:message></h3>
			</div>
			<div class="row">
				<div class="col-sm-6 col-sm-offset-3">
					<br>
					<h4><spring:message code="home.description2"></spring:message></h4>
					<br>
					<h4><spring:message code="home.description3"></spring:message>
					<spring:message code="home.description4"></spring:message></h4>
					<br>
					<h4><spring:message code="home.description5"></spring:message></h4>
					<br>			
				</div>
			</div>
			<div class="row">
				<div class="col-sm-2 col-sm-offset-5">
					<a class="btn btn-default" href="<c:url value="/user/signup"></c:url>" role="button">Sign Up</a>
				</div>
			</div>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>
</html>


<%-- 
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
				<p></p> --%>
				
				
				
<%-- <body class="body-image" role="document">
	<div id="wrap">
	
<%@include file="common/nav.jsp" %>	
	
	 	<div class="container" style="opacity:1; color:#FFFFFF">
			<div class="page-header"> 		
				<h1><spring:message code="home.title"></spring:message></h1>
			</div>
			<div class="lead text-center"> 		
				<h3><spring:message code="home.description1"></spring:message></h3>
			</div>
			<div class="row">
				<div class="col-sm-6 col-sm-offset-3">
					<br>
					<h4><spring:message code="home.description2"></spring:message></h4>
					<br>
					<h4><spring:message code="home.description3"></spring:message>
					<spring:message code="home.description4"></spring:message></h4>
					<br>
					<h4><spring:message code="home.description5"></spring:message></h4>
					<br>			
				</div>
			</div>
			<div class="row">
				<div class="col-sm-2 col-sm-offset-5">
					<a class="btn btn-default" href="<c:url value="/user/signup"></c:url>" role="button">Sign Up</a>
				</div>
			</div>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>
				 --%>