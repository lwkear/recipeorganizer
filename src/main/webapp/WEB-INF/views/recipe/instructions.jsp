<!DOCTYPE html>
<html>
<head>

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
		<form:form class="form-horizontal" role="form" method="post" modelAttribute="recipe">		
			<div class="row">
				<div class="col-sm-12">
				    <div class="panel-group" id="accordion">
				        <div class="panel panel-default">
				            <div class="panel-heading">
				                <h4 class="panel-title">
				                    <a data-toggle="collapse" href="#panel3"><spring:message code="recipe.instructions.title"></spring:message></a>
				                </h4>
				            </div>
				            <div id="panel3" class="panel-collapse collapse">
				                <div class="panel-body">

									<!-- must bind the instruction array, even on initial display -->
									<spring:bind path="recipe.instructions[0]"></spring:bind>
									<c:set var="instructplaceholder"><spring:message code="recipe.instructions.placeholder"></spring:message></c:set>
									<div class="col-sm-12">
										<c:forEach items="${recipe.instructions}" var="instruction" varStatus="loop">
											<spring:bind path="recipe.instructions[${loop.index}].description"><c:set var="instructError">${status.errorMessage}</c:set></spring:bind>
											<div class="form-group <c:if test="${not empty instructError}">has-error</c:if>">
												<div class="input-group instructGrp">
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
				        </div>
				    </div>
				</div>
			</div>
			<form:hidden id="userID" path="user.id"/>
			
			<div>
				<a class="btn btn-default" href="${flowExecutionUrl}&_eventId=proceed" role="button">Optional</a>
				<a class="btn btn-default" href="${flowExecutionUrl}&_eventId=cancel" role="button">Cancel</a>
			</div>
			
		</form:form>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/recipe.js" />"></script>

</html>
			