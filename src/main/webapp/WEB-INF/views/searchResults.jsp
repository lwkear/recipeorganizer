<!DOCTYPE html>
<html>
<head>

<title>Results</title>

<%@include file="common/head.jsp" %>

</head>

<body role="document">
	
<%@include file="common/nav.jsp" %>	

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<%-- <h1><spring:message code="thankyou.title"></spring:message></h1> --%>
				<h1>Search Results</h1>
			</div>
		</div>
		<div class="list-group col-sm-12">
			<c:forEach var="recipe" items="${resultList}">
				<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>" class="list-group-item">
					<h4 class="list-group-item-heading header-blue">${recipe.name}</h4>
					<p class="list-group-item-text">${recipe.description}</p>
				</a>
			</c:forEach>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>
</html>
