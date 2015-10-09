<!DOCTYPE html>
<html>
<head>
<title>EmailSent</title>
</head>

<%@include file="../common/js.jsp" %>
<%@include file="../common/head.jsp"%>

<body role="document">

    <%@include file="../common/nav.jsp" %>

	<div class="container">
		<h1>Thanks for signing up!</h1>
		<div class="col-sm-12">&nbsp;</div>
		<div class="col-sm-12">&nbsp;</div>
		<div class="col-sm-12">
			<div class="span12 center alert alert-success text-center">
				<span>${signupMessage}</span>
			</div>
		</div>
	</div>
	
</body>
</html>
