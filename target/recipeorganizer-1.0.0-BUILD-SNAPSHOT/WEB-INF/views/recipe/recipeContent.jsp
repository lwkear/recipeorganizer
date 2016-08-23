<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="net.kear.recipeorganizer.enums.SourceType"%>

<spring:message var="yesLabel" code="common.yes"></spring:message>
<spring:message var="noLabel" code="common.no"></spring:message>

<c:set var="noPhoto"><spring:message code="common.photo.nophoto"></spring:message></c:set>

<div class="row">
	<div class="row">
		<div class="col-sm-9">
			<div class="col-sm-12">
				<h4>${recipe.description}</h4>
			</div>
			<c:if test="${not empty recipe.background}">				
				<div class="col-sm-12 spacer-vert-xs">
					<strong><spring:message code="recipe.optional.background"></spring:message></strong>
					<p>${recipe.background}</p>
				</div>
			</c:if>
			<c:if test="${not empty recipe.notes}">
				<div class="col-sm-12 spacer-vert-xs">
					<strong><spring:message code="recipe.optional.notes"></spring:message></strong>
					<p>${recipe.notes}</p>
				</div>
			</c:if>
			<div id="privateNotesSection">
			
				<%@include file="privateNotes.jsp" %>
			
			</div>
			<div class="<c:if test="${privateRecipe}">transparent</c:if>">
			<div class="row">
				<div class="col-sm-12">
					<div class="col-sm-4 spacer-vert-xs">
						<span><strong><spring:message code="recipe.basics.category"></spring:message></strong>&nbsp;&nbsp;
							<spring:message code="category.${recipe.category.name}"></spring:message></span>
					</div>
					<c:if test="${not empty recipe.servings}">
						<div class="col-sm-4 spacer-vert-xs">
							<span><strong><spring:message code="recipe.basics.servings"></spring:message></strong>&nbsp;&nbsp;${recipe.servings}</span>
						</div>
					</c:if>	
					<div class="col-sm-2 spacer-vert-xs">
						<span><strong><spring:message code="recipe.basics.share"></spring:message></strong>&nbsp;&nbsp;${recipe.allowShare ? yesLabel : noLabel}</span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">
					<c:if test="${recipe.prepHours > 0 ||
								  recipe.prepMinutes > 0}">
						<div class="col-sm-4 spacer-vert-xs">
							<span>
								<strong><spring:message code="recipe.basics.preptime"></spring:message></strong>&nbsp;&nbsp;
								<c:if test="${recipe.prepHours > 0}">${recipe.prepHours}&nbsp;<spring:message code="report.hour"></spring:message></c:if>
								<c:if test="${recipe.prepMinutes > 0}">${recipe.prepMinutes}&nbsp;<spring:message code="report.minute"></spring:message></c:if>
							</span>
						</div>
					</c:if>
					<c:if test="${recipe.totalHours > 0 ||
								  recipe.totalMinutes > 0}">
						<div class="col-sm-4 spacer-vert-xs">
							<span>
								<strong><spring:message code="recipe.basics.totaltime"></spring:message></strong>&nbsp;&nbsp;
								<c:if test="${recipe.totalHours > 0}">${recipe.totalHours}&nbsp;<spring:message code="report.hour"></spring:message></c:if>
								<c:if test="${recipe.totalMinutes > 0}">${recipe.totalMinutes}&nbsp;<spring:message code="report.minute"></spring:message></c:if>
							</span>
						</div>
					</c:if>
					<c:if test="${not empty recipe.tags}">
						<div class="col-sm-4 spacer-vert-xs">
							<span>
								<strong><spring:message code="recipe.optional.tags"></spring:message></strong>&nbsp;&nbsp;<span id="tags">${recipe.tags}</span>
							</span>
						</div>
					</c:if>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">			
					<c:if test="${not empty recipe.source}">
						<c:set var="colwidth" value="col-sm-4"></c:set>
						<c:if test="${recipe.source.type == SourceType.WEBSITE}"><c:set var="colwidth" value="col-sm-6"></c:set></c:if>
						<div class="${colwidth} spacer-vert-xs">
						<span><strong><spring:message code="recipe.optional.source"></spring:message></strong>&nbsp;&nbsp;</span>
						<c:choose>
							<c:when test="${recipe.source.type == SourceType.COOKBOOK}">
								<span>${recipe.source.cookbook}</span>
							</c:when>
							<c:when test="${recipe.source.type == SourceType.MAGAZINE}">
								<span>${recipe.source.magazine}</span>
							</c:when>
							<c:when test="${recipe.source.type == SourceType.NEWSPAPER}">
								<span>${recipe.source.newspaper}</span>
							</c:when>
							<c:when test="${recipe.source.type == SourceType.PERSON}">
								<span>${recipe.source.person}</span>
							</c:when>
							<c:when test="${recipe.source.type == SourceType.WEBSITE}">
								<span>${recipe.source.websiteUrl}</span>
							</c:when>
							<c:when test="${recipe.source.type == SourceType.OTHER}">
								<span>${recipe.source.other}</span>
							</c:when>
						</c:choose>
						</div>
						<c:if test="${not empty recipe.source.cookbookPage || not empty recipe.source.magazinePubdate || not empty recipe.source.newspaperPubdate}">
							<div class="col-sm-8 spacer-vert-xs">
							<c:choose>
								<c:when test="${recipe.source.type == SourceType.COOKBOOK}">
									<span><c:if test="${not empty recipe.source.cookbookPage && recipe.source.cookbookPage > 0}">
										<strong><spring:message code="recipe.optional.source.cookbookpage"></spring:message></strong>&nbsp;${recipe.source.cookbookPage}</c:if>
									</span>
								</c:when>
								<c:when test="${recipe.source.type == SourceType.MAGAZINE}">
									<span><c:if test="${not empty recipe.source.magazinePubdate}">
										<strong><spring:message code="recipe.optional.source.published"></spring:message></strong>&nbsp;
										<fmt:formatDate type="date" value="${recipe.source.magazinePubdate}"/></c:if>
									</span>
								</c:when>
								<c:when test="${recipe.source.type == SourceType.NEWSPAPER}">
									<span><c:if test="${not empty recipe.source.newspaperPubdate}">
										<strong><spring:message code="recipe.optional.source.published"></spring:message></strong>&nbsp;
										<fmt:formatDate type="date" value="${recipe.source.newspaperPubdate}"/></c:if>
									</span>
								</c:when>
							</c:choose>
							</div>
						</c:if>
						<c:if test="${recipe.source.type == SourceType.WEBSITE && recipe.source.recipeUrl != 'http://'}">
							<div class="col-sm-6 spacer-vert-xs">
								<span><strong><spring:message code="recipe.optional.source.recipe"></spring:message></strong>&nbsp;${recipe.source.recipeUrl}</span>
							</div>
						</c:if>
					</c:if>
				</div>
			</div>
			<div class="col-sm-12 spacer-vert-xs">
				<h4><spring:message code="recipe.ingredients.title"></spring:message></h4>
			</div>
			<div class="col-sm-12">
				<c:forEach var="section" items="${recipe.ingredSections}" varStatus="loop">
					<c:if test="${(not empty section.name) && (section.name != 'XXXX')}">
						<p><strong>${section.name}</strong></p>
					</c:if>
					<table class="table table-condensed recipe-table">
						<tbody>
							<c:forEach var="ingred" items="${section.recipeIngredients}">
							<tr>
								<td class="ingredqty">${ingred.qtyAmt}</td>
								<td>${ingred.qtyType}</td>
								<td>${ingred.ingredient.name}&nbsp;&nbsp;<c:if test="${not empty ingred.qualifier}">(${ingred.qualifier})</c:if></td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:forEach>
			</div>
			</div>
		</div>
		<div class="col-sm-3" style="height:200px;">
			<div class="col-sm-12">
			<c:choose>
				<c:when test="${not empty recipe.photoName}">
					<img class="img-responsive center-block" src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photoName}"/>" alt="${noPhoto}"/>
				</c:when>
				<c:otherwise>
					<img class="img-responsive center-block" data-src="holder.js/200x200?auto=yes&amp;text=${noPhoto}"/>
				</c:otherwise>
			</c:choose>
			</div>
		</div>
	</div>
	<div class="<c:if test="${privateRecipe}">transparent</c:if>">
	<div class="col-sm-12 spacer-vert-xs">
		<h4><spring:message code="recipe.instructions.title"></spring:message></h4>
	</div>
	<div class="col-sm-12">
		<c:forEach var="section" items="${recipe.instructSections}" varStatus="loop">
			<c:if test="${(not empty section.name) && (section.name != 'XXXX')}">
				<p><strong>${section.name}</strong></p>
			</c:if>
			<table class="table table-condensed recipe-table">
				<tbody>				
					<c:forEach var="instrct" items="${section.instructions}">
						<tr><td><strong>${instrct.sequenceNo}.</strong>&nbsp;&nbsp;${instrct.description}</td></tr>
					</c:forEach>
				</tbody>
			</table>
		</c:forEach>
	</div>
	</div>
</div>