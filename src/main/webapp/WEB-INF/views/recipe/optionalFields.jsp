<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
								<div class="col-sm-1 srcGroup bookGroup <c:if test="${not empty cookbookPageError}">has-error</c:if>" style="display:none">
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
						<div class="form-group col-sm-12">
							<div class="row">
								<label class="control-label col-sm-3 <c:if test="${not empty tagsError}">text-danger</c:if>" style="text-align: left;" 
									id="tagsLabel" for="inputTags"><spring:message code="recipe.optional.tags"></spring:message></label>
								<c:choose>
									<c:when test="${not empty recipe.photoName}">
										<label class="control-label col-sm-3" style="text-align: left;" 
											id="photoLabel" for="selectedFile"><spring:message code="recipe.optional.photo"></spring:message></label>
										<label class="control-label col-sm-2" style="text-align: left;" 
											id="photoOptionsLabel" for="file"><spring:message code="common.photo.options"></spring:message></label>
										<label class="control-label col-sm-3 newphoto" style="text-align: left; display:none" 
											id="newPhotoLabel" for="file"><spring:message code="common.photo.new"></spring:message></label>
									</c:when>
									<c:otherwise>
										<label class="control-label col-sm-3" style="text-align: left;" 
											id="fileLabel" for="file"><spring:message code="recipe.optional.photo"></spring:message></label>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="row">						
								<div class="col-sm-3 <c:if test="${not empty tagsError}">has-error</c:if>" style="margin-bottom:0">
									<form:input class="form-control" type="text" id="inputTags" autocomplete="off" placeholder="${tagsplaceholder}" path="tags"/>
									<span class="text-danger">${tagsError}</span>
								</div>
								<c:choose>
									<c:when test="${not empty recipe.photoName}">												
										<div class="col-sm-3">
											<input class="form-control" type="text" id="photoName" value="${recipe.photoName}" disabled/>
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
										<div class="col-sm-4 newphoto" style="display:none">
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
										<div class="col-sm-4">
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
						<div class="form-group col-sm-12">
							<label class="control-label" for="inputNotes"><spring:message code="recipe.optional.notes"></spring:message></label>
							<form:textarea class="form-control" rows="3" id="inputNotes" placeholder="${notesplaceholder}" path="notes"></form:textarea>
						</div>
					</div>
				</div>
