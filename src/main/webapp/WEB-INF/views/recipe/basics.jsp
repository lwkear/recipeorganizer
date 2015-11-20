<!DOCTYPE html>
<html>
<head>

<title>Basics</title>

<%@include file="../common/head.jsp"%>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="recipe.name"><c:set var="nameError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.description"><c:set var="descriptionError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.servings"><c:set var="servingsError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.prepHours"><c:set var="prepHourError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.prepMinutes"><c:set var="prepMinuteError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.category.id"><c:set var="categoryError">${status.errorMessage}</c:set></spring:bind>

	<c:if test="${not empty prepHourError}"><c:set var="prepTimeError">X</c:set></c:if>
	<c:if test="${not empty prepMinuteError}"><c:set var="prepTimeError">X</c:set></c:if>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="recipe.basics.title"></spring:message></h3>
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
	
	<%-- <p><b>flow.instructCount:</b>${instructCount}</p>
	<p><b>flow.instructIndex:</b>${instructIndex}</p>
	<p><b>recipe.instructSections:</b>${recipe.numInstructSections}</p>
	<p><b>recipe.currentSection:</b>${recipe.currInstructSection}</p> --%>

		<div class="col-sm-12">
			<form:form class="form-horizontal" name="basicsForm" role="form" modelAttribute="recipe" autocomplete="off">
				<div class="row">
					<div class="col-sm-12">
						<div class="form-group col-sm-9 <c:if test="${not empty nameError}">has-error</c:if>">
							<label class="control-label" id="nameLabel" for="inputName">*<spring:message code="recipe.basics.name"></spring:message></label>
							<form:input type="text" class="form-control recipeName" id="inputName" path="name" autocomplete="off"/>
							<span class="text-danger">${nameError}</span>
							<span class="text-danger" id="dupeErr" style="display:none"></span>
						</div>
						<div class="form-group col-sm-12 <c:if test="${not empty descriptionError}">has-error</c:if>">
							<label class="control-label" for="inputDesc">*<spring:message code="recipe.basics.description"></spring:message></label>
							<form:textarea class="form-control" rows="3" id="inputDesc" path="description"></form:textarea>
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
								<label class="control-label col-sm-3" style="text-align: left;"><spring:message code="recipe.basics.share"></spring:message></label>
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
									<form:input type="text" class="form-control col-sm-1" id="inputServings" path="servings" autocomplete="off"/>
									<span class="text-danger">${servingsError}</span>
								</div>
								<div class="col-sm-3">	 <!-- style="margin:0; padding-left:0" -->
									<div class="row">
										<label class="control-label col-sm-1" for="inputPrepHour">	 <!-- style="padding-left: 0;" -->
											<spring:message code="recipe.basics.hour"></spring:message></label>
										<div class="col-sm-3 <c:if test="${not empty prepHourError}">has-error</c:if>">
											<form:input type="text" class="form-control" id="inputPrepHour" path="prepHours" autocomplete="off"/>
											<span class="text-danger">${prepHourError}</span>
										</div>
										<label class="control-label col-sm-2" for="inputPrepMinute">	<!-- style="padding-left: 0;" -->
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
						<div class="form-group col-sm-12 spacer-vert-sm">
							<div class="row">
								<div class="col-sm-5">
									<label class="control-label" style="text-align: left;"><spring:message code="recipe.basics.ingredients.sections"></spring:message></label>
								</div>
								<div class="row col-sm-6">
									<div class="radio-inline col-sm-1">
										<input type="radio" name="ingredSet" value="true"><spring:message code="common.yes"></spring:message>
									</div>
									<div class="radio-inline col-sm-1">
										<input type="radio" name="ingredSet" value="false" checked><spring:message code="common.no"></spring:message>
									</div>
									<div class="row col-sm-6 ingredNum" style="margin:0; padding-left:0; display:none">
										<label class="control-label col-sm-6" for="ingredSections"><spring:message code="recipe.basics.howmany"></spring:message></label>
										<div class="col-sm-4 <c:if test="${not empty ingredSectError}">has-error</c:if>">
											<form:input type="text" class="form-control" id="ingredSections" path="numIngredSections"/>
											<form:hidden id="currIngredSect" path="currIngredSection"/>
											<span class="text-danger">${ingredSectError}</span>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="form-group col-sm-12">
							<div class="row">
								<div class="col-sm-5">
									<label class="control-label" style="text-align: left;"><spring:message code="recipe.basics.instructions.sections"></spring:message></label>
								</div>
								<div class="row col-sm-6">
									<div class="radio-inline col-sm-1">
										<input type="radio" name="instructSet" id="instructYes" value="true"><spring:message code="common.yes"></spring:message>
									</div>
									<div class="radio-inline col-sm-1">
										<input type="radio" name="instructSet" value="false" checked><spring:message code="common.no"></spring:message>
									</div>
									<div class="row col-sm-6 instructNum" style="margin:0; padding-left:0; display:none">
										<label class="control-label col-sm-6" id="instructSectLabel" for="instructSections"><spring:message code="recipe.basics.howmany"></spring:message></label>
										<div class="col-sm-4 <c:if test="${not empty instructSectError}">has-error</c:if>">
											<form:input type="text" class="form-control" id="instructSections" path="numInstructSections"/>
											<form:hidden id="currInstructSect" path="currInstructSection"/>
											<span class="text-danger">${instructSectError}</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<small><spring:message code="common.requiredfield"></spring:message></small>
					</div>
				</div>
				<div class="row spacer-vert-lg">
					<div class="col-sm-5">
					</div>
					<div class="col-sm-2">
						<button class="btn btn-primary" id="proceed" type="submit" name="_eventId_proceed"><spring:message code="recipe.ingredients.button"></spring:message></button>
					</div>
					<div class="col-sm-3">
					</div>
					<div class="col-sm-2">
						<button class="btn btn-default" type="submit" name="_eventId_cancel"><spring:message code="common.cancel"></spring:message></button>
					</div>
				</div>
				<form:hidden id="userID" path="user.id"/>
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/basics.js" />"></script>

</html>
