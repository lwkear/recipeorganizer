<!DOCTYPE html>
<html>
<head>

<title>Dashboard</title>

<%@include file="../common/head.jsp" %>

</head>

<title><spring:message code="dashboard.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3>
					<c:if test="${not empty user.userProfile.avatar}">
						<span><img src="<c:url value="/user/avatar?filename=${user.userProfile.avatar}"/>" style="width:50px;height:50px;"/></span>
					</c:if>
					<spring:message code="dashboard.title"></spring:message>,&nbsp;${user.firstName}
				</h3>
			</div>
		</div>
		<div class="col-sm-12">
			<h4><spring:message code="dashboard.membersince"></spring:message>&nbsp;
				<span><fmt:formatDate type="date" value="${user.dateAdded}" /></span>
			</h4>
		</div>
		<div class="col-sm-12">
			<h4><spring:message code="dashboard.recipessubmitted"></spring:message>&nbsp;
				<span>${recipeCount}</span>
			</h4>
		</div>
		<div class="col-sm-12">
			<%-- <h4><spring:message code="dashboard.recipessubmitted"></spring:message>&nbsp; --%>
			<h4>Your recipes have been viewed&nbsp;<span>${viewCount}</span>&nbsp;times
			</h4>
		</div>
		<div class="col-sm-12">
			<div class="row">
				<div class="col-sm-6">
					<h4><spring:message code="dashboard.recentlyviewed"></spring:message></h4>
					<div class="list-group col-sm-12">
						<c:forEach var="recipe" items="${viewedRecipes}">
							<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>" class="list-group-item">
								<c:if test="${not empty recipe.photo}">
									<span class="pull-right"><img src="<c:url value="/recipe/photo?filename=${recipe.photo}"/>" style="width:50px;height:50px;"/></span>
								</c:if>	
								<%-- <h4 class="list-group-item-heading header-blue">${recipe.name}</h4> --%>
								<h4 class="header-blue">${recipe.name}</h4>
								<p class="list-group-item-text">${recipe.description}</p>
								<p class="clearfix"></p>										
							</a>
						</c:forEach>
					</div>
				</div>					
				<div class="col-sm-6">
					<h4><spring:message code="dashboard.recentlysubmitted"></spring:message></h4>
					<div class="list-group col-sm-12">
						<c:forEach var="recipe" items="${recentRecipes}">
							<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>" class="list-group-item">
								<c:if test="${not empty recipe.photo}">
									<span class="pull-right"><img src="<c:url value="/recipe/photo?filename=${recipe.photo}"/>" style="width:50px;height:50px;"/></span>
								</c:if>	
								<h4 class="header-blue">${recipe.name}</h4>
								<p class="list-group-item-text">${recipe.description}</p>
								<p class="clearfix"></p>										
							</a>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
	</div>
    
<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
