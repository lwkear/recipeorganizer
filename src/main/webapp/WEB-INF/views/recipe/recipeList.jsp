<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title>${title} - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
		<c:if test="${not empty warningMaint}">
			<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
		</c:if>
	 	<div class="col-sm-12 title-bar">
			<div class="page-header"> 		
				<h3>${title}</h3>
			</div>
		</div>
		<div class="col-sm-12">			
			<table class="table" id="recipeList">
				<thead>
					<tr>
						<th><spring:message code="recipe.table.name"></spring:message></th>
						<th><spring:message code="recipe.table.description"></spring:message></th>
						<c:if test="${fav}">
							<th><spring:message code="recipe.table.submittedby"></spring:message></th>
						</c:if>
						<th><spring:message code="recipe.table.submitted"></spring:message></th>
						<th><spring:message code="recipe.table.category"></spring:message></th>
						<th><spring:message code="recipe.table.source"></spring:message></th>
						<c:if test="${!fav}">
							<th><spring:message code="recipe.table.status"></spring:message></th>
						</c:if>
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
							<td>${recipe.description}</td>
							<c:if test="${fav}">
								<td>${recipe.firstName} ${recipe.lastName}</td>
							</c:if>
							<td><fmt:formatDate type="date" value="${recipe.submitted}" /></td>
							<td>${recipe.category}</td>
							<td>${recipe.sourcetype}</td>
							<c:if test="${!fav}">
								<td><custom:approval status="${recipe.status}"></custom:approval></td>
							</c:if>	
							<td><a class="btn btn-info btn-xs" href="../recipe/viewRecipe/${recipe.id}"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.view"></spring:message>">
								<span class="glyphicon glyphicon-list-alt"></span></a>
							</td>
							<c:if test="${!fav}">
								<td><a class="btn btn-success btn-xs" href="../recipe/editRecipe/${recipe.id}"
									data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.edit"></spring:message>">
									<span class="glyphicon glyphicon-pencil"></span></a>
								</td>
							</c:if>
							<c:choose>
								<c:when test="${fav}">
									<td>
										<button class="btn btn-danger btn-xs" type="button" id="fav${recipe.id}" onclick="removeFavorite(${userId}, ${recipe.id}, 
										'<spring:escapeBody javaScriptEscape="true">${recipe.name}</spring:escapeBody>')"
										data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.remove"></spring:message>">
										<span class="glyphicon glyphicon-remove"></span></button>
									</td>
								</c:when>
								<c:otherwise>
									<td>
										<button class="btn btn-danger btn-xs" type="button" id="delete${recipe.id}" onclick="deleteRecipe(${recipe.id}, 
										'<spring:escapeBody javaScriptEscape="true">${recipe.name}</spring:escapeBody>')"
										data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.delete"></spring:message>">
										<span class="glyphicon glyphicon-remove"></span></button>
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

<!-- include list-specific routines -->
<script src="<c:url value="/resources/custom/recipelist.js" />"></script>

</html>
