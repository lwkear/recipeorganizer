<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="menu.policies"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="title.policies"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<h4><spring:message code="policies.terms"></spring:message></h4>
			<div>
				<spring:message code="policies.terms.description"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="policies.privacy"></spring:message></h4>
			<div>
				<spring:message code="policies.privacy.description"></spring:message>
			</div>
		</div>
	</div>
   
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
