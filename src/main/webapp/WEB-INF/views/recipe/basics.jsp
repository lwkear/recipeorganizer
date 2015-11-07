<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="recipe.name"><c:set var="nameError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.description"><c:set var="descriptionError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.servings"><c:set var="servingsError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.prepTime"><c:set var="prepError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.category.id"><c:set var="categoryError">${status.errorMessage}</c:set></spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="recipe.add.title"></spring:message></h3>
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

		<%-- <form:form class="form-horizontal" role="form" method="post" modelAttribute="recipe"> --%>		
		<form:form class="form-horizontal" role="form" modelAttribute="recipe">
			<div class="row">
				<div class="col-sm-12">
					<div class="form-group col-sm-9 <c:if test="${not empty nameError}">has-error</c:if>">
						<label class="control-label" id="nameLabel" for="inputName"><spring:message code="recipe.basics.name"></spring:message></label>
						<form:input type="text" class="form-control recipeName" id="inputName" path="name" autocomplete="off"/>
						<span class="text-danger">${nameError}</span>
					</div>
					<div class="form-group col-sm-12 <c:if test="${not empty descriptionError}">has-error</c:if>">
						<label class="control-label" for="inputDesc"><spring:message code="recipe.basics.description"></spring:message></label>
						<form:textarea class="form-control" rows="3" id="inputDesc" path="description"></form:textarea>
						<span class="text-danger">${descriptionError}</span>
					</div>

					<div class="form-group col-sm-12">
						<div class="row">
							<label class="control-label col-sm-3 <c:if test="${not empty categoryError}">text-danger</c:if>" style="text-align: left;" 
									id="categoryLabel" for="inputCategory">
								<spring:message code="recipe.basics.category"></spring:message></label>
							<label class="control-label col-sm-3 <c:if test="${not empty servingsError}">text-danger</c:if>" style="text-align: left;" 
									id="servingsLabel" for="inputServings">
								<spring:message code="recipe.basics.servings"></spring:message></label>
							<label class="control-label col-sm-2 <c:if test="${not empty prepError}">text-danger</c:if>" style="text-align: left;"
									id="prepLabel" for="inputPrepTime">
								<spring:message code="recipe.basics.preptime"></spring:message></label>
							<label class="control-label col-sm-2" style="text-align: left;">
								<spring:message code="recipe.basics.share"></spring:message></label>
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
							<div class="col-sm-2 <c:if test="${not empty prepError}">has-error</c:if>">
								<form:input type="text" class="form-control col-sm-1" id="inputPrepTime" path="prepTime" autocomplete="off"/>
								<span class="text-danger">${prepError}</span>
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
			<form:hidden id="userID" path="user.id"/>
			<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>

			<div>
				<button class="btn btn-default" type="submit" name="_eventId_proceed">Ingredients</button>
				<button class="btn btn-default" type="submit" name="_eventId_cancel">Cancel</button>
			</div>
		</form:form>
	</div>
	

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/basics.js" />"></script>

</html>
			