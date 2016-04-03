<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%> --%>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>
	
	<div class="container container-white">
	 	<div class="col-sm-12">


	<div class="col-sm-12">
		<iframe id="iframerpt1" name="iframerpt1" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1463"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1101"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1522"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=741"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1141"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=421"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1462"/>"></iframe>
	</div>

<%--

$F{source.type} == null ? "" : 
($F{source.cookbook} != null ? $F{source.cookbook} : 
($F{source.magazine} != null ? $F{source.magazine} : 
($F{source.newspaper} != null ? $F{source.newspaper} : 
($F{source.other} != null ? $F{source.other} : 
($F{source.person} != null ? $F{source.person} : 
($F{source.websiteUrl} != null ? $F{source.websiteUrl} : 
""))))))

 +
$V{SourcePerson} + $V{SourceOther} + $V{SourceMagazine} + $V{SourceNewspaper}

	<variable name="SourceLabel" class="java.lang.String">
		<variableExpression><![CDATA[$F{source.type} == null ? "" :  $R{report.source} + " "]]></variableExpression>
	</variable>
	<variable name="Source" class="java.lang.String">
		<variableExpression><![CDATA[$F{source.type} == null ? "" : ($F{source.cookbook} != null ? $F{source.cookbook}  : "")]]></variableExpression>
	</variable>

			<printWhenExpression><![CDATA[($F{servings} != null || ($F{prepHours} > 0 || $F{prepMinutes} > 0) || $F{source.type} != null) ? new Boolean(true) : new Boolean(false)]]></printWhenExpression>

			<textField isBlankWhenNull="true">
				<reportElement x="300" y="10" width="250" height="20" uuid="9b0fbcfc-52d6-46fc-853b-7d9440699f56">
					<property name="com.jaspersoft.studio.unit.x" value="pixel"/>
					<property name="com.jaspersoft.studio.unit.y" value="pixel"/>
					<property name="com.jaspersoft.studio.unit.height" value="pixel"/>
					<property name="com.jaspersoft.studio.unit.width" value="pixel"/>
					<printWhenExpression><![CDATA[$F{source.type} != null]]></printWhenExpression>
				</reportElement>
				<textElement markup="styled">
					<font size="14"/>
				</textElement>
				<textFieldExpression><![CDATA["<style isBold=\"true\">" + $V{SourceLabel} + "</style>" + $V{Source}]]></textFieldExpression>
			</textField>

 			<div class="page-header"> 		
				<h3><spring:message code="maintenance.title"></spring:message></h3>
			</div>

			<form:form class="form-horizontal" role="form" method="post" modelAttribute="maintenanceDto">
			<div class="col-sm-12">
				<div class="text-center bold-maroon"><h5>${maintWindow}</h5></div>
		    	<div class="row col-sm-offset-4 spacer-vert-xs">
					<div class="col-sm-4">
						<div class="row">
			            	<label class="control-label" for="emergencyMaintenance"><spring:message code="maintenance.emergency"></spring:message></label>
				        </div>
				        <div class="row">
							<div class="radio-inline">
								<form:radiobutton value="true" path="emergencyMaintenance"/><spring:message code="common.yes"></spring:message>
							</div>
							<div class="radio-inline">
								<form:radiobutton value="false" path="emergencyMaintenance" checked="true"/><spring:message code="common.no"></spring:message>
							</div>
				        </div>
					</div>
					<div class="col-sm-4 <c:if test="${not empty emergencyDurationError}">has-error</c:if>">
				        <div class="row">
			        		<label class="control-label" for="emergencyDuration"><spring:message code="maintenance.emergency.duration"></spring:message></label>
				        </div>
				        <div class="row">
				            <div class="row col-sm-5">				            	
				                <form:input class="form-control" type="text" id="duration" path="emergencyDuration" autocomplete="off"/>
				            </div>
				        </div>
				        <span class="text-danger" style="margin-left:-15px;margin-right:-15px;" id="emergencyDurationErrMsg">${emergencyDurationError}</span>
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
					<div class="row">
						<div class="col-sm-4">
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
							<div class="row">
								<div class="col-sm-12">
				            		<label class="control-label" for="startTime"><spring:message code="maintenance.startTime"></spring:message></label>
						    	</div>
						        <div class="col-sm-6">
					                <form:input class="form-control" type="text" id="startTime" path="startTime" autocomplete="off"/> 
					            </div>
								<span class="text-danger" style="margin-right:-15px;" id="startTimeErrMsg">${startTimeError}</span>
							</div>
					        <div class="row">
					        	<div class="col-sm-12">
			        				<label class="control-label" for="duration"><spring:message code="maintenance.duration"></spring:message></label>
			        			</div>
					            <div class="col-sm-5">				            	
					                <form:input class="form-control" type="text" id="duration" path="duration" autocomplete="off"/>
					            </div>
						        <span class="text-danger" style="margin-right:-15px;" id="durationErrMsg">${durationError}</span>
					        </div>				        
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
				<div class="col-sm-12">				
					<div class="form-group col-sm-12 spacer-vert-sm">
						<div class="col-sm-offset-5 col-sm-2 text-center">
							<button type="submit" class="btn btn-primary" id="btnSubmit" name="btnSubmit"><spring:message code="common.save"></spring:message></button>
						</div>
					</div>
				</div>
			</div>				
			</form:form>
 --%>

		</div>
	</div>



<%--

	<spring:bind path="webGreeting.greeting"><c:set var="greetingError">${status.errorMessage}</c:set></spring:bind>	

	<div class="container container-white"> 
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3>Test</h3>
			</div>
		</div>
		<div class="col-sm-12">
			${text}
		</div>
		<div class="col-sm-12">
			${expired}
		</div>
		<div class="col-sm-12">
			<form:form role="form" modelAttribute="webGreeting" method="post">
				<div>
					<form:input id="greeting" type="text" path="greeting"/>
					<span class="text-danger">${greetingError}</span>
				</div>
		        <div class="form-group col-sm-2 col-sm-offset-5 text-center spacer-vert-sm">
					<button class="btn btn-primary" onclick="viewtext()" type="submit" name="submit">
						<spring:message code="common.submit"></spring:message></button>
        		</div>
			</form:form>
		</div>
        <div class="col-sm-12">
			<div class="row">
				<div class="col-sm-5 <c:if test="${not empty tagsError}">has-error</c:if>">
					<select class="form-control col-sm-5" id="inputTags" multiple>
						<option value="">Enter a tag</option>
						<option value="Ch">Christmas</option>
						<option value="Th">Thanksgiving</option>
						<option value="It">Italian</option>
						<option value="Cn">Chinese</option>
					</select>
					<span class="text-danger">${tagsError}</span>
				</div>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="col-sm-8 col col-sm-offset-2">			
				<div class="center alert text-center strong">
					<div><h4>${message}</h4></div>
				</div>
			</div>
		</div>
	</div>
 --%>
<%@include file="../common/footer.jsp" %>

</body>

<script type="text/javascript">

function viewtext() {
	var text = $('#inputTags option:selected').map(function () {
				return $(this).text();}).get();
	alert(text);	
}

$(function() {
	$('#inputTags').selectize({
		maxItems: 5,
	    persist: false,
	    diacritics: true,
	    create: function(input) {
	        return {
	            value: input,
	            text: input
	        }
	    }
	});
})

</script>

</html>
