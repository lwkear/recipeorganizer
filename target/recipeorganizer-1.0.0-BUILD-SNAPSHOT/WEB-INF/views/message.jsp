<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp"%>

<title><spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12">
			<div class="page-header">
				<h3>${title}</h3>
			</div>			
			<div class="col-sm-8 col col-sm-offset-2">			
				<div class="center alert text-center strong">
					<div><h4>${message}</h4></div>
				</div>
			</div>
		</div>
	</div>	

<%@include file="common/footer.jsp" %>

</body>
</html>
