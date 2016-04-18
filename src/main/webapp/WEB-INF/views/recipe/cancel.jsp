<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="recipe.cancel.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="recipe.cancel.title"></spring:message></h3>
			</div>
		</div>
	 	<div class="col-sm-8 col-sm-offset-2 text-center std-maroon">
			<h4><spring:message code="recipe.cancel.message1"></spring:message></h4>
		</div>
	 	<div class="col-sm-6 col-sm-offset-3 text-center spacer-vert-sm">
			<h5 class="text-center"><spring:message code="recipe.cancel.message2"></spring:message>&nbsp;&nbsp;<spring:message code="recipe.cancel.message3"></spring:message></h5>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
