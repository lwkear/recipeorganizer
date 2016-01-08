<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="head.search"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">
	
<%@include file="common/nav.jsp" %>	

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h1><spring:message code="title.searchresults"></spring:message> for "${searchTerm}"</h1>
			</div>
			<table class="table" id="recipeList">
				<thead>
					<tr>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="recipe" items="${resultList}">
						<tr>
							<td>
								<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>" class="list-group-item">
								<c:if test="${not empty recipe.photo}">
									<span class="pull-right"><img src="<c:url value="/recipe/photo?filename=${recipe.photo}"/>" style="width:75px;height:75px;"/></span>
								</c:if>	
								<h4 class="list-group-item-heading header-blue">${recipe.name}</h4>
								<p class="list-group-item-text">${recipe.description}</p>
								<p class="clearfix"></p>										
								</a>							
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>

<script type="text/javascript">

$("#recipeList").dataTable({
	language : {
		emptyTable:     "No recipes were found",
	    info:           "Showing _START_ to _END_ of _TOTAL_ recipes",
	    infoEmpty:      "Showing 0 to 0 of 0 recipes",
	    infoFiltered:	"(filtered from _MAX_ total recipes)",
	    lengthMenu:		"Show  _MENU_  recipes",
	    zeroRecords:	"No matching recipes found"
	},	
	stateSave		: true,
	fnDrawCallback: function() {
		$("#recipeList thead").remove();
	}
});

</script>

</html>



							
			<%-- <div class="col-sm-12">
				<div class="list-group col-sm-12">
					<c:forEach var="recipe" items="${resultList}">
						<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>" class="list-group-item">
							<c:if test="${not empty recipe.photo}">
								<span class="pull-right"><img src="<c:url value="/recipe/photo?filename=${recipe.photo}"/>" style="width:75px;height:75px;"/></span>
							</c:if>	
							<h4 class="list-group-item-heading header-blue">${recipe.name}</h4>
							<p class="list-group-item-text">${recipe.description}</p>
							<p class="clearfix"></p>										
						</a>
					</c:forEach>
				</div>
			</div> --%>
							