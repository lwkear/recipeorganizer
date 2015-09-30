<!DOCTYPE html>
<html>
<head>

<title>List Recipes</title>

<%@include file="../common/js.jsp"%>
<%@include file="../common/head.jsp"%>

<script type="text/javascript">

//shorthand for document.ready
$(function() {

	
})

</script>

</head>

<body role="document">

    <%@include file="../common/nav.jsp" %>

	<div class="container">

		<sec:authorize var="isAuth" access="isAuthenticated()"/>
		<c:if test="${isAuth}"> 
			<sec:authentication var="secUserId" property="principal.id"/>
		</c:if>
	
		<h1>Recipes</h1>
	
		<div class="col-sm-12">
			<table class="table table-condensed table-striped">
				<thead>
					<tr>
						<!-- <th>ID</th> -->
						<th>Name</th>
						<th>Category</th>
						<th>Submitted By</th>
						<!-- <th>Source</th> -->
						<th>Favorite</th>
						<th>Share</th>
						<th>LastMade</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="recipe" items="${recipes}">

						<tr>
							<%-- <td>${recipe.id}</td> --%>
							<td>${recipe.name}</td>
							<td>${recipe.category}</td>
							<td>${recipe.firstName}&nbsp;${recipe.lastName}</td>
							<%-- <td>${recipe.source}</td> --%>
							<td><c:choose><c:when test="${recipe.favorite}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose></td>							
							<td><c:choose><c:when test="${recipe.allowShare}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose></td>
							<td>${recipe.lastMade}</td>
							<td>
								<c:choose>
									<c:when test="${recipe.allowShare}">
										<a class="btn btn-info btn-sm" href="../recipe/viewRecipe/${recipe.id}">
										<span class="glyphicon glyphicon-list-alt"></span></a>
									</c:when>
									<c:otherwise>
										<a class="btn btn-default btn-sm disabled" href="../recipe/viewRecipe/${recipe.id}">
										<span class="glyphicon glyphicon-list-alt"></span></a>
									</c:otherwise>
								</c:choose>							
							</td>
							<td>
								<c:choose>
									<c:when test="${recipe.userId eq secUserId}">
										<a class="btn btn-success btn-sm" href="../recipe/editRecipe/${recipe.id}">
										<span class="glyphicon glyphicon-pencil"></span></a>
									</c:when>
									<c:otherwise>
										<a class="btn btn-default btn-sm disabled" href="../recipe/editRecipe/${recipe.id}">
										<span class="glyphicon glyphicon-pencil"></span></a>
									</c:otherwise>
								</c:choose>							
							</td>
							<td>
								<c:choose>
									<c:when test="${recipe.userId eq secUserId}">
										<a class="btn btn-danger btn-sm" href="../recipe/deleteRecipe/${recipe.id}">
										<span class="glyphicon glyphicon-remove"></span></a>
									</c:when>
									<c:otherwise>
										<a class="btn btn-default btn-sm disabled" href="../recipe/deleteRecipe/${recipe.id}">
										<span class="glyphicon glyphicon-remove"></span></a>
									</c:otherwise>
								</c:choose>							
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>	
</body>

<!-- Placed at the end of the document so the pages load faster -->

</html>