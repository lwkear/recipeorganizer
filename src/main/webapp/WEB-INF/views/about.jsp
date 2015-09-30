<!DOCTYPE html>
<html>
<head>

	<%-- <%@include file="common/head.jsp" %> --%>
	
	<title>About</title>

<!-- Placed at the end of the document so the pages load faster -->
<%@include file="common/js.jsp" %>

<%-- <%@ page session="false" %> --%>

</head>

<body role="document">

    <%@include file="common/nav.jsp" %>

	<div class="container theme-showcase" role="main">
		<div class="jumbotron">
			<h1>
				About Page!  
			</h1>
			<P>  The time on the server is ${serverTime}. </P>
		</div>
	</div>
	
</body>
</html>
