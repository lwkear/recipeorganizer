<!DOCTYPE html>
<html>
<head>

<title>Add Recipe</title>

<%@include file="../common/head.jsp"%>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

    <%-- <spring:hasBindErrors name="recipe">
    <c:set var="errorCnt">${errors.errorCount}</c:set>
    <p><b># of Errors:${errorCnt}</b></p>
    <p></p>
	<c:forEach var="error" items="${errors.allErrors}">
		<b><c:out value="${error}" /></b>
		<p></p>
	</c:forEach>
	</spring:hasBindErrors>
	<p></p>
	<p></p> --%>
	
	<!-- determine if instructions or ingredients have any errors -->
	<spring:hasBindErrors name="recipe">
		<c:if test="${errors.hasFieldErrors('instructions')}">
			<spring:bind path="recipe.instructions">
				<c:set var="instructListError">${status.errorMessage}</c:set>
		    	<%-- <c:out value="${status.errorMessage}"/>
		    	<c:out value="${status.displayValue}"/>
		    	<c:out value="${status.errorCode}"/> --%>
			</spring:bind>
			<c:set var="instructErr" value="true"/>		
		</c:if>
		<c:forEach items="${recipe.instructions}" varStatus="loop">
			<c:if test="${errors.hasFieldErrors('instructions[' += loop.index += '].description')}">
				<c:set var="instructErr" value="true"/>
			</c:if>				
		</c:forEach>
		<c:if test="${errors.hasFieldErrors('recipeIngredients')}">
			<spring:bind path="recipe.recipeIngredients">
				<c:set var="ingredListError">${status.errorMessage}</c:set>
		    	<%-- <c:out value="${status.errorMessage}"/>
		    	<c:out value="${status.displayValue}"/>
		    	<c:out value="${status.errorCode}"/> --%>
			</spring:bind>
			<c:set var="ingredErr" value="true"/>		
		</c:if>
		<c:forEach items="${recipe.recipeIngredients}" varStatus="loop">
			<c:if test="${errors.hasFieldErrors('recipeIngredients[' += loop.index += '].quantity')}">
				<c:set var="ingredQtyErr" value="true"/>
				<c:set var="ingredErr" value="true"/>
			</c:if>				
			<c:if test="${errors.hasFieldErrors('recipeIngredients[' += loop.index += '].qtyType')}">
				<c:set var="ingredQtyTypeErr" value="true"/>
				<c:set var="ingredErr" value="true"/>
			</c:if>
			<c:if test="${errors.hasFieldErrors('recipeIngredients[' += loop.index += '].ingredientId')}">
				<c:set var="ingredIdErr" value="true"/>
				<c:set var="ingredErr" value="true"/>
			</c:if>
			<c:if test="${errors.hasFieldErrors('recipeIngredients[' += loop.index += '].qualifier')}">
				<c:set var="ingredQualErr" value="true"/>
				<c:set var="ingredErr" value="true"/>
			</c:if>
		</c:forEach>
		</spring:hasBindErrors>
      
    <!-- extract applicable error messages -->
    <spring:bind path="recipe.name">
    	<c:set var="nameError">${status.errorMessage}</c:set>
    	<c:set var="nameDisplayValue">${status.displayValue}</c:set>
    	<c:set var="nameCode">${status.errorCode}</c:set>
    </spring:bind>
    <!-- TODO: EXCEPTION: move this to the server into a custom message interpolator -->
	<%-- <c:if test="${fn:contains(nameCode,'Size')}">
		<c:set var="nameLen">${fn:length(nameDisplayValue)}</c:set>
		<c:if test="${nameLen gt 0}">
			<c:set var="nameError">${nameError += " (you entered " += nameLen += ")"}</c:set>  
		</c:if>
	</c:if> --%>	
    
    <spring:bind path="recipe.category.id"><c:set var="categoryError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.servings"><c:set var="servingsError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.tags"><c:set var="tagsError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.sources[0].cookbook"><c:set var="cookbookError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.sources[0].magazine"><c:set var="magazineError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.sources[0].magazinePubdate"><c:set var="magazinePubdateError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.sources[0].newspaper"><c:set var="newspaperError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.sources[0].newspaperPubdate"><c:set var="newspaperPubdateError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.sources[0].person"><c:set var="personError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.sources[0].other"><c:set var="otherError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.sources[0].websiteUrl"><c:set var="websiteUrlError">${status.errorMessage}</c:set></spring:bind>
    <spring:bind path="recipe.sources[0].recipeUrl"><c:set var="recipeUrlError">${status.errorMessage}</c:set></spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="recipe.add.title"></spring:message></h3>
			</div>
		</div>
		<form:form class="form-horizontal" role="form" method="post" modelAttribute="recipe">
			<form:hidden id="userID" path="user.id" value="3"/>		
			<div class="row">
				<div class="col-sm-12">
				    <div class="panel-group" id="accordion">
				    	<div class="panel panel-default">
				            <div class="panel-heading">
				                <h4 class="panel-title"><a data-toggle="collapse" href="#panel1"><spring:message code="recipe.general.title"></spring:message></a></h4>
				                <span class="text-center">Some text</span>
				            </div>
				            <div id="panel1" class="panel-collapse collapse">
				            	<div class="panel-body">
									<div class="form-group col-sm-9 <c:if test="${not empty nameError}">has-error</c:if>">
										<label class="control-label" id="nameLabel" for="inputName"><spring:message code="recipe.general.name"></spring:message></label>
										<form:input type="text" class="form-control recipeName" id="inputName" path="name" autocomplete="off"/>
										<span class="text-danger">${nameError}</span>
									</div>
									<div class="form-group col-sm-12">
										<label class="control-label" for="inputDesc"><spring:message code="recipe.general.description"></spring:message></label>
										<form:textarea class="form-control" rows="3" id="inputDesc" path="description"></form:textarea>
									</div>
	
									<div class="form-group col-sm-12">
										<div class="row">
											<label class="control-label col-sm-3" style="text-align: left;" id="categoryLabel" for="inputCategory">
												<spring:message code="recipe.general.category"></spring:message></label>
											<label class="control-label col-sm-3" style="text-align: left;" id="servingsLabel" for="inputServings">
												<spring:message code="recipe.general.servings"></spring:message></label>
											<label class="control-label col-sm-2" style="text-align: left;">
												<spring:message code="recipe.general.share"></spring:message></label>
										</div>
										<div class="row">
											<form:hidden id="catID" path="category.id"/>
											<div class="col-sm-3 <c:if test="${not empty categoryError}">has-error</c:if>">
												<form:select class="form-control col-sm-3 select-placeholder" id="inputCategory" path="category.name">
													<option value="" style="display:none"><spring:message code="recipe.general.selectcat"></spring:message></option>
							            		</form:select>
							            		<span class="text-danger">${categoryError}</span>
							            	</div>
											<div class="col-sm-3 <c:if test="${not empty servingsError}">has-error</c:if>">
												<form:input type="text" class="form-control col-sm-1" id="inputServings" path="servings" autocomplete="off"/>
												<span class="text-danger">${servingsError}</span>
											</div>
											<div class="col-sm-2">
												<div class="radio-inline">
													<form:radiobutton value="true" path="allowShare" checked="true"/><spring:message code="common.yes"></spring:message>
												</div>
												<div class="radio-inline">
													<form:radiobutton value="false" path="allowShare" /><spring:message code="common.no"></spring:message>
												</div>
											</div>
										</div>
									</div>
	
				            	</div>
			            	</div>
						</div>
						<div class="panel panel-default">
				            <div class="panel-heading">
				                <h4 class="panel-title"><a data-toggle="collapse" href="#panel2"><spring:message code="recipe.ingredients.title"></spring:message></a></h4>
				            </div>
				            <div id="panel2" class="panel-collapse collapse">
				                <div class="panel-body">

									<!-- must bind the ingredients array, even on initial display -->
									<spring:bind path="recipe.recipeIngredients[0]"></spring:bind>
									<div class="form-group col-sm-12">
										<div class="form-group" style="margin-bottom:0">
											<label class="control-label col-sm-1" style="text-align: left" ><spring:message code="recipe.ingredients.quantity"></spring:message></label>
											<label class="control-label col-sm-2" style="text-align: left" ><spring:message code="recipe.ingredients.measure"></spring:message></label>
											<label class="control-label col-sm-5" style="text-align: left" ><spring:message code="recipe.ingredients.ingredient"></spring:message></label>
											<label class="control-label col-sm-4" style="text-align: left" ><spring:message code="recipe.ingredients.qualifier"></spring:message></label>
										</div>
										<c:forEach items="${recipe.recipeIngredients}" var="ingred" varStatus="loop">
											<!-- bind server-side validation errors -->
											<spring:bind path="recipe.recipeIngredients[${loop.index}].quantity"><c:set var="qtyError">${status.errorMessage}</c:set></spring:bind>
											<spring:bind path="recipe.recipeIngredients[${loop.index}].qtyType"><c:set var="qtyTypeError">${status.errorMessage}</c:set></spring:bind>
											<spring:bind path="recipe.recipeIngredients[${loop.index}].ingredientId"><c:set var="ingredError">${status.errorMessage}</c:set></spring:bind>
											<spring:bind path="recipe.recipeIngredients[${loop.index}].qualifier"><c:set var="qualError">${status.errorMessage}</c:set></spring:bind>
											<div  class="ingredGrp">
												<!-- display ajax validation errors -->						
												<div class="form-group ingredErrGrp2" style="margin-bottom:0; display:none">
													<label class="control-label col-sm-3" style="text-align: left; margin-bottom:0; "></label>
													<label class="control-label col-sm-5 text-danger jsonIgredErr" style="text-align: left; margin-bottom:0;"><b>Error</b></label>
													<label class="control-label col-sm-4" style="text-align: left; margin-bottom:0;"></label>
												</div>
												<div class="form-group">
													<form:hidden class="ingredID" id="ingredientID" path="recipeIngredients[${loop.index}].ingredientId" />
													<form:hidden class="ingredSeq" path="recipeIngredients[${loop.index}].sequenceNo"/>
													<div class="col-sm-1 <c:if test="${not empty qtyError}">has-error</c:if>">
														<form:input type="text" class="form-control ingredQty" id="inputQty" path="recipeIngredients[${loop.index}].quantity" autocomplete="off"/>
														<span class="text-danger">${qtyError}</span>
													</div>
													<div class="col-sm-2 <c:if test="${not empty qtyTypeError}">has-error</c:if>">
														<form:input type="text" class="form-control ingredQtyType" id="inputQtyType" path="recipeIngredients[${loop.index}].qtyType" />
														<span class="text-danger">${qtyTypeError}</span>
													</div>
													<div class="col-sm-5 <c:if test="${not empty ingredError}">has-error</c:if>">
														<input type="text" class="form-control ingredDesc" id="ingredient" value="${ingredientList[loop.index].name}"/>
														<span class="text-danger">${ingredError}</span>
													</div>
													<div class="col-sm-4 <c:if test="${not empty qualError}">has-error</c:if>">
														<div class="entry input-group">
															<form:input type="text" class="form-control ingredQual" id="inputQual" path="recipeIngredients[${loop.index}].qualifier" autocomplete="off"/>
															<span class="input-group-btn">
																<button class="btn btn-danger removeIngredient" type="button" style="<c:if test="${loop.last}">display:none</c:if>">
																	<span class="glyphicon glyphicon-minus"></span>
																</button>
																<button class="btn btn-success addIngredient" type="button">
																	<span class="glyphicon glyphicon-plus"></span>
																</button>
															</span>
														</div>
														<span class="text-danger">${qualError}</span>
													</div>
												</div>
											</div>
										</c:forEach>
									</div>


				                </div>
				            </div>
						</div>
				        <div class="panel panel-default">
				            <div class="panel-heading">
				                <h4 class="panel-title">
				                    <a data-toggle="collapse" href="#panel3"><spring:message code="recipe.instructions.title"></spring:message></a>
				                </h4>
				            </div>
				            <div id="panel3" class="panel-collapse collapse">
				                <div class="panel-body">

									<!-- must bind the instruction array, even on initial display -->
									<spring:bind path="recipe.instructions[0]"></spring:bind>
									<c:set var="instructplaceholder"><spring:message code="recipe.instructions.placeholder"></spring:message></c:set>
									<div class="col-sm-12">
										<c:forEach items="${recipe.instructions}" var="instruction" varStatus="loop">
											<spring:bind path="recipe.instructions[${loop.index}].description"><c:set var="instructError">${status.errorMessage}</c:set></spring:bind>
											<div class="form-group <c:if test="${not empty instructError}">has-error</c:if>">
												<div class="input-group instructGrp">
													<form:hidden class="instructSeq instruct" path="instructions[${loop.index}].sequenceNo"/>
													<form:textarea class="form-control instructDesc instruct" rows="2" path="instructions[${loop.index}].description"
														placeholder="${instructplaceholder}" />
													<span class="input-group-btn">
														<button class="btn btn-danger removeInstruction" type="button" style="<c:if test="${loop.last}">display:none</c:if>">
															<span class="glyphicon glyphicon-minus"></span>
														</button>
														<button class="btn btn-success addInstruction" type="button">
															<span class="glyphicon glyphicon-plus"></span>
														</button>
													</span>
												</div>
												<span class="text-danger">${instructError}</span>
											</div>
										</c:forEach>
									</div>

				                </div>
				            </div>
				        </div>
				        <div class="panel panel-default">
				            <div class="panel-heading">
				                <h4 class="panel-title">
				                    <a data-toggle="collapse" href="#panel4"><spring:message code="recipe.optional.title"></spring:message></a>
				                </h4>
				            </div>
				            <div id="panel4" class="panel-collapse collapse">
				                <div class="panel-body">
				                
									<c:set var="backgroundplaceholder"><spring:message code="recipe.optional.background.placeholder"></spring:message></c:set>
									<div class="form-group col-sm-12">
										<label class="control-label" for="inputBack">Background:</label>
										<form:textarea class="form-control" rows="3" id="inputBack" placeholder="${backgroundplaceholder}" path="background"></form:textarea>
									</div>
									
									<c:if test="${errors.hasFieldErrors('sources')}">
										<spring:bind path="recipe.sources[0]"></spring:bind>
									</c:if>
									<div class="form-group col-sm-12">
										<div class="form-group" style="margin-bottom:0">
											<label class="control-label col-sm-2" style="text-align: left">Source:</label>
											<label class="control-label col-sm-5 srcGroup bookGroup <c:if test="${not empty cookbookError}">text-danger</c:if>" style="text-align: left; display:none" id="cookbookLabel">
												<spring:message code="recipe.optional.source.cookbookname"></spring:message></label>
											<label class="control-label col-sm-1 srcGroup bookGroup" style="text-align: left; display:none">
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
												<form:select class="form-control col-sm-2 select-placeholder" id="inputSource" path="sources[0].type" >
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
												<form:input type="text" class="form-control col-sm-5 srcGroup bookGroup srcTA" id="inputBookName" style="display:none" path="sources[0].cookbook"/>
												<span class="text-danger">${cookbookError}</span>
											</div>
											<div class="col-sm-1 srcGroup bookGroup" style="display:none">
												<form:input type="text" class="form-control col-sm-1 srcGroup bookGroup" id="inputBookPage" style="display:none" path="sources[0].cookbookPage" autocomplete="off"/>
											</div>
											<div class="col-sm-5 srcGroup magGroup <c:if test="${not empty magazineError}">has-error</c:if>" style="display:none">
												<form:input type="text" class="form-control col-sm-5 srcGroup magGroup srcTA" id="inputMagName" style="display:none" path="sources[0].magazine"/>
												<span class="text-danger">${magazineError}</span>
											</div>
											<div class="col-sm-3 srcGroup magGroup <c:if test="${not empty magazinePubdateError}">has-error</c:if>" style="display:none">
												<form:input type="text" class="form-control col-sm-2 srcGroup magGroup" id="inputMagDate" style="display:none" path="sources[0].magazinePubdate"/>
												<span class="text-danger">${magazinePubdateError}</span>
											</div>
											<div class="col-sm-5 srcGroup newsGroup <c:if test="${not empty newspaperError}">has-error</c:if>" style="display:none">
												<form:input type="text" class="form-control col-sm-5 srcGroup newsGroup srcTA" id="inputNewsName" style="display:none" path="sources[0].newspaper"/>
												<span class="text-danger">${newspaperError}</span>
											</div>
											<div class="col-sm-3 srcGroup newsGroup <c:if test="${not empty newspaperPubdateError}">has-error</c:if>" style="display:none">
												<form:input type="text" class="form-control col-sm-2 srcGroup newsGroup" id="inputNewsDate" style="display:none" path="sources[0].newspaperPubdate"/>
												<span class="text-danger">${newspaperPubdateError}</span>
											</div>
											<div class="col-sm-4 srcGroup personGroup <c:if test="${not empty personError}">has-error</c:if>" style="display:none">
												<form:input type="text" class="form-control col-sm-4 srcGroup personGroup srcTA" id="inputPersonName" style="display:none" path="sources[0].person"/>
												<span class="text-danger">${personError}</span>
											</div>
											<div class="col-sm-5 srcGroup webGroup <c:if test="${not empty websiteUrlError}">has-error</c:if>" style="display:none">
												<form:input type="text" class="form-control col-sm-5 srcGroup webGroup srcTA" id="inputWebURL" style="display:none" path="sources[0].websiteUrl"/>
												<span class="text-danger">${websiteUrlError}</span>
											</div>
											<div class="col-sm-5 srcGroup webGroup <c:if test="${not empty recipeUrlError}">has-error</c:if>" style="display:none">
												<form:input type="text" class="form-control col-sm-5 srcGroup webGroup" id="inputRecipeURL" style="display:none" path="sources[0].recipeUrl"/>
												<span class="text-danger">${recipeUrlError}</span>
											</div>
											<div class="col-sm-7 srcGroup otherGroup <c:if test="${not empty otherError}">has-error</c:if>" style="display:none">
												<form:input type="text" class="form-control col-sm-7 srcGroup otherGroup" id="inputOtherDetails" style="display:none" path="sources[0].other" autocomplete="off"/>
												<span class="text-danger">${otherError}</span>
											</div>
										</div>
									</div>
	
									<div class="form-group col-sm-12 <c:if test="${not empty tagsError}">has-error</c:if>">
										<label class="control-label" id="tagsLabel" for="inputTags">Tags</label>
										<div class="form-group col-sm-12 <c:if test="${not empty tagsError}">has-error</c:if>" style="margin-bottom:0">
											<form:input class="form-control col-sm-6" type="text" id="inputTags" autocomplete="off" placeholder="Enter tags for this recipe" path="tags"/>
											<span class="text-danger">${tagsError}</span>
										</div>					
									</div>

									<div class="form-group col-sm-12">
										<label class="control-label" for="inputNotes">Notes:</label>
										<form:textarea class="form-control" rows="3" id="inputNotes" placeholder="Enter any special notes, tips or instructions" path="notes"></form:textarea>
									</div>
		
				                </div>
				            </div>
				        </div>
				    </div>
				</div>
			</div>
			<div class="form-group col-sm-12">
				<div class="col-sm-offset-5 col-sm-2 text-center">
					<button type="submit" class="btn btn-primary pull-left" id="save" name="save" value="Save">Save</button>
					<input type="reset" class="btn btn-default pull-right" id="reset" value="Reset">
				</div>
			</div>
		</form:form>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/recipe.js" />"></script>

</html>
