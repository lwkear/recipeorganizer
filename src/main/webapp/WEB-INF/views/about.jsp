<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="menu.about"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="menu.about"></spring:message></h3>
			</div>
			<input name="${_csrf.parameterName}" value="${_csrf.token}"/>
		</div>
	</div>
    
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
