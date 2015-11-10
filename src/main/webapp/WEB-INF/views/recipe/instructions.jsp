<!DOCTYPE html>
<html>
<head>

<title>Instructions</title>

<%@include file="../common/head.jsp"%>

</head>

<body role="document" onload="document.instructForm.inputDesc.focus();">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="recipe.instructions.title"></spring:message></h3>
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
	
		<form:form class="form-horizontal" role="form" name="instructForm" modelAttribute="recipe">
			<div class="row">
				<div class="col-sm-12">
					<!-- must bind the instruction array, even on initial display -->
					<spring:bind path="recipe.instructions[0]"></spring:bind>
					<c:set var="instructplaceholder"><spring:message code="recipe.instructions.placeholder"></spring:message></c:set>
						<label class="control-label" id="nameLabel" for="inputName">*<spring:message code="recipe.instructions.step"></spring:message></label>
						<div class="col-sm-12">
						<c:forEach items="${recipe.instructions}" var="instruction" varStatus="loop">
							<spring:bind path="recipe.instructions[${loop.index}].description"><c:set var="instructError">${status.errorMessage}</c:set></spring:bind>
							<div class="form-group <c:if test="${not empty instructError}">has-error</c:if>">
								<div class="input-group instructGrp">
									<%-- <form:hidden class="instructId instruct" path="instructions[${loop.index}].id"/> --%>
									<form:hidden class="instructSeq instruct" path="instructions[${loop.index}].sequenceNo"/>
									<form:textarea class="form-control instructDesc instruct" id="inputDesc" rows="2" path="instructions[${loop.index}].description"
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
			<div class="row">
				<div class="col-sm-12">
					<small><spring:message code="common.requiredfield"></spring:message></small>
				</div>
			</div>
			<div class="row spacer-vert">
				<div class="col-sm-2">
					<button class="btn btn-default row-adjust" type="submit" name="_eventId_back"><spring:message code="common.back"></spring:message></button>
				</div>
				<div class="col-sm-3">
				</div>
				<div class="col-sm-2">
					<button class="btn btn-primary row-adjust" type="submit" name="_eventId_proceed"><spring:message code="recipe.optional.button"></spring:message></button>
				</div>
				<div class="col-sm-3">
				</div>
				<div class="col-sm-2">
					<button class="btn btn-default" type="submit" name="_eventId_cancel"><spring:message code="common.cancel"></spring:message></button>
				</div>
			</div>
			<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
		</form:form>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/instructions.js" />"></script>

</html>
			