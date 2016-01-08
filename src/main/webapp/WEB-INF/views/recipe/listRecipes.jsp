<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title>${title} - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3>${title}</h3>
			</div>			
			<table class="table" id="recipeList">
				<thead>
					<tr>
						<th><spring:message code="recipe.table.name"></spring:message></th>
						<th><spring:message code="recipe.table.description"></spring:message></th>
						<c:if test="${fav}">
							<th><spring:message code="common.submittedby"></spring:message></th>
						</c:if>
						<th><spring:message code="recipe.table.submitted"></spring:message></th>
						<th><spring:message code="recipe.table.category"></spring:message></th>
						<th><spring:message code="recipe.table.source"></spring:message></th>
						<th data-orderable="false"></th>
						<c:if test="${!fav}">
							<th data-orderable="false"></th>
						</c:if>
						<th data-orderable="false"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="recipe" items="${recipes}">
						<tr id="${recipe.id}">
							<td>${recipe.name}</td>
							<td>${recipe.desc}</td>
							<c:if test="${fav}">
								<td>${recipe.firstName} ${recipe.lastName}</td>
							</c:if>
							<td>${recipe.submitted}</td>
							<td>${recipe.category}</td>
							<td>${recipe.sourcetype}</td>
							<td><a class="btn btn-info btn-sm" href="../recipe/viewRecipe/${recipe.id}">
								<span class="glyphicon glyphicon-list-alt"></span></a>
							</td>
							<c:if test="${!fav}">
								<td><a class="btn btn-success btn-sm" href="../recipe/editRecipe/${recipe.id}">
									<span class="glyphicon glyphicon-pencil"></span></a>
								</td>
							</c:if>
							<c:choose>
								<c:when test="${fav}">
									<td>
										<button class="btn btn-danger btn-sm" type="button" onclick="removeFavorite(${userId}, ${recipe.id}, '${recipe.name}')"><span class="glyphicon glyphicon-remove"></span></button>
									</td>
								</c:when>
								<c:otherwise>
									<td>
										<button class="btn btn-danger btn-sm" type="button" onclick="deleteRecipe(${recipe.id}, '${recipe.name}')"><span class="glyphicon glyphicon-remove"></span></button>
									</td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>	

<%@include file="../common/footer.jsp" %>

</body>

<!-- include user admin-specific routines -->
<script src="<c:url value="/resources/custom/recipelist.js" />"></script>

</html>
