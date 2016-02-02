<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>



<div class="row">
	<c:choose>
		<c:when test="${recipe.numIngredSections > 1}">
			<div class="col-sm-12">
				<div class="form-group col-sm-3 <c:if test="${not empty nameError}">has-error</c:if>">
					<label class="control-label" id="nameLabel" for="ingredSectionName">*<spring:message code="recipe.ingredients.sectionname"></spring:message>${currNdx+1}</label>
					<form:input type="text" class="form-control maxSize" id="ingredSectionName" path="ingredSections[${currNdx}].name" data-max="${sizeMap['IngredientSection.name.max']}"/>
					<span class="text-danger" id="ingredSectionNameErrMsg">${nameError}</span>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<form:hidden id="name" path="ingredSections[${currNdx}].name" value="XXXX"/>
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
				<spring:bind path="recipe.ingredSections[${currNdx}].recipeIngredients[${loop.index}].qualifier"><c:set var="qualError">${status.errorMessage}</c:set></spring:bind>
				<spring:bind path="recipe.ingredSections[${currNdx}].recipeIngredients[${loop.index}].ingredient.name"><c:set var="ingredError">${status.errorMessage}</c:set></spring:bind>
				<div class="ingredGrp">
					<!-- display ajax validation errors -->						
					<div class="form-group ingredErrGrp" style="margin-bottom:0; display:none">
						<label class="control-label col-sm-3" style="text-align: left; margin-bottom:0; "></label>
						<label class="control-label col-sm-5 text-danger jsonIgredErr" style="text-align: left; margin-bottom:0;">
							<b><spring:message code="common.error"></spring:message></b></label>
						<label class="control-label col-sm-4" style="text-align: left; margin-bottom:0;"></label>
					</div>
					<div class="form-group">
						<form:hidden class="recipeIngredID" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].id"/>
						<form:hidden class="ingredID" id="ingredientID" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].ingredient.id"/>
						<form:hidden class="ingredSeq" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].sequenceNo"/>
						<div class="col-sm-1 <c:if test="${not empty qtyError}">has-error</c:if>">
							<form:input type="text" class="form-control ingredQty maxSize" id="quantity" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].quantity" 
								autocomplete="off" data-max="${sizeMap['RecipeIngredient.quantity.max']}"/>
							<span class="text-danger ingredErrGrp" id="quantityErrMsg">${qtyError}</span>
						</div>
						<div class="col-sm-2 <c:if test="${not empty qtyTypeError}">has-error</c:if>">
							<form:input type="text" class="form-control ingredQtyType maxSize" id="qtyType" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].qtyType" 
								 data-max="${sizeMap['RecipeIngredient.qtyType.max']}"/>
							<span class="text-danger ingredErrGrp" id="qtyTypeErrMsg">${qtyTypeError}</span>
						</div>
						<div class="col-sm-5 <c:if test="${not empty ingredError}">has-error</c:if>">
							<form:input type="text" class="form-control ingredDesc maxSize" id="ingredientName" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].ingredient.name"
								 data-max="${sizeMap['Ingredient.name.max']}"/>
							<span class="text-danger ingredErrGrp" id="ingredientNameErrMsg">${ingredError}</span>
						</div>
						<div class="col-sm-4 <c:if test="${not empty qualError}">has-error</c:if>">
							<div class="entry input-group">
								<form:input type="text" class="form-control ingredQual maxSize" id="qualifier" path="ingredSections[${currNdx}].recipeIngredients[${loop.index}].qualifier" 
									placeholder="${qualplaceholder}"/>
								<%-- <span class="text-danger ingredErrGrp" id="qualifierErrMsg">${qualError}</span> --%>
								<span class="input-group-btn">
									<button class="btn btn-danger removeIngredient" type="button" style="<c:if test="${loop.last}">display:none</c:if>">
										<span class="glyphicon glyphicon-minus"></span>
									</button>
									<button class="btn btn-success addIngredient" type="button">
										<span class="glyphicon glyphicon-plus"></span>
									</button>
								</span>
							</div>
							<span class="text-danger ingredErrGrp" id="qualifierErrMsg">${qualError}</span>
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</div>
