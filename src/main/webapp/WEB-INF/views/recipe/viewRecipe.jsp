<!DOCTYPE html>
<html>
<head>

<title>DisplayRecipe</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header">
				<h3>${recipe.name}</h3>
				<h5><spring:message code="common.submittedby"></spring:message>&nbsp;${recipe.user.firstName}&nbsp;${recipe.user.lastName}</h5>
			</div>
			
<%@include file="recipeContent.jsp" %>
			
		</div>
	</div>		

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/recipe.js" />"></script>

</html>