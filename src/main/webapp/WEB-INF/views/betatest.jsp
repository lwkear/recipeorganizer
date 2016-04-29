<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="signup.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="signup.title"></spring:message></h3>
			</div>
		</div>
		<div class="lead text-center"> 		
			<h3><spring:message code="beta.notasignup"></spring:message></h3>
		</div>
		<div class="row">
			<div class="col-sm-8 col-sm-offset-2">
				<br>
				<h5><spring:message code="beta.description1"></spring:message></h5>
				<br>
				<h5><spring:message code="beta.description2"></spring:message></h5>
				<br>
				<h5><spring:message code="beta.description3" arguments="${properties['company.email.support.account']}"></spring:message></h5>
				<br>
				<h5><spring:message code="beta.description4" arguments="${properties['company.email.support.technical']}"></spring:message></h5>
			</div>
			<div class="col-sm-8 col-sm-offset-2 text-center spacer-vert-xs">
				<h4 style="color:black"><spring:message code="beta.thanks"></spring:message></h4>
			</div>
 		</div>
 	</div>
   
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
