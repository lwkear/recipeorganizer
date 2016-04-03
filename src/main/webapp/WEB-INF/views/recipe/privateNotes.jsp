<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty recipeNote}">
	<div class="col-sm-12 spacer-vert-xs">
		<strong><spring:message code="recipe.note.privatenotes"></spring:message></strong>
		<p class="privateNotes">${recipeNote}</p>
	</div>
</c:if>
