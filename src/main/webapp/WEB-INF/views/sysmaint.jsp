<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp"%>

<title><spring:message code="sysmaint.title.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="sysmaint.title.systemUnavailable"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">			
			<div class="col-sm-12">			
				<div class="center alert text-center strong">
				<c:forEach var="msg" items="${systemMsgs}">
					<div><h4>${msg}</h4></div>
				</c:forEach>
				</div>
			</div>
		</div>
	</div>	

<%@include file="common/footer.jsp" %>
	
</body>
</html>
