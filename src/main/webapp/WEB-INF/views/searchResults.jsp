<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="head.search"></spring:message> - <spring:message code="menu.product"></spring:message></title>

<style type="text/css">
.dataTable > thead > tr > th[class*="sort"]:after{
    content: "" !important;
}
.toolbar {
    float: left;
}
</style>

</head>

<body role="document" onload="blurInputFocus()">
	
<%@include file="common/nav.jsp" %>	

	<c:set var="noPhoto"><spring:message code="common.photo.nophoto"></spring:message></c:set>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header"> 		
				<h3><spring:message code="title.searchresultsfor"></spring:message> "${searchTerm}"</h3>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="row">
				<div class="col-sm-3">
					<div class="col-sm-12">
						<form>
							<h4><spring:message code="recipe.table.category"></spring:message></h4>
							<div class="form-group">
								<c:forEach var="cat" items="${categories}">
									<div class="checkbox">
										<label>
											<input class="category" id="cat${cat.catId}" type="checkbox">${cat.catName} (${cat.catCount})
										</label>
									</div>
								</c:forEach>
							</div>
						</form>
					</div>
					<div class="col-sm-12">
						<form>
							<h4><spring:message code="recipe.table.source"></spring:message></h4>
							<div class="form-group">
								<c:forEach var="src" items="${sources}">
									<div class="checkbox">
										<label>
											<input class="source" id="src${src.sourceType}" type="checkbox">${src.sourceName} (${src.sourceCount})
										</label>
									</div>
								</c:forEach>
							</div>
						</form>
					</div>
				</div>
				<div class="col-sm-9">
					<div class="col-sm-12">
						<span class="search-label"><spring:message code="recipe.table.label"></spring:message></span>
					</div>
					<table class="table" id="recipeList">
						<thead>
							<tr>
								<th>Rank</th>
								<th></th>
								<th>CatId</th>
								<th>Source</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="recipe" items="${resultList}">
								<tr id="${recipe.id}">
									<td>${recipe.rank}</td>
									<td>
										<div class="media">
											<div class="media-left">
												<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>">
													<c:choose>
														<c:when test="${not empty recipe.photo}">
															<img class="media-object" style="width:96px" 
																src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photo}"/>" alt="${noPhoto}">
														</c:when>
														<c:otherwise>
															<img class="media-object" style="width:96px" 
																data-src="holder.js/96x96?auto=yes&amp;text=${noPhoto}">
														</c:otherwise>
													</c:choose>
												</a>
											</div>
											<div class="media-body">
												<a href="<c:url value="/recipe/viewRecipe/${recipe.id}"/>">
													<h4 class="media-heading std-blue">${recipe.name}</h4>
												</a>
												<div style="color:black">${recipe.description}</div>												
											</div>
										</div>
									</td>
									<td>cat${recipe.catId}</td>
									<td>src${recipe.sourceType}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<input type="hidden" id="newSearch" value="${newSearch}"/>
		</div>
	</div>

<%@include file="common/footer.jsp" %>

</body>

<!-- include list-specific routines -->
<script src="<c:url value="/resources/custom/searchlist.js" />"></script>

</html>