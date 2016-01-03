<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<title><spring:message code="recipe.optional.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

    <spring:bind path="recipe.tags"><c:set var="tagsError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.cookbookPage"><c:set var="cookbookPageError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.cookbook"><c:set var="cookbookError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.magazine"><c:set var="magazineError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.magazinePubdate"><c:set var="magazinePubdateError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.newspaper"><c:set var="newspaperError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.newspaperPubdate"><c:set var="newspaperPubdateError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.person"><c:set var="personError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.other"><c:set var="otherError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.websiteUrl"><c:set var="websiteUrlError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.source.recipeUrl"><c:set var="recipeUrlError">${status.errorMessage}</c:set></spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="recipe.optional.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<form:form class="form-horizontal" role="form" name="optionalForm" modelAttribute="recipe" enctype="multipart/form-data">

				<%@include file="optionalFields.jsp" %>

				<div class="row spacer-vert-lg">
					<div class="col-sm-2">
						<button class="btn btn-default" id="back" type="submit" name="_eventId_back"><spring:message code="common.back"></spring:message></button>
					</div>
					<div class="col-sm-3">
					</div>
					<div class="col-sm-2">
						<button class="btn btn-primary" type="submit" id="review" name="_eventId_proceed"><spring:message code="common.review"></spring:message></button>
					</div>
					<div class="col-sm-3">
					</div>
					<div class="col-sm-2">
						<button class="btn btn-default" type="submit" name="_eventId_cancel"><spring:message code="common.cancel"></spring:message></button>
					</div>
				</div>
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
				<form:hidden id="instructSections" path="numInstructSections"/>
				<form:hidden id="currInstructSect" path="currInstructSection"/>
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/typeahead.js" />"></script>
<script src="<c:url value="/resources/custom/optional.js" />"></script>

</html>


	<%-- <spring:hasBindErrors name="recipe">
    <c:set var="errorCnt">${errors.errorCount}</c:set>
    <p><b># of Errors:${errorCnt}</b></p>
    <p></p>
	<c:forEach var="error" items="${errors.allErrors}">
		<b><c:out value="${error}" /></b>
		<p></p>
	</c:forEach>
	</spring:hasBindErrors> --%>

	<%-- <p><b>flow.instructCount:</b>${instructCount}</p>
	<p><b>flow.instructIndex:</b>${instructIndex}</p>
	<p><b>recipe.instructSections:</b>${recipe.numInstructSections}</p>
	<p><b>recipe.currentSection:</b>${recipe.currInstructSection}</p> --%>

