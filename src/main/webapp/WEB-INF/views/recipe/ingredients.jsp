<!DOCTYPE html>
<html>
<head>

<title>Ingredients</title>

<%@include file="../common/head.jsp"%>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="recipe.ingredients.title"></spring:message></h3>
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

	<p><b>flow.instructCount:</b>${instructCount}</p>
	<p><b>flow.instructIndex:</b>${instructIndex}</p>
	<p><b>recipe.instructSections:</b>${recipe.numInstructSections}</p>
	<p><b>recipe.currentSection:</b>${recipe.currInstructSection}</p>
	<p><b>flow.ingredCount:</b>${ingredCount}</p>
	<p><b>flow.ingredIndex:</b>${ingredIndex}</p>
	<p><b>recipe.ingredSections:</b>${recipe.numIngredSections}</p>
	<p><b>recipe.currentSection:</b>${recipe.currIngredSection}</p>
	
	<c:set var="currNdx" value="${recipe.currIngredSection}"/>

		<div class="col-sm-12">
			<form:form class="form-horizontal" role="form" name="ingredForm" modelAttribute="recipe">
				<spring:bind path="recipe.ingredSections[${currNdx}]"></spring:bind>		
				<div class="row">
					<c:choose>
						<c:when test="${recipe.numIngredSections > 1}">
							<div class="col-sm-12">
								<div class="form-group col-sm-3 <c:if test="${not empty nameError}">has-error</c:if>">
									<label class="control-label" id="nameLabel" for="inputName">*<spring:message code="recipe.ingredients.sectionname"></spring:message>${currNdx+1}</label>
									<form:input type="text" class="form-control" id="name" path="ingredSections[${currNdx}].name"/>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<form:hidden id="name" path="ingredSections[${currNdx}].name" value="None"/>
						</c:otherwise>
					</c:choose>
					<div class="col-sm-12">
						<spring:bind path="recipe.ingredSections[${currNdx}].recipeIngredients[0]"></spring:bind>
						<div class="form-group col-sm-12">
							<div class="form-group" style="margin-bottom:0">
								<label class="control-label col-sm-1" style="text-align: left" >*<spring:message code="recipe.ingredients.quantity"></spring:message></label>
								<label class="control-label col-sm-2" style="text-align: left" ><spring:message code="recipe.ingredients.measure"></spring:message></label>
								<label class="control-label col-sm-5" style="text-align: left" >*<spring:message code="recipe.ingredients.ingredient"></spring:message></label>
								<label class="control-label col-sm-4" style="text-align: left" ><spring:message code="recipe.ingredients.qualifier"></spring:message></label>
							</div>
							<c:forEach items="${recipe.ingredSections[currNdx].recipeIngredients}" var="ingred" varStatus="loop">
								<!-- bind server-side validation errors -->
								<spring:bind path="recipe.ingredSections[${currNdx}].recipeIngredients[${loop.index}].quantity"><c:set var="qtyError">${status.errorMessage}</c:set></spring:bind>
								<spring:bind path="recipe.ingredSections[${currNdx}].recipeIngredients[${loop.index}].qtyType"><c:set var="qtyTypeError">${status.errorMessage}</c:set></spring:bind>
								<spring:bind path="recipe.ingredSections[${currNdx}].recipeIngredients[${loop.index}].ingredientId"><c:set var="ingredError">${status.errorMessage}</c:set></spring:bind>
								<spring:bind path="recipe.ingredSections[${currNdx}].recipeIngredients[${loop.index}].qualifier"><c:set var="qualError">${status.errorMessage}</c:set></spring:bind>
								<div  class="ingredGrp">
									<!-- display ajax validation errors -->						
									<div class="form-group ingredErrGrp" style="margin-bottom:0; display:none">
										<label class="control-label col-sm-3" style="text-align: left; margin-bottom:0; "></label>
										<label class="control-label col-sm-5 text-danger jsonIgredErr" style="text-align: left; margin-bottom:0;">
											<b><spring:message code="common.error"></spring:message></b></label>
										<label class="control-label col-sm-4" style="text-align: left; margin-bottom:0;"></label>
									</div>
									<div class="form-group">
										<form:hidden class="recipeIngredID" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].id" />
										<form:hidden class="ingredID" id="ingredientID" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].ingredientId" />
										<form:hidden class="ingredSeq" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].sequenceNo"/>
										<div class="col-sm-1 <c:if test="${not empty qtyError}">has-error</c:if>">
											<form:input type="text" class="form-control ingredQty" id="inputQty" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].quantity" autocomplete="off"/>
											<span class="text-danger ingredErrGrp">${qtyError}</span>
										</div>
										<div class="col-sm-2 <c:if test="${not empty qtyTypeError}">has-error</c:if>">
											<form:input type="text" class="form-control ingredQtyType" id="inputQtyType" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].qtyType" />
											<span class="text-danger ingredErrGrp">${qtyTypeError}</span>
										</div>
										<div class="col-sm-5 <c:if test="${not empty ingredError}">has-error</c:if>">
											<input type="text" class="form-control ingredDesc" id="ingredient" value="${ingredientList[loop.index].name}"/>
											<span class="text-danger ingredErrGrp">${ingredError}</span>
										</div>
										<div class="col-sm-4 <c:if test="${not empty qualError}">has-error</c:if>">
											<div class="entry input-group">
												<form:input type="text" class="form-control ingredQual" id="inputQual" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].qualifier" autocomplete="off"/>
												<span class="input-group-btn">
													<button class="btn btn-danger removeIngredient" type="button" style="<c:if test="${loop.last}">display:none</c:if>">
														<span class="glyphicon glyphicon-minus"></span>
													</button>
													<button class="btn btn-success addIngredient" type="button">
														<span class="glyphicon glyphicon-plus"></span>
													</button>
												</span>
											</div>
											<span class="text-danger ingredErrGrp">${qualError}</span>
										</div>
									</div>
								</div>
							</c:forEach>
						</div>
	                </div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<small><spring:message code="common.requiredfield"></spring:message></small>
					</div>
				</div>
				<div class="row spacer-vert-lg">
					<div class="col-sm-2">
						<button class="btn btn-default row-adjust" type="submit" name="_eventId_back"><spring:message code="common.back"></spring:message></button>
					</div>
					<div class="col-sm-3">
					</div>
					<div class="col-sm-2">
						<c:choose>
							<c:when test="${recipe.numIngredSections > 1 && (recipe.currIngredSection < (recipe.numIngredSections - 1))}">
								<c:set var="buttonName" value="Next Set"/>
							</c:when>
							<c:otherwise>
								<c:set var="buttonName"><spring:message code="recipe.instructions.button"></spring:message></c:set>
							</c:otherwise>
						</c:choose>
						<button class="btn btn-primary row-adjust" type="submit" name="_eventId_proceed">${buttonName}</button>
					</div>
					<div class="col-sm-3">
					</div>
					<div class="col-sm-2">
						<button class="btn btn-default" type="submit" name="_eventId_cancel"><spring:message code="common.cancel"></spring:message></button>
					</div>
				</div>
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
				<form:hidden id="sectSeqNo" path="ingredSections[${currNdx}].sequenceNo"/>
				<form:hidden id="ingredSections" path="numIngredSections"/>
				<form:hidden id="currIngredSect" path="currIngredSection"/>				
				<form:hidden id="instructSections" path="numInstructSections"/>				
				<form:hidden id="currInstructSect" path="currInstructSection"/>
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/typeahead.js" />"></script>
<script src="<c:url value="/resources/custom/ingredients.js" />"></script>

</html>
			