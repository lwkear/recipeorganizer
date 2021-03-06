<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="recipe.done.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<c:set var="returnLabel" value="${sessionScope.returnLabel}"/>
<c:set var="returnUrl" value="${sessionScope.returnUrl}"/>

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<c:if test="${not empty returnLabel}">
					<h5><a class="btn btn-link btn-xs" href="${returnUrl}"><spring:message code="${returnLabel}"></spring:message></a></h5>
				</c:if>
				<h3><spring:message code="recipe.done.title"></spring:message></h3>
			</div>
		</div>
	 	<div class="col-sm-12 text-center">
			<h4><spring:message code="recipe.save.message1" arguments="${recipe.name}"></spring:message>
				<c:choose>
					<c:when test="${update}"><spring:message code="recipe.update.message"></spring:message></c:when>
					<c:otherwise><spring:message code="recipe.save.message2"></spring:message></c:otherwise>
				</c:choose>
			</h4>
		</div>
		<div class="col-sm-6 col-sm-offset-3 text-center spacer-vert-sm">
			<c:choose>
				<c:when test="${not recipe.allowShare}">
					<p><spring:message code="recipe.done.noshare1"></spring:message>
					<p><spring:message code="recipe.done.noshare2"></spring:message>
				</c:when>
				<c:when test="${recipe.allowShare and recipe.copyrighted}">				
					<p><spring:message code="recipe.done.questions"></spring:message>
				</c:when>
				<c:otherwise>				
					<p><spring:message code="recipe.done.sharereview"></spring:message>
					<p><spring:message code="recipe.done.questions"></spring:message>
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${recipe.allowShare and recipe.copyrighted}">
			<div class="col-sm-6 col-sm-offset-3 spacer-vert-sm">
				<p class="std-maroon"><spring:message code="recipe.done.copyright" arguments="${recipe.source.type}"></spring:message>
			</div>
		</c:if>
		<c:if test="${update}">			
			<div class="col-sm-12 spacer-vert-xs">
				<div class="form-group col-sm-2 text-center col-sm-offset-5">
					<a class="btn btn-default" href="<c:url value='/recipe/viewRecipe/${recipe.id}'/>" role="button"><spring:message code="recipe.done.viewrecipe"></spring:message></a>
				</div>
			</div>
		</c:if>
		<c:if test="${not update}">			
		 	<div class="col-sm-12 spacer-vert-sm">
				<h5 class="text-center"><spring:message code="recipe.save.message3"></spring:message></h5>
			</div>
			<div class="col-sm-12 spacer-vert-xs">
				<div class="form-group col-sm-2 text-center col-sm-offset-5">
					<a class="btn btn-default" href="<c:url value='/recipe'/>" role="button"><spring:message code="recipe.done.newrecipe"></spring:message></a>
				</div>
			</div>
		</c:if>
	</div>

<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
