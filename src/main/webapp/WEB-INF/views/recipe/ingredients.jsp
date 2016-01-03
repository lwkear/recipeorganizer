<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<title><spring:message code="recipe.ingredients.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<c:set var="currNdx" value="${recipe.currIngredSection}"/>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="recipe.ingredients.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<form:form class="form-horizontal" role="form" name="ingredForm" modelAttribute="recipe" autocomplete="off">
				<spring:bind path="recipe.ingredSections[${currNdx}]"></spring:bind>
				<spring:bind path="recipe.ingredSections[${currNdx}].name"><c:set var="nameError">${status.errorMessage}</c:set></spring:bind>
				
				<%@include file="ingredientsFields.jsp" %>
				
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
<script src="<c:url value="/resources/custom/addrecipe.js" />"></script>
<script src="<c:url value="/resources/custom/ingredients.js" />"></script>

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
	
			