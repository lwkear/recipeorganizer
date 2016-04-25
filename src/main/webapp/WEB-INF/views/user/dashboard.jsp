<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="dashboard.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header">
				<h3>
					<c:if test="${not empty user.userProfile.avatar}">
						<span><img src="<c:url value="/user/avatar?id=${user.id}&filename=${user.userProfile.avatar}"/>" alt="" style="width:50px;height:50px;"/></span>
					</c:if>
					<spring:message code="dashboard.title"></spring:message>,&nbsp;${user.firstName}
				</h3>
			</div>
		</div>
		<c:if test="${not empty nextMaint}">
			<div class="row">
				<h5 class="bold-maroon text-center"><em>${nextMaint}</em></h5>
			</div>
		</c:if>
		<c:if test="${not empty pswdExpire}">
			<div class="row">
				<div class="col-sm-12">
					<div class="col-sm-12">
						<h5 class="bold-maroon text-center"><em>${pswdExpire}</em></h5>
					</div>
				</div>
			</div>
		</c:if>

		<div class="row col-sm-12">
			<div class="col-sm-4">
				<h5><strong><spring:message code="dashboard.membersince"></spring:message></strong>&nbsp;
					<span class="bold-maroon"><fmt:formatDate type="date" value="${user.dateAdded}"/></span></h5>
				<h5><strong><spring:message code="dashboard.memberlevel"></spring:message></strong>&nbsp;
					<span class="bold-maroon"><spring:message code="roles.${role.name}"></spring:message></span></h5>
			</div>
			<c:if test="${(isAuthor || isEditor || isAdmin)}">
				<div class="col-sm-4">
					<h5><strong><spring:message code="dashboard.recipessubmitted"></spring:message></strong>&nbsp;
						<span class="bold-maroon">${recipeCount}</span></h5>
					<h5><strong><spring:message code="dashboard.viewcount1" arguments=""></spring:message></strong>&nbsp;
						<span class="bold-maroon">${viewCount}</span>&nbsp;
						<strong><spring:message code="dashboard.viewcount2"></spring:message></strong></h5>
				</div>
			</c:if>
			<c:if test="${(isEditor || isAdmin)}">
				<div class="col-sm-4">
					<h5><a class="btn btn-link btn-xs" href="../admin/approval"><span class="badge" style="background-color:#337ab7">${recipeApprovals}</span></a>
					<strong><spring:message code="dashboard.approvalrequired"></spring:message></strong>&nbsp;						
					</h5>	
					<h5><a class="btn btn-link btn-xs" href="../admin/comments"><span class="badge" style="background-color:#337ab7">${flaggedComments}</span></a>
					<strong><spring:message code="dashboard.flaggedcomments"></spring:message></strong>&nbsp;						
					</h5>
					<h5><a class="btn btn-link btn-xs" href="../admin/ingredients"><span class="badge" style="background-color:#337ab7">${ingredientReviews}</span></a>
					<strong><spring:message code="dashboard.ingredientreview"></spring:message></strong>&nbsp;						
					</h5>
				</div>
			</c:if>
		</div>

		<div class="col-sm-12">
			<div class="row">
				<div class="col-sm-6">
					<h5><strong><spring:message code="dashboard.recentlyviewed"></spring:message></strong></h5>
					<div class="list-group col-sm-12">
						<c:forEach var="recipe" items="${viewedRecipes}">
							<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>" class="list-group-item">
								<c:if test="${not empty recipe.photo}">
									<span class="pull-right"><img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" alt="" style="width:36px;height:36px;"/></span>
								</c:if>	
								<h5 class="header-blue">${recipe.name}</h5>
								<p class="list-group-item-text">${recipe.description}</p>
								<p class="clearfix"></p>										
							</a>
						</c:forEach>
						<c:if test="${empty viewedRecipes}">
							<a href="#" class="list-group-item">
								<h5 class="text-center"><em><small><spring:message code="dashboard.noviewed"></spring:message></small></em></h5>
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
										<span class="pull-right"><img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" alt="" style="width:36px;height:36px;"/></span>
									</c:if>	
									<h5 class="header-blue">${recipe.name}</h5>
									<p class="list-group-item-text">${recipe.description}</p>
									<p class="clearfix"></p>										
								</a>
							</c:forEach>
							<c:if test="${empty recentRecipes}">
								<a href="#" class="list-group-item">
									<h5 class="text-center"><em><small><spring:message code="dashboard.nosubmitted"></spring:message></small></em></h5>
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
