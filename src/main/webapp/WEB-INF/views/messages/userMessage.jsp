<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<title><spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12">
			<div class="page-header">
				<h3>${title}</h3>
			</div>			
			<div class="row">
				<div class="span12 center alert alert-success text-center">
					<span>${message}</span>
				</div>
			</div>
		</div>
	</div>	

<%@include file="../common/footer.jsp" %>

</body>
</html>
