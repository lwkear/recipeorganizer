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
			<div class="row">
				<div class="span12 center alert alert-danger text-center">
					<span><spring:message code="common.403.message"></spring:message>&nbsp;${errorMessage}</span>
				</div>
			</div>
		</div>
	</div>	

<%@include file="../common/footer.jsp" %>
	
</body>
</html>
