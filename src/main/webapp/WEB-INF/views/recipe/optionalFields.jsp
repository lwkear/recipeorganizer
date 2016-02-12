<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="net.kear.recipeorganizer.persistence.model.Source"%>

<div class="row">
	<div class="col-sm-12">
		<c:set var="backgroundplaceholder"><spring:message code="recipe.optional.background.placeholder"></spring:message></c:set>
		<c:set var="tagsplaceholder"><spring:message code="recipe.optional.tags.placeholder"></spring:message></c:set>
		<c:set var="notesplaceholder"><spring:message code="recipe.optional.notes.placeholder"></spring:message></c:set>
		<input type="text" id="typeCookbook" value="${Source.TYPE_COOKBOOK}" style="display:none">
		<input type="text" id="typeMagazine" value="${Source.TYPE_MAGAZINE}" style="display:none">
		<input type="text" id="typeNewspaper" value="${Source.TYPE_NEWSPAPER}" style="display:none">
		<input type="text" id="typePerson" value="${Source.TYPE_PERSON}" style="display:none">
		<input type="text" id="typeWebsite" value="${Source.TYPE_WEBSITE}" style="display:none">
		<input type="text" id="typeOther" value="${Source.TYPE_OTHER}" style="display:none">		
		<div class="form-group col-sm-12">
			<label class="control-label" for="inputBack"><spring:message code="recipe.optional.background"></spring:message></label>
			<form:textarea class="form-control" rows="3" id="inputBack" placeholder="${backgroundplaceholder}" path="background"></form:textarea>
		</div>
		<div class="form-group col-sm-12">
			<label class="control-label" for="inputNotes"><spring:message code="recipe.optional.notes"></spring:message></label>
			<form:textarea class="form-control" rows="3" id="inputNotes" placeholder="${notesplaceholder}" path="notes"></form:textarea>
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
				<label class="control-label col-sm-1 srcGroup bookGroup <c:if test="${not empty cookbookPageError}">text-danger</c:if>" style="text-align: left; display:none">
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
            			<form:option value="${Source.TYPE_COOKBOOK}"><spring:message code="recipe.optional.source.select.cookbook"></spring:message></form:option>
            			<form:option value="${Source.TYPE_MAGAZINE}"><spring:message code="recipe.optional.source.select.magazine"></spring:message></form:option>
            			<form:option value="${Source.TYPE_NEWSPAPER}"><spring:message code="recipe.optional.source.select.newspaper"></spring:message></form:option>
            			<form:option value="${Source.TYPE_PERSON}"><spring:message code="recipe.optional.source.select.person"></spring:message></form:option>
            			<form:option value="${Source.TYPE_WEBSITE}"><spring:message code="recipe.optional.source.select.website"></spring:message></form:option>
            			<form:option value="${Source.TYPE_OTHER}"><spring:message code="recipe.optional.source.select.other"></spring:message></form:option>
            			<form:option value="None"><spring:message code="recipe.optional.source.select.none"></spring:message></form:option>
					</form:select>
				</div>
				<div class="col-sm-5 srcGroup bookGroup <c:if test="${not empty cookbookError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-5 srcGroup bookGroup srcTA maxSize" id="cookbook" style="display:none" path="source.cookbook"
						 data-max="${sizeMap['Source.cookbook.max']}"/>
					<span class="text-danger" id="cookbookErrMsg">${cookbookError}</span>
				</div>
				<div class="col-sm-1 srcGroup bookGroup <c:if test="${not empty cookbookPageError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-1 srcGroup bookGroup maxSize" id="cookbookPage" style="display:none" path="source.cookbookPage" value="0" 
						data-max="${sizeMap['Source.cookbookPage.max']}"/>
					<span class="text-danger" id="cookbookPageErrMsg">${cookbookPageError}</span>
				</div>
				<div class="col-sm-5 srcGroup magGroup <c:if test="${not empty magazineError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-5 srcGroup magGroup srcTA maxSize" id="magazine" style="display:none" path="source.magazine"
						data-max="${sizeMap['Source.magazine.max']}"/>
					<span class="text-danger" id="magazineErrMsg">${magazineError}</span>
				</div>
				<div class="col-sm-3 srcGroup magGroup <c:if test="${not empty magazinePubdateError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-2 srcGroup magGroup" id="inputMagDate" style="display:none" path="source.magazinePubdate"/>
					<span class="text-danger">${magazinePubdateError}</span>
				</div>
				<div class="col-sm-5 srcGroup newsGroup <c:if test="${not empty newspaperError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-5 srcGroup newsGroup srcTA maxSize" id="newspaper" style="display:none" path="source.newspaper"
						data-max="${sizeMap['Source.newspaper.max']}"/>
					<span class="text-danger" id="newspaperErrMsg">${newspaperError}</span>
				</div>
				<div class="col-sm-3 srcGroup newsGroup <c:if test="${not empty newspaperPubdateError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-2 srcGroup newsGroup" id="inputNewsDate" style="display:none" path="source.newspaperPubdate"/>
					<span class="text-danger">${newspaperPubdateError}</span>
				</div>
				<div class="col-sm-4 srcGroup personGroup <c:if test="${not empty personError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-4 srcGroup personGroup srcTA maxSize" id="person" style="display:none" path="source.person"
						data-max="${sizeMap['Source.person.max']}"/>
					<span class="text-danger" id="personErrMsg">${personError}</span>
				</div>
				<div class="col-sm-5 srcGroup webGroup <c:if test="${not empty websiteUrlError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-5 srcGroup webGroup srcTA maxSize" id="websiteUrl" style="display:none" path="source.websiteUrl"
						data-max="${sizeMap['Source.websiteUrl.max']}"/>
					<span class="text-danger" id="websiteUrlErrMsg">${websiteUrlError}</span>
				</div>
				<div class="col-sm-5 srcGroup webGroup <c:if test="${not empty recipeUrlError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-5 srcGroup webGroup maxSize" id="recipeUrl" style="display:none" path="source.recipeUrl"
						data-max="${sizeMap['Source.recipeUrl.max']}"/>
					<span class="text-danger" id="recipeUrlErrMsg">${recipeUrlError}</span>
				</div>
				<div class="col-sm-7 srcGroup otherGroup <c:if test="${not empty otherError}">has-error</c:if>" style="display:none">
					<form:input type="text" class="form-control col-sm-7 srcGroup otherGroup maxSize" id="other" style="display:none" path="source.other" 
						autocomplete="off" data-max="${sizeMap['Source.other.max']}"/>
					<span class="text-danger" id="otherErrMsg">${otherError}</span>
				</div>
			</div>
		</div>
		<div class="form-group col-sm-12">
			<form:hidden id="hiddentags" path="tags" value="${recipe.tags}"/>
			<div class="row">
				<label class="control-label col-sm-5 <c:if test="${not empty tagsError}">text-danger</c:if>" style="text-align: left;" 
					id="tagsLabel" for="inputTags"><spring:message code="recipe.optional.tags"></spring:message></label>
			</div>
			<div class="row">
				<div class="col-sm-5 <c:if test="${not empty tagsError}">has-error</c:if>">
					<select class="form-control col-sm-5" id="inputTags" multiple>
						<option value="">Enter a tag</option>
					</select>
					<span class="text-danger">${tagsError}</span>
				</div>
			</div>
		</div>
		<div class="form-group col-sm-12">
			<div class="row">
				<c:choose>
					<c:when test="${not empty recipe.photoName}">
						<label class="control-label col-sm-5" style="text-align: left;" 
							id="photoLabel" for="selectedFile"><spring:message code="recipe.optional.photo"></spring:message></label>
						<label class="control-label col-sm-2" style="text-align: left;" 
							id="photoOptionsLabel" for="file"><spring:message code="common.photo.options"></spring:message></label>
						<label class="control-label col-sm-5 newphoto" style="text-align: left; display:none" 
							id="newPhotoLabel" for="file"><spring:message code="common.photo.new"></spring:message></label>
					</c:when>
					<c:otherwise>
						<label class="control-label col-sm-5" style="text-align: left;" 
							id="fileLabel" for="file"><spring:message code="recipe.optional.photo"></spring:message></label>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="row">						
				<c:choose>
					<c:when test="${not empty recipe.photoName}">												
						<div class="col-sm-5">
							<!-- NOTE: disabled inputs are not submitted to the controller, hence the need for a hidden field -->
							<form:hidden id="hiddenphoto" path="photoName" value="${recipe.photoName}"/>
							<input class="form-control" type="text" value="${recipe.photoName}" disabled/>
						</div>
						<div class="col-sm-2">
							<div class="radio">
								<label><input  type="radio" name="photoOpts" value="keep" checked><spring:message code="common.photo.keep"></spring:message></label>
							</div>
							<div class="radio">
								<label><input type="radio" name="photoOpts" value="remove"><spring:message code="common.photo.remove"></spring:message></label>
							</div>
							<div class="radio">
								<label><input type="radio" name="photoOpts" value="change"><spring:message code="common.photo.change"></spring:message></label>
							</div>
						</div>
						<div class="col-sm-5 newphoto" style="display:none">
							<div class="input-group">
								<span class="input-group-btn">
									<span class="btn btn-default btn-file">
										<spring:message code="common.selectfile"></spring:message>&hellip;
										<input type="file" id="file" name="file">
									</span>
								</span>
								<input type="text" id="photoname" class="form-control" readonly>
							</div>
						</div>
					</c:when>
					<c:otherwise>									
						<div class="col-sm-5">
							<div class="input-group">
								<span class="input-group-btn">
									<span class="btn btn-default btn-file">
										<spring:message code="common.selectfile"></spring:message>&hellip;
										<input type="file" id="file" name="file">
									</span>
								</span>
								<input type="text" id="photoname" class="form-control" disabled>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>							
	</div>
</div>
