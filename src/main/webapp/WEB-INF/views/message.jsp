<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp"%>

<title><spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<div class="page-header"> 		
				<h3>${title}</h3>
			</div>
		</div>
		<div class="col-sm-6 col-sm-offset-3 spacer-vert-sm">
			<h4>${msgHeader}</h4>
		</div>
		<div class="col-sm-6 col-sm-offset-3 spacer-vert-xs">
			<c:forEach var="msg" items="${messages}">
				${msg}
			</c:forEach>
		</div>
		<div class="col-sm-6 col-sm-offset-3 spacer-vert-xs">
			<h4><spring:message code="email.common.memberthankyou"></spring:message></h4>
		</div>
	</div>	

<%@include file="common/footer.jsp" %>

</body>
</html>
