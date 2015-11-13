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
			<h4 class="text-center"><spring:message code="recipe.save.message1"></spring:message><em>&nbsp;${recipe.name}</em><spring:message code="recipe.save.message2"></spring:message></h4>
		</div>
	 	<div class="col-sm-12">
			<h5 class="text-center"><spring:message code="recipe.save.message3"></spring:message></h5>
		</div>
		<div class="col-sm-12 spacer-vert-lg">
			<div class="form-group col-sm-2 col-sm-offset-5">
				<a class="btn btn-default" href="../recipeorganizer/recipe" role="button"><spring:message code="recipe.end.newrecipe"></spring:message></a>
			</div>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
