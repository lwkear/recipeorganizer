<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="net.kear.recipeorganizer.persistence.model.Source"%>

<spring:message var="yesLabel" code="common.yes"></spring:message>
<spring:message var="noLabel" code="common.no"></spring:message>

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
			<div class=row>
				<div class="col-sm-12">
					<div class="col-sm-4 spacer-vert-xs">
						<span><strong><spring:message code="recipe.basics.category"></spring:message></strong>&nbsp;&nbsp;${recipe.category.name}</span>
					</div>
					<c:if test="${not empty recipe.servings}">
						<div class="col-sm-3 spacer-vert-xs">
							<span><strong><spring:message code="recipe.basics.servings"></spring:message></strong>&nbsp;&nbsp;${recipe.servings}</span>
						</div>
					</c:if>	
					<c:if test="${recipe.prepMinutes != 0 ||
								  recipe.prepMinutes != 0}">
						<div class="col-sm-3 spacer-vert-xs">
							<span>
								<strong><spring:message code="recipe.basics.prep"></spring:message></strong>&nbsp;&nbsp;
								<c:if test="${not empty recipe.prepHours}">${recipe.prepHours}&nbsp;<spring:message code="recipe.basics.hour"></spring:message></c:if>
								<c:if test="${not empty recipe.prepMinutes}">${recipe.prepMinutes}&nbsp;<spring:message code="recipe.basics.minute"></spring:message></c:if>
							</span>
						</div>
					</c:if>
					<div class="col-sm-2 spacer-vert-xs">
						<span><strong><spring:message code="recipe.basics.share"></spring:message></strong>&nbsp;&nbsp;${recipe.allowShare ? yesLabel : noLabel}</span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">			
					<c:if test="${not empty recipe.source}">
						<div class="col-sm-9 spacer-vert-xs">
						<span><strong><spring:message code="recipe.optional.source"></spring:message></strong>&nbsp;&nbsp;</span>
						<c:choose>
							<c:when test="${recipe.source.type == Source.TYPE_COOKBOOK}">
								<span>${recipe.source.cookbook}<c:if test="${not empty recipe.source.cookbookPage}">&nbsp;&nbsp;&nbsp;&nbsp;
									<strong><spring:message code="recipe.optional.source.cookbookpage"></spring:message></strong>&nbsp;${recipe.source.cookbookPage}</c:if>
								</span>
							</c:when>
							<c:when test="${recipe.source.type == Source.TYPE_MAGAZINE}">
								<span>${recipe.source.magazine}<c:if test="${not empty recipe.source.magazinePubdate}">&nbsp;&nbsp;&nbsp;&nbsp;
									<strong><spring:message code="recipe.optional.source.published"></spring:message></strong>&nbsp;${recipe.source.magazinePubdate}</c:if>
								</span>
							</c:when>
							<c:when test="${recipe.source.type == Source.TYPE_NEWSPAPER}">
								<span>${recipe.source.newspaper}<c:if test="${not empty recipe.source.newspaperPubdate}">&nbsp;&nbsp;&nbsp;&nbsp;
									<strong><spring:message code="recipe.optional.source.published"></spring:message></strong>&nbsp;${recipe.source.newspaperPubdate}</c:if>
								</span>
							</c:when>
							<c:when test="${recipe.source.type == Source.TYPE_PERSON}">
								<span>${recipe.source.person}</span>
							</c:when>
							<c:when test="${recipe.source.type == Source.TYPE_WEBSITE}">
								<span>${recipe.source.websiteUrl}
									<c:if test="${recipe.source.recipeUrl != 'http://'}">&nbsp;&nbsp;&nbsp;&nbsp;${recipe.source.recipeUrl}</c:if>
								</span>
							</c:when>
							<c:when test="${recipe.source.type == Source.TYPE_OTHER}">
								<span>${recipe.source.other}</span>
							</c:when>
						</c:choose>
						</div>
					</c:if>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12">			
					<c:if test="${not empty recipe.tags}">
						<div class="col-sm-6 spacer-vert-xs">
							<%-- <c:set var="tags">${recipe.tags}</c:set> --%> 
							<span>
								<strong><spring:message code="recipe.optional.tags"></spring:message></strong>&nbsp;&nbsp;<span id="tags">${recipe.tags}</span>
							</span>
						</div>
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
		<c:if test="${not empty recipe.photoName}">			
			<div class="col-sm-3">
				<span><img src="<c:url value="/recipe/photo?id=${recipe.id}&filename=${recipe.photoName}"/>" alt="" style="width:250px;height:188px;"/></span>
			</div>
		</c:if>	
	</div>
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
						<tr><td><strong>${instrct.sequenceNo}:</strong>&nbsp;&nbsp;${instrct.description}</td></tr>
					</c:forEach>
				</tbody>
			</table>
		</c:forEach>
	</div>
</div>