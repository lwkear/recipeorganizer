<!DOCTYPE html>
<html>
<head>

<title>About</title>

<%@include file="common/head.jsp" %>

</head>

<body role="document">
	<div id="wrap">

<%@include file="common/nav.jsp" %>
	
		<div class="container" style="opacity:1; color:#FFFFFF">
			<div class="page-header">
				<h1><spring:message code="about.title"></spring:message></h1>
				<h3><spring:message code="general.servertime"></spring:message>&nbsp;&nbsp;${serverTime}.</h3>
			</div>
		</div>
	</div>

<%@include file="common/footer.jsp" %>
	
</body>
</html>
