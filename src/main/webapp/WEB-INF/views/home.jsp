<!DOCTYPE html>
<html>
<head>
	
<%@include file="common/head.jsp" %>

<title><spring:message code="menu.product"></spring:message></title>

</head>

<!-- <body role="document" class="recipe-pic"> -->
<body role="document">
	
<%@include file="common/nav.jsp" %>	

	<!-- <div class="container container-opaque"> -->	
	<div class="container container-white">
	 	<div class="col-sm-12">
			<c:set var="recentCount" value="${fn:length(recentRecipes)-1}"/>
			<c:set var="mostViewedCount" value="${fn:length(mostViewedRecipes)}"/>

			<div class="col-sm-12 spacer-vert-md">
				<div id="submittedCarousel" class="carousel slide">
					<div class="carousel-inner" role="listbox">
						<div class="item active">
							<div class="row">
								<c:forEach var="recipe" items="${recentRecipes}" begin="0" end="2" varStatus="loop">
								<%-- <c:forEach begin="1" end="3" varStatus="loop"> --%>
									<div class="col-sm-4 col-xs-6">
										<div class="slider-size">
											<img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" style="width: 100%;height: 100%;">
											<%-- <img data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/> --%>
											<div class="carousel-caption">${recipe.name}</div>
										</div>
									</div>
									<%-- <div class="col-sm-4 col-xs-6">
										<img data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/>
									</div>
									<div class="col-sm-4 col-xs-6">
										<img data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/>
									</div> --%>
								</c:forEach>
							</div>
						</div>
						<div class="item">
							<div class="row">
								<c:forEach var="recipe" items="${recentRecipes}" begin="3" end="5" varStatus="loop">
									<div class="col-sm-4 col-xs-6">
										<div class="slider-size">
											<img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" style="width: 100%;height: 100%;">
											<%-- <img data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/> --%>
											<div class="carousel-caption">${recipe.name}</div>
										</div>
									</div>
									<%-- <div class="col-sm-4 col-xs-6">
										<img data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/>
									</div>
									<div class="col-sm-4 col-xs-6">
										<img data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/>
									</div> --%>
								</c:forEach>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>
</html>

						<!-- this works for a single photo -->
						<%-- <div class="carousel-inner" role="listbox">
							<c:forEach var="recipe" items="${recentRecipes}" varStatus="loop">
								<div class="item <c:if test="${loop.index eq 0}">active</c:if>">
									<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>">
										<div class="slider-size">
											<img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>"  style="width: 100%;height: 100%;"/>	<!-- style="height: 100%;" -->
											<div class="carousel-caption desc">
												${recipe.name}
											</div>
										</div>										
									</a>
								</div>
							</c:forEach>
						</div> --%>

						 <!-- Doesn't work -->
						<%-- <div class="carousel-inner" role="listbox">
							<div class="item active">
								<c:forEach var="recipe" items="${recentRecipes}" end="2" varStatus="loop">
									<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>">
										<div style="background:url('<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>') center center; background-size:cover;">	 <!-- class="slider-size" -->
											<div class="carousel-caption">
												${recipe.name}
											</div>
										</div>
									</a>
								</c:forEach>
							</div>								
						</div> --%>
