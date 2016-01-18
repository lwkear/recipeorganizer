<!DOCTYPE html>
<html>
<head>
<title>Error</title>
</head>

<%@include file="../common/head.jsp"%>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="common.error"></spring:message></h3>
			</div>			
			<div class="col-sm-12">			
				<!-- <div class="center alert alert-danger text-center"> -->
				<div class="center alert text-danger text-center strong">
				<c:forEach var="msg" items="${errorMsgs}">
					<div><h4>${msg}</h4></div>
				</c:forEach>
				</div>
			</div>
		</div>
	</div>	

<%@include file="../common/footer.jsp" %>
	
</body>
</html>
