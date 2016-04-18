<!DOCTYPE html>
<html>
<head>
	
<%@include file="common/head.jsp" %>

<title><spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">
	
<%@include file="common/nav.jsp" %>	

	<div class="container container-opaque">	
	 	<div class="col-sm-12">
			<div class="page-header-white"> 		
				<h1><spring:message code="title.home"></spring:message></h1>
			</div>
			<div class="lead text-center"> 		
				<h3><spring:message code="home.description1"></spring:message></h3>
			</div>
			<div class="row">
				<div class="col-sm-6 col-sm-offset-3">
					<br>
					<h4 style="color:white"><spring:message code="home.description2"></spring:message></h4>
					<br>
					<h4 style="color:white"><spring:message code="home.description3"></spring:message>
					<spring:message code="home.description4"></spring:message></h4>
					<br>
					<h4 style="color:white"><spring:message code="home.description5"></spring:message></h4>
					<br>			
				</div>
			</div>
			<div class="row">
				<div class="col-sm-2 col-sm-offset-5 text-center">
					<a class="btn btn-default" href="<c:url value="/user/signup"></c:url>" role="button">
						<spring:message code="menu.signup"></spring:message>
					</a>
				</div>
			</div>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>
</html>
