<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp"%>

<title><spring:message code="exception.system.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12">
			<div class="page-header">
				<h3>${errorTitle}</h3>
			</div>			
			<div class="col-sm-12">			
				<div class="center alert text-center strong">
				<c:forEach var="msg" items="${errorMsgs}">
					<div><h4>${msg}</h4></div>
				</c:forEach>
				</div>
			</div>
		</div>
	</div>	

<%@include file="common/footer.jsp" %>
	
</body>
</html>