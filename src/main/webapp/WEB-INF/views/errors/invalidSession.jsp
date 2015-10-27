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
				<h3><spring:message code="session.invalid.title"></spring:message></h3>
			</div>			
			<div class="row">
				<div class="span12 center alert alert-danger text-center">
					<h3><spring:message code="session.invalid.message"></spring:message></h3>
				</div>
				<div class="form-group col-sm-12">&nbsp;</div>
				<div class="form-group col-sm-2 col-sm-offset-5">
					<a class="btn btn-default" href="<c:url value="/user/login"></c:url>" role="button">Login</a>
				</div>
			</div>
		</div>
	</div>	

<%@include file="../common/footer.jsp" %>

</body>
</html>
