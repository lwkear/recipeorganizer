<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<title><spring:message code="recipe.basics.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="recipe.name"><c:set var="nameError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.description"><c:set var="descriptionError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.servings"><c:set var="servingsError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.prepHours"><c:set var="prepHourError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.prepMinutes"><c:set var="prepMinuteError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.category.id"><c:set var="categoryError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.numIngredSections"><c:set var="ingredSectError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="recipe.numInstructSections"><c:set var="instructSectError">${status.errorMessage}</c:set></spring:bind>

	<c:if test="${not empty prepHourError}"><c:set var="prepTimeError">X</c:set></c:if>
	<c:if test="${not empty prepMinuteError}"><c:set var="prepTimeError">X</c:set></c:if>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="recipe.basics.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<form:form class="form-horizontal" name="basicsForm" role="form" modelAttribute="recipe" autocomplete="off">
				<div class="row">
					<div class="col-sm-12">
					
						<%@include file="basicsFields.jsp" %>
						
						<div class="form-group col-sm-12 spacer-vert-sm">
							<div class="row">
								<div class="col-sm-5">
									<label class="control-label" style="text-align: left;"><spring:message code="recipe.basics.ingredients.sections"></spring:message></label>
								</div>
								<div class="row col-sm-6">
									<div class="radio-inline col-sm-1">
										<input type="radio" name="ingredSet" id="ingredYes" value="true"><spring:message code="common.yes"></spring:message>
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
				<div class="row spacer-vert-md">
					<div class="col-sm-2 col-sm-push-5 text-center">
						<button class="btn btn-primary" id="proceed" type="submit" name="_eventId_proceed"><spring:message code="recipe.ingredients.button"></spring:message></button>
					</div>
					<div class="col-sm-2 col-sm-push-8 text-right">
						<button class="btn btn-default" id="fakeSubmitCancel"><spring:message code="common.cancel"></spring:message></button>
						<button id="cancelSubmitBtn" type="submit" name="_eventId_cancel" style="display:none"></button>
					</div>
				</div>
				<form:hidden id="userID" path="user.id"/>
				<form:input type="text" style="display:none" path="views"/>
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/basics.js" />"></script>

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

	
<%-- 	
					<div class="col-sm-5">
					</div>
					<div class="col-sm-2 col-sm-push-5 text-center">
						<button class="btn btn-primary" id="proceed" type="submit" name="_eventId_proceed"><spring:message code="recipe.ingredients.button"></spring:message></button>
					</div>
					<div class="col-sm-3">
					</div>
					<div class="col-sm-2 text-right">
						<button class="btn btn-default" id="fakeSubmitCancel"><spring:message code="common.cancel"></spring:message></button>
						<button id="cancelSubmitBtn" type="submit" name="_eventId_cancel" style="display:none"></button>
					</div>
	 --%>