<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="menu.myrecipes"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<sec:authorize var="isAuth" access="isAuthenticated()"/>
	<c:if test="${isAuth}"> 
		<sec:authentication var="secUserId" property="principal.id"/>
	</c:if>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="menu.myrecipes"></spring:message></h3>
			</div>			
			<table class="table" id="recipeList">
				<thead>
					<tr>
						<th><spring:message code="recipe.table.name"></spring:message></th>
						<th><spring:message code="recipe.table.description"></spring:message></th>
						<th><spring:message code="recipe.table.submitted"></spring:message></th>
						<th><spring:message code="recipe.table.category"></spring:message></th>
						<th><spring:message code="recipe.table.source"></spring:message></th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="recipe" items="${recipes}">
						<tr>
							<td>${recipe.name}</td>
							<td>${recipe.desc}</td>
							<td>${recipe.submitted}</td>
							<td>${recipe.category}</td>
							<td>${recipe.sourcetype}</td>
							<td><a class="btn btn-info btn-sm" href="../recipe/viewRecipe/${recipe.id}">
								<span class="glyphicon glyphicon-list-alt"></span></a>
							</td>
							<td><a class="btn btn-success btn-sm" href="../recipe/editRecipe/${recipe.id}">
								<span class="glyphicon glyphicon-pencil"></span></a>
							</td>
							<td><a class="btn btn-danger btn-sm" href="../recipe/deleteRecipe/${recipe.id}">
								<span class="glyphicon glyphicon-remove"></span></a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>	

<%@include file="../common/footer.jsp" %>

</body>

<script type="text/javascript">

$(document).ready(function() {
	$('#recipeList').DataTable({
    	columnDefs: [{
    	      targets: [-1,-2,-3],
    	      orderable: false
    	    }]		
	});
})
</script>

</html>