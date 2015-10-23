<!DOCTYPE html>
<html>
<head>

<title>SignUp</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document" onload="document.signupForm.email.focus();">

<%@include file="../common/nav.jsp" %>
	
	<spring:bind path="userDto.email" htmlEscape="false"><c:set var="emailError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userDto.confirmEmail" htmlEscape="false"><c:set var="confirmEmailError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userDto.password"><c:set var="passwordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userDto.confirmPassword"><c:set var="confirmPasswordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userDto.firstName"><c:set var="firstNameError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="userDto.lastName"><c:set var="lastNameError">${status.errorMessage}</c:set></spring:bind>

	<spring:bind path="userDto">
		<c:if test="${status.error}">
			<c:forEach var="code" varStatus="loop" items="${status.errorCodes}">
				<c:if test="${fn:containsIgnoreCase(code, 'PasswordMatch')}">
					<c:forEach var="error" begin="${loop.index}" end="${loop.index}" items="${status.errorMessages}" >
						<c:set var="confirmPasswordError">${error}</c:set>
					</c:forEach>
				</c:if>
				<c:if test="${fn:containsIgnoreCase(code, 'EmailMatch')}">
					<c:forEach var="error" begin="${loop.index}" end="${loop.index}" items="${status.errorMessages}" >
						<c:set var="confirmEmailError">${error}</c:set>
					</c:forEach>
				</c:if>
			</c:forEach>
		</c:if>
	</spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="signup.title"></spring:message></h3>
			</div>			
			<div class="row">
				<form:form name="signupForm" role="form" method="post" modelAttribute="userDto">
			        <div class="col-sm-12">
				        <div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty emailError}">has-error</c:if>">
							<label class="control-label" id="emailLabel" for="email"><spring:message code="common.email"></spring:message></label>
							<form:input class="form-control" type="text" id="email" path="email" autocomplete="off"/>
							<span class="text-danger" id="emailErrMsg">${emailError}</span>
						</div>
				        <div class="form-group col-sm-4 <c:if test="${not empty confirmEmailError}">has-error</c:if>">
							<label class="control-label" id="confirmEmailLabel" for="confirmEmail"><spring:message code="signup.confirmemail"></spring:message></label>
							<form:input class="form-control" type="text" id="confirmEmail" path="confirmEmail" autocomplete="off"/>
							<span class="text-danger">${confirmEmailError}</span>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty passwordError}">has-error</c:if>">
							<label class="control-label" for="password"><spring:message code="common.password"></spring:message></label>
							<form:input class="form-control" type="password" id="password" path="password" autocomplete="off"/>
							<span class="text-danger">${passwordError}</span>
						</div>
						<div class="form-group col-sm-4 <c:if test="${not empty confirmPasswordError}">has-error</c:if>">
							<label class="control-label" for="confirmpassword"><spring:message code="password.confirmpassword"></spring:message></label>
							<form:input class="form-control" type="password" id="confirmpassword" path="confirmPassword" autocomplete="off"/>
							<span class="text-danger">${confirmPasswordError}</span>
						</div>
					</div>
			        <div class="col-sm-12">
			        	<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty firstNameError}">has-error</c:if>">
							<label class="control-label" for="firstname"><spring:message code="signup.firstname"></spring:message></label>
							<form:input class="form-control" type="text" id="firstname" path="firstName" autocomplete="off"/>
							<span class="text-danger">${firstNameError}</span>
						</div>
			        	<div class="form-group col-sm-4 <c:if test="${not empty lastNameError}">has-error</c:if>">
							<label class="control-label" for="lastname"><spring:message code="signup.lastname"></spring:message></label>
							<form:input class="form-control" type="text" id="lastname" path="lastName" autocomplete="off"/>
							<span class="text-danger">${lastNameError}</span>
						</div>
					</div>
					<div class="form-group col-sm-12">&nbsp;</div>
			        <div class="form-group col-sm-2 col-sm-offset-5">
						<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit"><spring:message code="common.submit"></spring:message></button>
	        		</div>
	      		</form:form>
			</div>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<script type="text/javascript">

$(function() {

	$(document)
	.on('blur', '#email', function(e)
	{
		e.preventDefault();

		var emailStr = $(this).val();
		//nothing to do
		if (!emailStr)
			return;

		//save off the variable for the .done and .fail methods
		var username = $(this);

		//allow jquery to create the query string from the data parameters to handle special characters
		$.ajax({
			type: 'GET',
			url: '/recipeorganizer/ajax/anon/lookupUser',
			dataType: 'json',
			data: {
				email : emailStr
			}
		})
		.done(function(data) {
			//TODO: GUI: probably need to check for a success status?
			console.log('Email ok (not found)');
			//fix the appearance in case a name was entered in error
			username.parent('div').reveClass('has-error');
			//$('#emailErrMsg').html("");
		})
		.fail(function(jqXHR, status, error) {
			console.log('fail status: '+ jqXHR.status);
			console.log('fail error: '+ error);

			//server sets CONFLICT error if name exists
			if (jqXHR.status == 409) {
				//server currently returns a simple error message
				var respText = jqXHR.responseText;
				console.log('respText: '+ respText);
				//set the error indicator
				username.parent('div').addClass('has-error');
				$('#emailErrMsg').html(respText);
				return;
			}
		});
	});
});

</script>

</html>


		<%-- <spring:hasBindErrors name="userDto">
	    <c:set var="errorCnt">${errors.errorCount}</c:set>
	    <p><b># of Errors:${errorCnt}</b></p>
	    <p></p>
		<c:forEach var="error" items="${errors.allErrors}">
			<b><c:out value="${error}" /></b>
			<p></p>
		</c:forEach>
		</spring:hasBindErrors>
		<p></p>
		<p></p> --%>
		
		
		<%-- <p><b><c:out value="${emailError}" /></b></p>
		<p><b><c:out value="${passwordError}" /></b></p>
		<p><b><c:out value="${confirmPasswordError}" /></b></p>
		<p><b><c:out value="${firstNameError}" /></b></p>
		<p><b><c:out value="${lastNameError}" /></b></p> --%>

<%-- 
	      		<div class="form-group col-sm-12"></div>
	      		<div class="form-group col-sm-12">
	      			<div class="form-group col-sm-4 col-sm-offset-2">
	      				<small><spring:message code="common.requiredfield"></spring:message></small>
	      			</div>
	      		</div>
--%>