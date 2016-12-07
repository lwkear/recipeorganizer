<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="maintenance.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="maintenanceDto.startTime"><c:set var="startTimeError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="maintenanceDto.duration"><c:set var="durationError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="maintenanceDto.emergencyDuration"><c:set var="emergencyDurationError">${status.errorMessage}</c:set></spring:bind>

	<div class="container container-white">
	 	<div class="col-sm-12 title-bar">
			<div class="page-header"> 		
				<h3><spring:message code="maintenance.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<form:form class="form-horizontal" role="form" method="post" modelAttribute="maintenanceDto">
			<div class="col-sm-12">
				<div class="text-center bold-maroon"><h5>${maintWindow}</h5></div>
				<div class="row col-sm-offset-4 spacer-vert-xs">				
					<div class="row">
						<div class="col-sm-5">
		            		<label class="control-label" for="maintenanceEnabled"><spring:message code="maintenance.enablemaintenance"></spring:message></label>
		            	</div>
						<div class="radio-inline">
							<form:radiobutton value="true" path="maintenanceEnabled"/><spring:message code="common.yes"></spring:message>
						</div>
						<div class="radio-inline">
							<form:radiobutton value="false" path="maintenanceEnabled"/><spring:message code="common.no"></spring:message>
						</div>
					</div>
				</div>
				<div class="row col-sm-offset-4 spacer-vert-xs">
					<div class="row">
						<div class="col-sm-4">
							<div class="row <c:if test="${not empty startTimeError}">has-error</c:if>">
								<div class="col-sm-12">
				            		<label class="control-label" for="startTime"><spring:message code="maintenance.startTime"></spring:message></label>
						    	</div>
						        <div class="col-sm-5">
					                <form:input class="form-control" type="text" id="startTime" path="startTime" autocomplete="off"/> 
					            </div>
							</div>
							<span class="text-danger" id="startTimeErrMsg">${startTimeError}</span>
					        <div class="row <c:if test="${not empty durationError}">has-error</c:if>">
					        	<div class="col-sm-12">
			        				<label class="control-label" for="duration"><spring:message code="maintenance.duration"></spring:message></label>
			        			</div>
					            <div class="col-sm-4">				            	
					                <form:input class="form-control" type="text" id="duration" path="duration" autocomplete="off"/>
					            </div>
					        </div>
					        <span class="text-danger" id="durationErrMsg">${durationError}</span>				        
					    </div>
						<div class="col-sm-6">
							<div class="row">
								<div class="row col-sm-12">
									<label class="control-label"><spring:message code="maintenance.daysOfWeek"></spring:message></label>
								</div>
							</div>
							<div class="row col-sm-12">
								<div class="form-group" id="dayGroup">
									<div class="col-sm-12">
										<form:checkboxes element="div class='col-sm-12 checkbox'" items="${dayMap}" path="daysOfWeek"/>
									</div>
								</div>
							</div>					
						</div>
					</div>
				</div>
			</div>
			<div class="col-sm-12">
		    	<div class="col-sm-offset-3">
		    		<div class="row">
			    		<div class="col-sm-8">
			    			<hr class="hr-black">
			    		</div>
			    	</div>
		    	</div>
		    </div>
			<div class="col-sm-12">
		    	<div class="row col-sm-offset-4">
					<div class="col-sm-4">
						<div class="row">
			            	<label class="control-label" for="emergencyMaintenance"><spring:message code="maintenance.emergency"></spring:message></label>
				        </div>
				        <div class="row">
							<div class="radio-inline">
								<form:radiobutton value="true" path="emergencyMaintenance"/><spring:message code="common.yes"></spring:message>
							</div>
							<div class="radio-inline">
								<form:radiobutton value="false" path="emergencyMaintenance"/><spring:message code="common.no"></spring:message>
							</div>
				        </div>
					</div>
					<div class="col-sm-4 <c:if test="${not empty emergencyDurationError}">has-error</c:if>">
				        <div class="row">
			        		<label class="control-label" for="emergencyDuration"><spring:message code="maintenance.emergency.duration"></spring:message></label>
				        </div>
				        <div class="row">
				            <div class="row col-sm-4">				            	
				                <form:input class="form-control" type="text" id="duration" path="emergencyDuration" autocomplete="off"/>
				            </div>
				        </div>
				        <div class="row">
				        	<span class="text-danger" id="emergencyDurationErrMsg">${emergencyDurationError}</span>
				        </div>
					</div>
		    	</div>
		    </div>
			<div class="col-sm-12">				
				<div class="form-group col-sm-12 spacer-vert-sm">
					<div class="col-sm-offset-5 col-sm-2 text-center">
						<button type="submit" class="btn btn-primary" id="btnSubmit" name="btnSubmit"><spring:message code="common.save"></spring:message></button>
					</div>
				</div>
			</div>
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>
</html>


	<%-- <spring:hasBindErrors name="maintenanceDto">
    <c:set var="errorCnt">${errors.errorCount}</c:set>
    <p><b># of Errors:${errorCnt}</b></p>
    <p></p>
	<c:forEach var="error" items="${errors.allErrors}">
		<b><c:out value="${error}" /></b>
		<p></p>
	</c:forEach>
	</spring:hasBindErrors> --%>
