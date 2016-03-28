<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="approvaladmin.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
		<c:if test="${not empty warningMaint}">
			<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
		</c:if>
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="approvaladmin.head"></spring:message></h3>
			</div>			
			<table class="table" id="recipeList">
				<thead>
					<tr>
						<th><spring:message code="recipe.table.name"></spring:message></th>
						<th><spring:message code="recipe.table.description"></spring:message></th>
						<th><spring:message code="recipe.table.submittedby"></spring:message></th>
						<th><spring:message code="recipe.table.submitted"></spring:message></th>
						<th><spring:message code="recipe.table.category"></spring:message></th>
						<th><spring:message code="recipe.table.source"></spring:message></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="recipe" items="${recipes}">
						<tr id="${recipe.id}">
							<td>${recipe.name}</td>
							<td>${recipe.description}</td>
							<td>${recipe.firstName} ${recipe.lastName}</td>
							<td><fmt:formatDate type="date" value="${recipe.submitted}" /></td>
							<td>${recipe.category}</td>
							<td>${recipe.sourcetype}</td>
							<td><a class="btn btn-info btn-xs" href="../recipe/viewRecipe/${recipe.id}"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.view"></spring:message>">
								<span class="glyphicon glyphicon-list-alt"></span></a>
							</td>
							<td><a class="btn btn-success btn-xs" href="../recipe/editRecipe/${recipe.id}"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.edit"></spring:message>">
								<span class="glyphicon glyphicon-pencil"></span></a>
							</td>
							<td>
								<button class="btn btn-success btn-xs" type="button" onclick="approveRecipe(${recipe.id})"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.approve"></spring:message>">
								<span class="glyphicon glyphicon-ok"></span></button>
							</td>
							<td>
								<button class="btn btn-danger btn-xs" type="button" onclick="deleteRecipe(${recipe.id}, '${recipe.name}')"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.delete"></spring:message>">
								<span class="glyphicon glyphicon-remove"></span></button>
							</td>
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
