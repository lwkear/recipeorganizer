<!DOCTYPE html>
<html>
<head>

<title>About</title>

<%@include file="common/head.jsp" %>
<%@include file="common/js.jsp" %>

</head>

<body role="document">

    <%@include file="common/nav.jsp" %>

	<div class="container theme-showcase" role="main">
		<div class="jumbotron">
			<h1>
				<spring:message code="label.about.title"></spring:message>  
			</h1>
			<P>  <spring:message code="label.servertime"></spring:message>&nbsp;${serverTime}. </P>
		</div>
	</div>

<%@include file="common/footer.jsp" %>
	
</body>
</html>
