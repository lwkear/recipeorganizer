<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<title><spring:message code="recipe.instructions.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<c:set var="currNdx" value="${recipe.currInstructSection}"/>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header"> 		
				<h3><spring:message code="recipe.instructions.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<form:form class="form-horizontal" role="form" name="instructForm" modelAttribute="recipe" autocomplete="off">
				<spring:bind path="recipe.instructSections[${currNdx}]"></spring:bind>
				<spring:bind path="recipe.instructSections[${currNdx}].name"><c:set var="nameError">${status.errorMessage}</c:set></spring:bind>
				
				<%@include file="instructionsFields.jsp" %>
								
				<div class="row">
					<div class="col-sm-12">
						<small><spring:message code="common.requiredfield"></spring:message></small>
					</div>
				</div>
				<div class="row spacer-vert-md" id="buttonDiv">
					<div class="col-sm-2 col-sm-push-5 text-center">
						<c:choose>
							<c:when test="${recipe.numInstructSections > 1 && (recipe.currInstructSection < (recipe.numInstructSections - 1))}">
								<c:set var="buttonName" value="Next Set"/>
							</c:when>
							<c:otherwise>
								<c:set var="buttonName"><spring:message code="recipe.optional.button"></spring:message></c:set>
							</c:otherwise>
						</c:choose>
						<button class="btn btn-primary row-adjust" type="submit" id="proceedBtn" name="_eventId_proceed">${buttonName}</button>
					</div>
					<div class="col-sm-2 col-sm-pull-2 text-left">
						<button class="btn btn-default row-adjust" type="submit" name="_eventId_back"><spring:message code="common.back"></spring:message></button>
					</div>
					<div class="col-sm-2 col-sm-push-6 text-right">
						<button class="btn btn-default" id="fakeSubmitCancel"><spring:message code="common.cancel"></spring:message></button>
						<button id="cancelSubmitBtn" type="submit" name="_eventId_cancel" style="display:none"></button>
					</div>
				</div>
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
				<form:hidden id="sectSeqNo" path="instructSections[${currNdx}].sequenceNo"/>
				<form:hidden id="instructSections" path="numInstructSections"/>				
				<form:hidden id="currInstructSect" path="currInstructSection"/>
				<form:hidden id="ingredSections" path="numIngredSections"/>
				<form:hidden id="currIngredSect" path="currIngredSection"/>				
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/emptyrows.js" />"></script>
<script src="<c:url value="/resources/custom/instructions.js" />"></script>

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
	<p><b>recipe.currentSection:</b>${recipe.currInstructSection}</p>
	<p><b>flow.ingredCount:</b>${ingredCount}</p>
	<p><b>flow.ingredIndex:</b>${ingredIndex}</p>
	<p><b>recipe.ingredSections:</b>${recipe.numIngredSections}</p>
	<p><b>recipe.currentSection:</b>${recipe.currIngredSection}</p> --%>
