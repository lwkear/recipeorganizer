<!DOCTYPE html>
<html>
<head>

<title>Instructions</title>

<%@include file="../common/head.jsp"%>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

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
	
		<form:form class="form-horizontal" role="form" name="instructForm" modelAttribute="recipe">
			<div class="row">
				<div class="col-sm-12">
					<!-- must bind the instruction array, even on initial display -->
					<spring:bind path="recipe.instructions[0]"></spring:bind>
					<c:set var="instructplaceholder"><spring:message code="recipe.instructions.placeholder"></spring:message></c:set>
					<div class="col-sm-12">
						<c:forEach items="${recipe.instructions}" var="instruction" varStatus="loop">
							<spring:bind path="recipe.instructions[${loop.index}].description"><c:set var="instructError">${status.errorMessage}</c:set></spring:bind>
							<div class="form-group <c:if test="${not empty instructError}">has-error</c:if>">
								<div class="input-group instructGrp">
									<%-- <form:hidden class="instructId instruct" path="instructions[${loop.index}].id"/> --%>
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
			<div class="row" style="margin-top:40px">
				<div class="col-sm-2">
					<button class="btn btn-default" type="submit" name="_eventId_back">Back</button>
				</div>
				<div class="col-sm-3">
				</div>
				<div class="col-sm-2">
					<button class="btn btn-primary" type="submit" name="_eventId_proceed">Optional</button>
				</div>
				<div class="col-sm-3">
				</div>
				<div class="col-sm-2">
					<button class="btn btn-default" type="submit" name="_eventId_cancel">Cancel</button>
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
			