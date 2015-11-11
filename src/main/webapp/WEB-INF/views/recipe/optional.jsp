<!DOCTYPE html>
<html>
<head>

<title>Optional</title>

<%@include file="../common/head.jsp"%>

</head>

<body role="document" onload="document.optionalForm.inputBack.focus();">

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

	<spring:hasBindErrors name="recipe">
    <c:set var="errorCnt">${errors.errorCount}</c:set>
    <p><b># of Errors:${errorCnt}</b></p>
    <p></p>
	<c:forEach var="error" items="${errors.allErrors}">
		<b><c:out value="${error}" /></b>
		<p></p>
	</c:forEach>
	</spring:hasBindErrors>
	<p></p>
	<p></p>

		<div class="col-sm-12">
			<form:form class="form-horizontal" role="form" name="optionalForm" modelAttribute="recipe">
				<div class="row">
					<div class="col-sm-12">
						<c:set var="backgroundplaceholder"><spring:message code="recipe.optional.background.placeholder"></spring:message></c:set>
						<c:set var="tagsplaceholder"><spring:message code="recipe.optional.tags.placeholder"></spring:message></c:set>
						<c:set var="notesplaceholder"><spring:message code="recipe.optional.notes.placeholder"></spring:message></c:set>
						<div class="form-group col-sm-12">
							<label class="control-label" for="inputBack"><spring:message code="recipe.optional.background"></spring:message></label>
							<form:textarea class="form-control" rows="3" id="inputBack" placeholder="${backgroundplaceholder}" path="background"></form:textarea>
						</div>
						<c:if test="${errors.hasFieldErrors('source')}">
							<spring:bind path="recipe.source"></spring:bind>
						</c:if>
						<div class="form-group col-sm-12">
							<div class="form-group" style="margin-bottom:0">
								<label class="control-label col-sm-2" style="text-align: left">
									<spring:message code="recipe.optional.source"></spring:message></label>
								<label class="control-label col-sm-5 srcGroup bookGroup <c:if test="${not empty cookbookError}">text-danger</c:if>" style="text-align: left; display:none" id="cookbookLabel">
									<spring:message code="recipe.optional.source.cookbookname"></spring:message></label>
								<label class="control-label col-sm-1 srcGroup bookGroup <c:if test="${not empty cookbookPageError}">text-danger</c:if>"" style="text-align: left; display:none">
									<spring:message code="recipe.optional.source.cookbookpage"></spring:message></label>
								<label class="control-label col-sm-5 srcGroup magGroup <c:if test="${not empty magazineError}">text-danger</c:if>" style="text-align: left; display:none" id="zineLabel">
									<spring:message code="recipe.optional.source.magazinename"></spring:message></label>
								<label class="control-label col-sm-3 srcGroup magGroup <c:if test="${not empty magazinePubdateError}">text-danger</c:if>" style="text-align: left; display:none" id="zineDateLabel">
									<spring:message code="recipe.optional.source.pubdate"></spring:message></label>						
								<label class="control-label col-sm-5 srcGroup newsGroup <c:if test="${not empty newspaperError}">text-danger</c:if>" style="text-align: left; display:none" id="newsLabel">
									<spring:message code="recipe.optional.source.newspapername"></spring:message></label>
								<label class="control-label col-sm-3 srcGroup newsGroup <c:if test="${not empty newspaperPubdateError}">text-danger</c:if>" style="text-align: left; display:none" id="newsDateLabel">
									<spring:message code="recipe.optional.source.pubdate"></spring:message></label>
								<label class="control-label col-sm-4 srcGroup personGroup <c:if test="${not empty personError}">text-danger</c:if>" style="text-align: left; display:none" id="personLabel">
									<spring:message code="recipe.optional.source.person"></spring:message></label>
								<label class="control-label col-sm-5 srcGroup webGroup <c:if test="${not empty websiteUrlError}">text-danger</c:if>" style="text-align: left; display:none" id="webURLLabel">
									<spring:message code="recipe.optional.source.websiteurl"></spring:message></label>
								<label class="control-label col-sm-5 srcGroup webGroup <c:if test="${not empty recipeUrlError}">text-danger</c:if>" style="text-align: left; display:none" id="recipeURLLabel">
									<spring:message code="recipe.optional.source.recipeurl"></spring:message></label>
								<label class="control-label col-sm-7 srcGroup otherGroup <c:if test="${not empty otherError}">text-danger</c:if>" style="text-align: left; display:none" id="otherLabel">
									<spring:message code="recipe.optional.source.details"></spring:message></label>
							</div>
							<div class="form-group" style="margin-bottom:0">
								<div class="col-sm-2">
									<form:select class="form-control col-sm-2 select-placeholder" id="inputSource" path="source.type" >
				            			<form:option style="display:none" value=""><spring:message code="recipe.optional.source.select.placeholder"></spring:message></form:option>
				            			<form:option value="Cookbook"><spring:message code="recipe.optional.source.select.cookbook"></spring:message></form:option>
				            			<form:option value="Magazine"><spring:message code="recipe.optional.source.select.magazine"></spring:message></form:option>
				            			<form:option value="Newspaper"><spring:message code="recipe.optional.source.select.newspaper"></spring:message></form:option>
				            			<form:option value="Person"><spring:message code="recipe.optional.source.select.person"></spring:message></form:option>
				            			<form:option value="Website"><spring:message code="recipe.optional.source.select.website"></spring:message></form:option>
				            			<form:option value="Other"><spring:message code="recipe.optional.source.select.other"></spring:message></form:option>
				            			<form:option value="None"><spring:message code="recipe.optional.source.select.none"></spring:message></form:option>
									</form:select>
								</div>
								<div class="col-sm-5 srcGroup bookGroup <c:if test="${not empty cookbookError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-5 srcGroup bookGroup srcTA" id="inputBookName" style="display:none" path="source.cookbook"/>
									<span class="text-danger">${cookbookError}</span>
								</div>
								<div class="col-sm-1 srcGroup bookGroup" <c:if test="${not empty cookbookPageError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-1 srcGroup bookGroup" id="inputBookPage" style="display:none" path="source.cookbookPage" value="0" autocomplete="off"/>
									<span class="text-danger">${cookbookPageError}</span>
								</div>
								<div class="col-sm-5 srcGroup magGroup <c:if test="${not empty magazineError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-5 srcGroup magGroup srcTA" id="inputMagName" style="display:none" path="source.magazine"/>
									<span class="text-danger">${magazineError}</span>
								</div>
								<div class="col-sm-3 srcGroup magGroup <c:if test="${not empty magazinePubdateError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-2 srcGroup magGroup" id="inputMagDate" style="display:none" path="source.magazinePubdate"/>
									<span class="text-danger">${magazinePubdateError}</span>
								</div>
								<div class="col-sm-5 srcGroup newsGroup <c:if test="${not empty newspaperError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-5 srcGroup newsGroup srcTA" id="inputNewsName" style="display:none" path="source.newspaper"/>
									<span class="text-danger">${newspaperError}</span>
								</div>
								<div class="col-sm-3 srcGroup newsGroup <c:if test="${not empty newspaperPubdateError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-2 srcGroup newsGroup" id="inputNewsDate" style="display:none" path="source.newspaperPubdate"/>
									<span class="text-danger">${newspaperPubdateError}</span>
								</div>
								<div class="col-sm-4 srcGroup personGroup <c:if test="${not empty personError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-4 srcGroup personGroup srcTA" id="inputPersonName" style="display:none" path="source.person"/>
									<span class="text-danger">${personError}</span>
								</div>
								<div class="col-sm-5 srcGroup webGroup <c:if test="${not empty websiteUrlError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-5 srcGroup webGroup srcTA" id="inputWebURL" style="display:none" path="source.websiteUrl"/>
									<span class="text-danger">${websiteUrlError}</span>
								</div>
								<div class="col-sm-5 srcGroup webGroup <c:if test="${not empty recipeUrlError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-5 srcGroup webGroup" id="inputRecipeURL" style="display:none" path="source.recipeUrl"/>
									<span class="text-danger">${recipeUrlError}</span>
								</div>
								<div class="col-sm-7 srcGroup otherGroup <c:if test="${not empty otherError}">has-error</c:if>" style="display:none">
									<form:input type="text" class="form-control col-sm-7 srcGroup otherGroup" id="inputOtherDetails" style="display:none" path="source.other" autocomplete="off"/>
									<span class="text-danger">${otherError}</span>
								</div>
							</div>
						</div>
	
						<div class="form-group col-sm-12 <c:if test="${not empty tagsError}">has-error</c:if>">
							<label class="control-label" id="tagsLabel" for="inputTags"><spring:message code="recipe.optional.tags"></spring:message></label>
							<div class="form-group col-sm-12 <c:if test="${not empty tagsError}">has-error</c:if>" style="margin-bottom:0">
								<form:input class="form-control" type="text" id="inputTags" autocomplete="off" placeholder="${tagsplaceholder}" path="tags"/>
							</div>
							<div class="col-sm-12">
								<span class="text-danger">${tagsError}</span>
							</div>												
						</div>
	
						<div class="form-group col-sm-12">
							<label class="control-label" for="inputNotes"><spring:message code="recipe.optional.notes"></spring:message></label>
							<form:textarea class="form-control" rows="3" id="inputNotes" placeholder="${notesplaceholder}" path="notes"></form:textarea>
						</div>
					</div>
				</div>
				<div class="row spacer-vert">
					<div class="col-sm-2">
						<button class="btn btn-default" type="submit" name="_eventId_back"><spring:message code="common.back"></spring:message></button>
					</div>
					<div class="col-sm-3">
					</div>
					<div class="col-sm-2">
						<button class="btn btn-primary" type="submit" id="review" name="_eventId_save"><spring:message code="common.review"></spring:message></button>
					</div>
					<div class="col-sm-3">
					</div>
					<div class="col-sm-2">
						<button class="btn btn-default" type="submit" name="_eventId_cancel"><spring:message code="common.cancel"></spring:message></button>
					</div>
				</div>
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/typeahead.js" />"></script>
<script src="<c:url value="/resources/custom/optional.js" />"></script>

</html>
			