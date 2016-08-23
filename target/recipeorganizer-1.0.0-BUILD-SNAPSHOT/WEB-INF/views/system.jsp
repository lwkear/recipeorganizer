<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp"%>

<title><spring:message code="exception.title.system"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

<div class="container container-white">
	<div class="col-sm-12 spacer-vert-lg">
		<div class="col-sm-2">
			<h1 class="bold-maroon"><spring:message code="exception.title.oops"></spring:message></h1>
		</div>
		<div class="col-sm-8">
			<h4><spring:message code="exception.common.embarrassing"></spring:message></h4>
			<div>
				<c:forEach var="msg" items="${errorMsgs}">
					${msg}
				</c:forEach>
			</div>
			<h4 class="spacer-vert-md"><em><spring:message code="exception.common.techteam"></spring:message></em></h4>
		</div>
	</div>
</div>	

<%@include file="common/footer.jsp" %>
	
</body>
</html>
