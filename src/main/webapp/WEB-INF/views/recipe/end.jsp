<!DOCTYPE html>
<html>
<head>

<title>End</title>

<title>About</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="recipe.end.title"></spring:message></h3>
			</div>
		</div>
	 	<div class="col-sm-12">
	 	
			<h4>${recipe.name}&nbsp;<spring:message code="recipe.save.message"></spring:message></h4>
		</div>
		<div class="row spacer-vert-lg">
			<div class="form-group col-sm-2 col-sm-offset-5">
				<a class="btn btn-default" href="../recipe" role="button"><spring:message code="recipe.end.newrecipe"></spring:message></a>
			</div>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
