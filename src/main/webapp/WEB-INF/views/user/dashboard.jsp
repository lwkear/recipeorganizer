<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="dashboard.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<c:url var="whatsnewUrl" value="/whatsnew"></c:url>
	<c:set var="noPhoto" value="No photo"></c:set>

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
					<h5><a class="btn btn-link btn-xs" href="<c:url value='/admin/approval'/>"><span class="badge" style="background-color:#538cc6">${recipeApprovals}</span></a>
					<strong><spring:message code="dashboard.approvalrequired"></spring:message></strong>&nbsp;						
					</h5>	
					<h5><a class="btn btn-link btn-xs" href="<c:url value='/admin/comments'/>"><span class="badge" style="background-color:#538cc6">${flaggedComments}</span></a>
					<strong><spring:message code="dashboard.flaggedcomments"></spring:message></strong>&nbsp;						
					</h5>
					<h5><a class="btn btn-link btn-xs" href="<c:url value='/admin/ingredients'/>"><span class="badge" style="background-color:#538cc6">${ingredientReviews}</span></a>
					<strong><spring:message code="dashboard.ingredientreview"></spring:message></strong>&nbsp;						
					</h5>
				</div>
			</c:if>
		</div>

		<div class="row col-sm-12">
			<div class="col-sm-9">
				<h4><em><spring:message code="dashboard.whatsnew" arguments="${whatsnewUrl}"></spring:message></em></h4>
			</div>
		</div>

		<c:set var="viewIter" value="1"/>
		<c:if test="${fn:length(viewedRecipes) gt 6}">
			<c:set var="viewIter" value="2"/>
		</c:if>

		<div class="col-sm-12 spacer-vert-xs">
			<div class="row">
				<div class="col-sm-12">
					<h4><strong><spring:message code="dashboard.recentlyviewed"></spring:message></strong></h4>
					<c:if test="${empty viewedRecipes}">
						<div class="col-sm-12">
							<h5><em><spring:message code="dashboard.noviewed"></spring:message></em></h5>
						</div>
					</c:if>
					<c:if test="${not empty viewedRecipes}">
						<div id="viewedCarousel" class="carousel slide">
							<div class="carousel-inner">
								<c:forEach var="ndx" begin="1" end="${viewIter}">
									<div class="item <c:if test="${ndx eq 1}">active</c:if>" style="height:150px;">
										<c:if test="${ndx eq 1}">
											<c:set var="startNdx" value="0"/>
											<c:set var="endNdx" value="5"/>
										</c:if>
										<c:if test="${ndx eq 2}">
											<c:set var="startNdx" value="6"/>
											<c:set var="endNdx" value="12"/>
										</c:if>
										<c:forEach var="recipe" items="${viewedRecipes}" begin="${startNdx}" end="${endNdx}" varStatus="loop">
											<div class="col-sm-2 text-center" style="height:100px; <c:if test="${not loop.last}">border-right: 1px solid #000;</c:if>">
												<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>">
													<c:choose>
														<c:when test="${not empty recipe.photo}">
															<img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" alt="${noPhoto}"/>
														</c:when>
														<c:otherwise>
															<img data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/>
														</c:otherwise>
													</c:choose>
												</a>
												<div class="carousel-caption">
													<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>">
														<h5>${recipe.name}</h5>
													</a>
												</div>
											</div>
										</c:forEach>
									</div>
								</c:forEach>								
							</div>
							<c:if test="${fn:length(viewedRecipes) gt 6}">
								<a class="left carousel-control" href="#viewedCarousel" role="button" data-slide="prev">
									<span class="icon-prev" aria-hidden="true"></span>
								</a>
								<a class="right carousel-control" href="#viewedCarousel" role="button" data-slide="next">
									<span class="icon-next" aria-hidden="true"></span>								
								</a>
							</c:if>						
						</div>
					</c:if>
				</div>
			</div>
		</div>
		
		<c:set var="submitIter" value="1"/>
		<c:if test="${fn:length(recentRecipes) gt 6}">
			<c:set var="submitIter" value="2"/>
		</c:if>
		
		<div class="col-sm-12 spacer-vert-xs">
			<div class="row">
				<div class="col-sm-12">
					<h4><strong><spring:message code="dashboard.recentlysubmitted"></spring:message></strong></h4>
					<div id="submittedCarousel" class="carousel slide">
						<div class="carousel-inner">
							<c:forEach var="ndx" begin="1" end="${submitIter}">
								<div class="item <c:if test="${ndx eq 1}">active</c:if>" style="height:150px;">
									<c:if test="${ndx eq 1}">
										<c:set var="startNdx" value="0"/>
										<c:set var="endNdx" value="5"/>
									</c:if>
									<c:if test="${ndx eq 2}">
										<c:set var="startNdx" value="6"/>
										<c:set var="endNdx" value="12"/>
									</c:if>
									<c:forEach var="recipe" items="${recentRecipes}" begin="${startNdx}" end="${endNdx}" varStatus="loop">
										<div class="col-sm-2 text-center" style="height:100px; <c:if test="${not loop.last}">border-right: 1px solid #000;</c:if>">
											<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>">
												<c:choose>
													<c:when test="${not empty recipe.photo}">
														<img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" alt="no image"/>
													</c:when>
													<c:otherwise>
														<img data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/>
													</c:otherwise>
												</c:choose>
											</a>
											<div class="carousel-caption">
												<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>">
													<h5>${recipe.name}</h5>
												</a>
											</div>
										</div>
									</c:forEach>
								</div>
							</c:forEach>								
						</div>
						<c:if test="${fn:length(recentRecipes) gt 6}">
							<a class="left carousel-control" href="#submittedCarousel" role="button" data-slide="prev">
								<span class="icon-prev" aria-hidden="true"></span>
							</a>
							<a class="right carousel-control" href="#submittedCarousel" role="button" data-slide="next">
								<span class="icon-next" aria-hidden="true"></span>
							</a>
						</c:if>						
					</div>
				</div>
			</div>
		</div>
		
		<div class="col-sm-12 spacer-vert-md">
			<div class="row">
				<div class="col-sm-6 col-sm-offset-3 text-center">
					<h4><strong><spring:message code="dashboard.featured"></spring:message></strong></h4>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-3 col-sm-offset-3" style="height:200px;">
					<a href="<c:url value="/recipe/viewRecipe/${featuredRecipe.id}"/>">
						<c:choose>
							<c:when test="${not empty featuredRecipe.photo}">
								<img class="img-responsive center-block" src="<c:url value="/recipe/photo?id=${featuredRecipe.id}&filename=${featuredRecipe.photo}"/>" alt="${noPhoto}"/>
							</c:when>
							<c:otherwise>
								<img class="img-responsive center-block" data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/>
							</c:otherwise>
						</c:choose>
					</a>
				</div>
				<div class="col-sm-3">
					<div class="row">
						<a href="<c:url value="/recipe/viewRecipe/${featuredRecipe.id}"/>">
							<h3>${featuredRecipe.name}</h3>
						</a>
					</div>
					<div class="row">
						<p>${featuredRecipe.description}</p>
					</div>
				</div>
			</div>
		</div>

		<div class="col-sm-12 spacer-vert-md">
			<div class="row">
				<div class="col-sm-6 col-sm-offset-3 text-center">
					<h4><strong><spring:message code="dashboard.mostviewed"></spring:message></strong></h4>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-3 col-sm-offset-3" style="height:200px;">
					<a href="<c:url value="/recipe/viewRecipe/${mostViewedRecipe.id}"/>">
						<c:choose>
							<c:when test="${not empty mostViewedRecipe.photo}">
								<img class="img-responsive center-block" src="<c:url value="/recipe/photo?id=${mostViewedRecipe.id}&filename=${mostViewedRecipe.photo}"/>" alt="${noPhoto}"/>
							</c:when>
							<c:otherwise>
								<img class="img-responsive center-block" data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/>
							</c:otherwise>
						</c:choose>
					</a>
				</div>
				<div class="col-sm-3">
					<div class="row">
						<a href="<c:url value="/recipe/viewRecipe/${mostViewedRecipe.id}"/>">
							<h3>${mostViewedRecipe.name}</h3>
						</a>
					</div>
					<div class="row">
						<p>${mostViewedRecipe.description}</p>
					</div>
				</div>
			</div>
		</div>
	</div>
    
<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
