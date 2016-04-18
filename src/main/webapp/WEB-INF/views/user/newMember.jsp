<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="menu.about"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="dashboard.title"></spring:message>,&nbsp;${user.firstName}</h3>
			</div>
		</div>
		<div class="col-sm-12">
			<h4><spring:message code="newmember.title1"></spring:message></h4>
			<div>
				<spring:message code="newmember.description1"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
		</div>
		<div class="col-sm-12 spacer-vert-xs">
		</div>
	</div>
    
<%@include file="../common/footer.jsp" %>	

</body>
</html>
