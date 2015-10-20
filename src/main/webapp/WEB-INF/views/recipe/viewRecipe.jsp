<!DOCTYPE html>
<html>
<head>

<title>DisplayRecipe</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="content col-sm-12">
			<div class="page-header"> 		
				<h3>${recipe.name}</h3>
			</div>			
			<h5>Submitted by ${recipe.user.firstName}&nbsp;${recipe.user.lastName}</h5>
			<div class="row">
				<div class="col-sm-12">
					<h4>${recipe.description}</h4>
				</div>
				<div class="col-sm-12">
					<h3>Background</h3>
					<p>${recipe.background}</p>
				</div>
				<div class="col-sm-12">
					<h3>Ingredients</h3>
				</div>
					<div class="col-sm-12">
						<table class="table table-condensed table-striped" style="margin-bottom:0">
							<tbody>
								<c:forEach var="ingred" items="${recipe.recipeIngredients}">
								<tr>
									<td>${ingred.quantity}</td>
									<td>${ingred.qtyType}</td>
									<td>${ingred.ingredient.name}<c:if test="${not empty ingred.qualifier}"> (${ingred.qualifier}) </c:if></td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<div class="row">
				<div class="col-sm-12">
					<h3>Instructions</h3>
					<div class="col-sm-12">
						<table class="table table-condensed table-striped">
							<tbody>				
								<c:forEach var="instrct" items="${recipe.instructions}">
									<tr><td>${instrct.sequenceNo}: ${instrct.description}</td></tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<%-- <div class="col-sm-12">
					<h3>Notes</h3>
					<p>${recipe.notes}</p>
				</div> --%>
				<%-- <div class="col-sm-12">
					<div class="col-sm-2">
						<h4>Source: ${recipe.sources[0].type}</h4>
					</div>
					<div class="col-sm-10">
						<c:choose>
							<c:when test="${recipe.sources[0].type == 'Cookbook'}">
								<h4>${recipe.sources[0].cookbook}&nbsp;&nbsp;Page# ${recipe.sources[0].cookbookPage}</h4>
							</c:when>
							<c:when test="${recipe.sources[0].type == 'Magazine'}">
								<h4>${recipe.sources[0].magazine}&nbsp;&nbsp;Published ${recipe.sources[0].magazinePubdate}</h4>
							</c:when>
							<c:when test="${recipe.sources[0].type == 'Newspaper'}">
								<h4>${recipe.sources[0].newspaper}&nbsp;&nbsp;Published ${recipe.sources[0].newspaperPubdate}</h4>
							</c:when>
							<c:when test="${recipe.sources[0].type == 'Person'}">
								<h4>${recipe.sources[0].person}</h4>
							</c:when>
							<c:when test="${recipe.sources[0].type == 'Website'}">
								<h4>${recipe.sources[0].websiteUrl}&nbsp;&nbsp;&nbsp;&nbsp;${recipe.sources[0].recipeUrl}</h4>
							</c:when>
							<c:when test="${recipe.sources[0].type == 'Other'}">
								<h4>${recipe.sources[0].other}</h4>
							</c:when>
						</c:choose>
					</div>
				</div> --%>
				<%-- <div class="col-sm-12">
					<div class="col-sm-3">
						<h4>Category: ${recipe.category.name}</h4>
					</div>
					<div class="col-sm-3">
						<h4>Servings: ${recipe.servings}</h4>
					</div>
					<div class="col-sm-3">
						<h4>Share: <c:choose><c:when test="${recipe.allowShare}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose></h4>
					</div>
					<div class="col-sm-3">
						<h4>Favorite: <c:choose><c:when test="${recipe.favorite}">Yes</c:when><c:otherwise>No</c:otherwise></c:choose></h4>
					</div>
				</div> --%>
				<%-- <div class="col-sm-12">
					<div class="col-sm-3">
						<h4>Last Made: ${recipe.lastMade}</h4>
					</div>
					<div class="col-sm-3">
						<h4># Times Made: ${recipe.madeCount}</h4>
					</div>
					<div class="col-sm-6">
						<c:set var="tags">${recipe.tags[0]}</c:set> 
						<c:if test="${not empty tags}">
							<ul class="list-inline">
							<li><h4>Tags:</h4></li>
							<c:forEach var="tag" items="${recipe.tags}" varStatus="loop">
								<li><h4>${tag}</h4></li>
							</c:forEach>
							</ul>
						</c:if>
					</div>
				</div> --%>
			</div>
		</div>
	</div>		

<%@include file="../common/footer.jsp" %>

</body>
</html>