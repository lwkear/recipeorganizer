<!DOCTYPE html>
<html>
<head>

<title>UserProfile</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document" onload="document.profileForm.city.focus();">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="userProfile.city"><c:set var="cityError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userProfile.state"><c:set var="stateError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userProfile.interests"><c:set var="interestsError">${status.errorMessage}</c:set></spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="profile.title"></spring:message></h3>
			</div>			
			<div class="row">
				<form:form name="profileForm" role="form" method="post" modelAttribute="userProfile">
					<div class="col-sm-12">
			        	<div class="form-group col-sm-6 col-sm-offset-2 <c:if test="${not empty cityError}">has-error</c:if>">
							<label class="control-label" for="city">
								<spring:message code="profile.city"></spring:message>&nbsp;&nbsp;${cityError}</label>
							<form:input class="form-control" type="text" id="city" name="city" path="city" autocomplete="off"/>
						</div>
			        	<div class="form-group col-sm-2 <c:if test="${not empty stateError}">has-error</c:if>">
							<label class="control-label" for="state">
								<spring:message code="profile.state"></spring:message>&nbsp;&nbsp;${stateError}</label>
							<form:input class="form-control" type="text" id="state" path="state" autocomplete="off"/>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group col-sm-8 col-sm-offset-2">
							<label class="control-label"><spring:message code="profile.age"></spring:message></label>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group col-sm-8 col-sm-offset-2" id="ageGroup">
							<div class="radio-inline">
								<form:radiobutton value="1" id="age1" path="age"/>&lt;18
							</div>
							<div class="radio-inline">
								<form:radiobutton value="2" id="age2" path="age"/>18-30
							</div>
							<div class="radio-inline">
								<form:radiobutton value="3" id="age3" path="age"/>31-50
							</div>
							<div class="radio-inline">
								<form:radiobutton value="4" id="age4" path="age"/>51-70
							</div>
							<div class="radio-inline">
								<form:radiobutton value="5" id="age5" path="age"/>>70
							</div>
							<div class="radio-inline">
								<form:radiobutton value="0" id="age0" path="age"/><spring:message code="profile.nevermind"></spring:message>
							</div>
						</div>
					</div>
					<div class="col-sm-12">
			        	<div class="form-group col-sm-8 col-sm-offset-2 <c:if test="${not empty interestsError}">has-error</c:if>">
							<label class="control-label" for="interests">
								<spring:message code="profile.interests"></spring:message>&nbsp;&nbsp;${interestsError}</label>
							<form:textarea class="form-control" rows="4" id="interests" path="interests"></form:textarea>				
						</div>
					</div>
					<div class="form-group col-sm-12">
					</div>
			        <div class="form-group col-sm-2 col-sm-offset-5">
						<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit">
							<spring:message code="common.submit"></spring:message></button>
	        		</div>
	        		<form:hidden path="id" />
	        		<form:hidden path="user.id" />
	      		</form:form>
			</div>
	    </div>
	</div>
	
<%@include file="../common/footer.jsp" %>

</body>

<script type="text/javascript">

function setAge() {
	var age = $('#age').val();
	console.log("age=" + age);
	$("#age" + age).prop('checked',true);
}

//shorthand for document.ready
$(function() {
	setAge();
});

</script>

</html>

<%-- 
		<spring:hasBindErrors name="user">
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
 --%>