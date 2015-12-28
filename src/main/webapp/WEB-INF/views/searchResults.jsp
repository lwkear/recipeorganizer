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
				<h1><spring:message code="title.searchresults"></spring:message></h1>
			</div>
		</div>
		<div class="col-sm-12">
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
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>
</html>
