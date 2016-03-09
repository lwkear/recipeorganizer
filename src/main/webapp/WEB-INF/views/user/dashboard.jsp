<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="dashboard.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3>
					<c:if test="${not empty user.userProfile.avatar}">
						<span><img src="<c:url value="/user/avatar?id=${user.id}&filename=${user.userProfile.avatar}"/>" alt="" style="width:50px;height:50px;"/></span>
					</c:if>
					<spring:message code="dashboard.title"></spring:message>,&nbsp;${user.firstName}
				</h3>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12">
				<div class="col-sm-3">
					<h5><strong><spring:message code="dashboard.membersince"></spring:message></strong>&nbsp;
						<span class="bold-maroon"><fmt:formatDate type="date" value="${user.dateAdded}"/></span></h5>
				</div>
				<div class="col-sm-4">
					<h5><strong><spring:message code="dashboard.memberlevel"></spring:message></strong>&nbsp;
						<span class="bold-maroon">${role.description}</span></h5>
				</div>
			</div>
		</div>
		<c:if test="${(isAuthor || isEditor || isAdmin)}">
			<div class="row">
				<div class="col-sm-12">
					<div class="col-sm-3">
						<h5><strong><spring:message code="dashboard.recipessubmitted"></spring:message></strong>&nbsp;
							<span class="bold-maroon">${recipeCount}</span></h5>
					</div>
					<div class="col-sm-6">
						<h5><strong><spring:message code="dashboard.viewcount1"></spring:message></strong>&nbsp;
							<span class="bold-maroon">${viewCount}</span>&nbsp;
							<strong><spring:message code="dashboard.viewcount2"></spring:message></strong></h5>
					</div>
				</div>
			</div>
		</c:if>
		<div class="col-sm-12">
			<div class="row">
				<div class="col-sm-6">
					<h5><strong><spring:message code="dashboard.recentlyviewed"></spring:message></strong></h5>
					<div class="list-group col-sm-12">
						<c:forEach var="recipe" items="${viewedRecipes}">
							<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>" class="list-group-item">
								<c:if test="${not empty recipe.photo}">
									<span class="pull-right"><img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" alt="" style="width:50px;height:50px;"/></span>
								</c:if>	
								<h4 class="header-blue">${recipe.name}</h4>
								<p class="list-group-item-text">${recipe.description}</p>
								<p class="clearfix"></p>										
							</a>
						</c:forEach>
						<c:if test="${empty viewedRecipes}">
							<a href="#" class="list-group-item">
								<h4 class="text-center"><em><small><spring:message code="dashboard.noviewed"></spring:message></small></em></h4>
								<p class="clearfix"></p>										
							</a>
						</c:if>
					</div>
				</div>
				<c:if test="${(isAuthor || isEditor || isAdmin)}">					
					<div class="col-sm-6">
						<h5><strong><spring:message code="dashboard.recentlysubmitted"></spring:message></strong></h5>
						<div class="list-group col-sm-12">
							<c:forEach var="recipe" items="${recentRecipes}">
								<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>" class="list-group-item">
									<c:if test="${not empty recipe.photo}">
										<span class="pull-right"><img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" alt="" style="width:50px;height:50px;"/></span>
									</c:if>	
									<h4 class="header-blue">${recipe.name}</h4>
									<p class="list-group-item-text">${recipe.description}</p>
									<p class="clearfix"></p>										
								</a>
							</c:forEach>
							<c:if test="${empty recentRecipes}">
								<a href="#" class="list-group-item">
									<h4 class="text-center"><em><small><spring:message code="dashboard.nosubmitted"></spring:message></small></em></h4>
									<p class="clearfix"></p>										
								</a>
							</c:if>
						</div>
					</div>
				</c:if>
			</div>
		</div>
	</div>
    
<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
