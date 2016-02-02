<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="recipe.end.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="recipe.end.title"></spring:message></h3>
			</div>
		</div>
	 	<div class="col-sm-12 text-center">
			<h4><spring:message code="recipe.save.message1"></spring:message><em>&nbsp;${recipe.name}</em><spring:message code="recipe.save.message2"></spring:message></h4>
		</div>
		<div class="col-sm-8 col-sm-offset-2 text-center spacer-vert-md">
			<c:if test="${recipe.allowShare}">				
				<p class="std-maroon"><spring:message code="recipe.review.sharereview"></spring:message><spring:message code="recipe.review.questions"></spring:message></p>
			</c:if>
			<c:if test="${not recipe.allowShare}">				
				<p class="std-maroon"><spring:message code="recipe.review.noshare"></spring:message></p>
			</c:if>
		</div>			
	 	<div class="col-sm-12 spacer-vert-sm">
			<h5 class="text-center"><spring:message code="recipe.save.message3"></spring:message></h5>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<div class="form-group col-sm-2 col-sm-offset-5">
				<a class="btn btn-default" href="../recipeorganizer/recipe" role="button"><spring:message code="recipe.end.newrecipe"></spring:message></a>
			</div>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
