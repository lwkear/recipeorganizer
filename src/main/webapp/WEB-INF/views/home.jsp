<!DOCTYPE html>
<html>
<head>
	
<%@include file="common/head.jsp" %>

<title><spring:message code="menu.product"></spring:message></title>

</head>

<!-- <body role="document" class="recipe-pic"> -->
<body role="document">
	
<%@include file="common/nav.jsp" %>	

	<div class="container container-opaque">	
	 	<div class="col-sm-12">
			<div class="page-header-white"> 		
				<h1><spring:message code="title.home"></spring:message></h1>
			</div>
			<div class="lead text-center"> 		
				<h3><spring:message code="home.description1"></spring:message></h3>
			</div>
			<div class="row">
				<div class="col-sm-6 col-sm-offset-3">
					<br>
					<h4 style="color:white"><spring:message code="home.description2"></spring:message></h4>
					<br>
					<h4 style="color:white"><spring:message code="home.description3"></spring:message>
					<spring:message code="home.description4"></spring:message></h4>
					<br>
					<h4 style="color:white"><spring:message code="home.description5"></spring:message></h4>
					<br>			
				</div>
			</div>
			<div class="row">
				<div class="col-sm-2 col-sm-offset-5 text-center">
					<a class="btn btn-default" href="<c:url value="/user/signup"></c:url>" role="button">
						<spring:message code="menu.signup"></spring:message>
					</a>
				</div>
			</div>
			<c:if test="${not empty featuredRecipe}">
				<div class="col-sm-12 spacer-vert-md">
					<div class="row">
						<div class="col-sm-6 col-sm-offset-3 text-center">
							<h4 style="color:white"><strong><spring:message code="dashboard.featured"></spring:message></strong></h4>
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
									<h3 style="color:white">${featuredRecipe.name}</h3>
								</a>
							</div>
							<div class="row">
								<p>${featuredRecipe.description}</p>
							</div>
						</div>
					</div>
				</div>
			</c:if>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>
</html>
