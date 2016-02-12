<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="descplaceholder"><spring:message code="recipe.basics.description.placeholder"></spring:message></c:set>

<div class="form-group col-sm-9 <c:if test="${not empty nameError}">has-error</c:if>">
	<label class="control-label" id="nameLabel" for="recipeName">*<spring:message code="recipe.basics.name"></spring:message></label>
	<form:input type="text" class="form-control recipeName maxSize" id="recipeName" path="name" autocomplete="off" data-max="${sizeMap['Recipe.name.max']}"/>
	<span class="text-danger" id="recipeNameErrMsg">${nameError}</span>
	<span class="text-danger" id="dupeErr" style="display:none"></span>
</div>
<div class="form-group col-sm-12 <c:if test="${not empty descriptionError}">has-error</c:if>">
	<label class="control-label" for="inputDesc">*<spring:message code="recipe.basics.description"></spring:message></label>
	<form:textarea class="form-control" rows="3" id="inputDesc" placeholder="${descplaceholder}" path="description"></form:textarea>
	<span class="text-danger">${descriptionError}</span>
</div>
<div class="form-group col-sm-12">
	<div class="row">
		<label class="control-label col-sm-3 <c:if test="${not empty categoryError}">text-danger</c:if>" style="text-align: left;" 
			id="categoryLabel" for="inputCategory">*<spring:message code="recipe.basics.category"></spring:message></label>
		<label class="control-label col-sm-3 <c:if test="${not empty servingsError}">text-danger</c:if>" style="text-align: left;" 
			id="servingsLabel" for="inputServings"><spring:message code="recipe.basics.servings"></spring:message></label>
		<label class="control-label col-sm-3 <c:if test="${not empty prepTimeError}">text-danger</c:if>" style="text-align: left;"
			id="prepLabel" ><spring:message code="recipe.basics.preptime"></spring:message></label>
		<label class="control-label col-sm-3" style="text-align: left;"><spring:message code="recipe.basics.sharerecipe"></spring:message></label>
	</div>
	<div class="row">
		<form:hidden id="catID" path="category.id"/>
		<form:hidden id="catName" path="category.name"/>
		<div class="col-sm-3 <c:if test="${not empty categoryError}">has-error</c:if>">
			<select class="form-control col-sm-3 select-placeholder" id="inputCategory">
				<option value="" style="display:none"><spring:message code="recipe.basics.selectcat"></spring:message></option>
          	</select>
          	<span class="text-danger">${categoryError}</span>
		</div>
		<div class="col-sm-3 <c:if test="${not empty servingsError}">has-error</c:if>">
			<form:input type="text" class="form-control col-sm-1 maxSize" id="servings" path="servings" autocomplete="off" data-max="${sizeMap['Recipe.servings.max']}"/>
			<span class="text-danger" id="servingsErrMsg">${servingsError}</span>
		</div>
		<div class="col-sm-3">
			<div class="row">
				<label class="control-label col-sm-1 <c:if test="${not empty prepHourError}">text-danger</c:if>" for="inputPrepHour">
					<spring:message code="recipe.basics.hour"></spring:message></label>
				<div class="col-sm-4 <c:if test="${not empty prepHourError}">has-error</c:if>">
					<form:input type="text" class="form-control" id="inputPrepHour" path="prepHours" autocomplete="off"/>
					<span class="text-danger">${prepHourError}</span>
				</div>
				<label class="control-label col-sm-2 <c:if test="${not empty prepMinuteError}">text-danger</c:if>" for="inputPrepMinute">
					<spring:message code="recipe.basics.minute"></spring:message></label>
				<div class="col-sm-4 <c:if test="${not empty prepMinuteError}">has-error</c:if>">
					<form:input type="text" class="form-control" id="inputPrepMinute" path="prepMinutes" autocomplete="off"/>
					<span class="text-danger">${prepMinuteError}</span>
				</div>
			</div>
		</div>
		<div class="col-sm-3">
			<div class="radio-inline">
				<form:radiobutton value="true" path="allowShare" checked="true"/><spring:message code="common.yes"></spring:message>
			</div>
			<div class="radio-inline">
				<form:radiobutton value="false" path="allowShare" /><spring:message code="common.no"></spring:message>
			</div>
		</div>
	</div>
</div>							
