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

	<p><b>flow.instructCount:</b>${instructCount}</p>
	<p><b>flow.instructIndex:</b>${instructIndex}</p>
	<p><b>recipe.instructSections:</b>${recipe.numInstructSections}</p>
	<p><b>recipe.currentSection:</b>${recipe.currInstructSection}</p>
	<p><b>flow.ingredCount:</b>${ingredCount}</p>
	<p><b>flow.ingredIndex:</b>${ingredIndex}</p>
	<p><b>recipe.ingredSections:</b>${recipe.numIngredSections}</p>
	<p><b>recipe.currentSection:</b>${recipe.currIngredSection}</p>

	<c:set var="currNdx" value="${recipe.currInstructSection}"/>
	
		<div class="col-sm-12">
			<form:form class="form-horizontal" role="form" name="instructForm" modelAttribute="recipe">
				<spring:bind path="recipe.instructSections[${currNdx}]"></spring:bind>
				<div class="row">
					<c:choose>
						<c:when test="${recipe.numInstructSections > 1}">
							<div class="col-sm-12">
								<div class="form-group col-sm-3 <c:if test="${not empty nameError}">has-error</c:if>">
									<label class="control-label" id="nameLabel" for="inputName">*<spring:message code="recipe.instructions.sectionname"></spring:message>${currNdx+1}</label>
									<form:input type="text" class="form-control" id="name" path="instructSections[${currNdx}].name" autocomplete="off"/>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<form:hidden id="name" path="instructSections[${currNdx}].name" value="None"/>
						</c:otherwise>
					</c:choose>
					<div class="col-sm-12">
							<c:set var="instructplaceholder"><spring:message code="recipe.instructions.placeholder"></spring:message></c:set>
							<label class="control-label" id="descLabel" for="inputDesc">*<spring:message code="recipe.instructions.step"></spring:message></label>
							<div class="col-sm-12">
								<spring:bind path="recipe.instructSections[${currNdx}].instructions[0]"></spring:bind>
								<c:forEach items="${recipe.instructSections[currNdx].instructions}" varStatus="loop">
									<spring:bind path="instructSections[${currNdx}].instructions[${loop.index}].description"><c:set var="instructError">${status.errorMessage}</c:set></spring:bind>
									<div class="form-group <c:if test="${not empty instructError}">has-error</c:if>">
										<div class="input-group instructGrp">
											<form:hidden class="instructSeq instruct" path="instructSections[${currNdx}].instructions[${loop.index}].sequenceNo"/>
											<form:textarea class="form-control instructDesc instruct" id="inputDesc" rows="2" path="instructSections[${currNdx}].instructions[${loop.index}].description"
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
										<span class="text-danger instructErr">${instructError}</span>
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
							<c:when test="${recipe.numInstructSections > 1 && (recipe.currInstructSection < (recipe.numInstructSections - 1))}">
								<c:set var="buttonName" value="Next Set"/>
							</c:when>
							<c:otherwise>
								<c:set var="buttonName"><spring:message code="recipe.optional.button"></spring:message></c:set>
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
<script src="<c:url value="/resources/custom/instructions.js" />"></script>

</html>
