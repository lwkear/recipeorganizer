<!DOCTYPE html>
<html>
<head>

<title>ListRecipes</title>

<%@include file="../common/head.jsp" %>

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
				<%-- <h3><spring:message code="signup.title"></spring:message></h3> --%>
				<h3>Recipes</h3>
			</div>			
			<table class="table table-condensed table-striped">
				<thead>
					<tr>
						<th>Name</th>
						<th>Category</th>
						<th>Submitted By</th>
						<th>Share</th>
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
							<td><c:choose><c:when test="${recipe.allowShare}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose></td>
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

<%@include file="../common/footer.jsp" %>

</body>

<!-- Placed at the end of the document so the pages load faster -->

</html>