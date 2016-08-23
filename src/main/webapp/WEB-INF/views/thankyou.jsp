<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="head.thankyou"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<!-- <body role="document" class="recipe-pic"> -->
<body role="document">
	
<%@include file="common/nav.jsp" %>	

	<div class="container container-opaque">	
	 	<div class="col-sm-12">
			<div class="page-header-white">
				<h1><spring:message code="title.thankyou"></spring:message></h1>
			</div>
			<div class="row">
				<div class="col-sm-6 col-sm-offset-3">
					<br>
					<h4 style="color:white"><spring:message code="thankyou.description1"></spring:message></h4>
					<br>
					<h4 style="color:white"><spring:message code="thankyou.description2"></spring:message></h4>
					<br>
					<h4 style="color:white"><spring:message code="thankyou.description3" arguments="${properties['company.email.feedback']}"></spring:message></h4>
					<br>			
				</div>
			</div>
		</div>
	</div>

<%@include file="common/footer.jsp" %>
	
</body>
</html>
