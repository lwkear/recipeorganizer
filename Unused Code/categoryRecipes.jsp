<%-- <%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="noPhoto"><spring:message code="common.photo.nophoto"></spring:message></c:set>

<div class="col-sm-12">
	<span class="search-label"><spring:message code="recipe.table.label"></spring:message></span>
</div>
<table class="table" id="categoryRecipeList">
	<thead>
		<tr>
			<th></th>
			<th data-orderable="false"></th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="recipe" items="${recipeList}">
			<tr id="${recipe.id}">
				<td>${recipe.views}</td>
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
											data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}">
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
				<td>${recipe.name}</td>
				<td>${recipe.submitted}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<!-- include specific routines -->
<script src="<c:url value="/resources/js/jquery-2.1.3.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables/js/dataTables.bootstrap.min.js"/>"></script>
<script src="<c:url value="/resources/holder/holder.min.js"/>"></script>
<script src="<c:url value="/resources/custom/categoryrecipes.js" />"></script>
 --%>