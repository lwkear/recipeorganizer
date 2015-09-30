<!DOCTYPE html>
<html>
<head>

<title>Signup</title>

<%@include file="../common/head.jsp" %>
<%@include file="../common/js.jsp" %>

<script type="text/javascript">

//shorthand for document.ready
$(function() {

	$(document)
	.on('blur', '#username', function(e)
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
			url: '/recipeorganizer/user/lookupUser',
			dataType: 'json',
			data: {
				email : emailStr
			}
		})
		.done(function(data) {
			//TODO: probably need to check for a success status?
			console.log('Email ok (not found)');
			//fix the appearance in case a name was entered in error
			username.parent('div').removeClass('has-error');
			$('#userLabel').html("Email:");
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
				$('#userLabel').html("Name:&nbsp;&nbsp;" + respText);
				return;
			}
		});
	});
});

</script>

</head>

<body role="document">

	<%@include file="../common/nav.jsp" %>

	<spring:bind path="users.email"><c:set var="emailError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="users.password"><c:set var="passwordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="users.confirmPassword"><c:set var="confirmError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="users.firstName"><c:set var="firstNameError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="users.lastName"><c:set var="lastNameError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="users.city"><c:set var="cityError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="users.state"><c:set var="stateError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="users.interests"><c:set var="interestsError">${status.errorMessage}</c:set></spring:bind>

	<div class="container">
	
		<h2 class="text-center">Sign Up</h2>
		
		<spring:hasBindErrors name="users">
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
		
		<spring:bind path="users">
			<c:if test="${status.error}">
                <c:forEach var="code" varStatus="loop" items="${status.errorCodes}">
                	<c:if test="${fn:containsIgnoreCase(code, 'PasswordMatch')}">
		                <c:forEach var="error" begin="${loop.index}" end="${loop.index}" items="${status.errorMessages}" >
		                    <c:set var="confirmError">${error}</c:set>
		                </c:forEach>
		            </c:if>
				</c:forEach>
            </c:if>
		</spring:bind>

		<div class="row">
			<form:form role="form" method="post" modelAttribute="users">  <!-- enctype="multipart/form-data"> class="form-horizontal" -->
		        <div class="col-sm-12">
			        <div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty emailError}">has-error</c:if>">
						<label class="control-label" id="userLabel" for="username">*Email:&nbsp;&nbsp;${emailError}</label>
						<form:input class="form-control" type="text" id="username" placeholder="Email" path="email" autocomplete="off"/>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty passwordError}">has-error</c:if>">
						<label class="control-label" for="password">*Password:&nbsp;&nbsp;${passwordError}</label>
						<form:input class="form-control" type="text" id="password" placeholder="Password" path="password" autocomplete="off"/>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty confirmError}">has-error</c:if>">
						<label class="control-label" for="confirmpassword">*Confirm Password:&nbsp;&nbsp;${confirmError}</label>
						<form:input class="form-control" type="text" id="confirmpassword" placeholder="Confirm password" path="confirmPassword" autocomplete="off"/>
					</div>
				</div>
		        <div class="col-sm-12">
		        	<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty firstNameError}">has-error</c:if>">
						<label class="control-label" for="firstname">*First Name:&nbsp;&nbsp;${firstNameError}</label>
						<form:input class="form-control" type="text" id="firstname" placeholder="First name" path="firstName" autocomplete="off"/>
					</div>
		        	<div class="form-group col-sm-4 <c:if test="${not empty lastNameError}">has-error</c:if>">
						<label class="control-label" for="lastname">Last Name:&nbsp;&nbsp;${lastNameError}</label>
						<form:input class="form-control" type="text" id="lastname" placeholder="Last name" path="lastName" autocomplete="off"/>
					</div>
				</div>
				<div class="col-sm-12">
		        	<div class="form-group col-sm-6 col-sm-offset-2 <c:if test="${not empty cityError}">has-error</c:if>">
						<label class="control-label" for="firstname">City:&nbsp;&nbsp;${cityError}</label>
						<form:input class="form-control" type="text" id="city" placeholder="City" path="city" autocomplete="off"/>
					</div>
		        	<div class="form-group col-sm-2 <c:if test="${not empty stateError}">has-error</c:if>">
						<label class="control-label" for="lastname">State:&nbsp;&nbsp;${stateError}</label>
						<form:input class="form-control" type="text" id="state" path="state" autocomplete="off"/>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="form-group col-sm-8 col-sm-offset-2">
						<label class="control-label">Age:</label>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="form-group col-sm-8 col-sm-offset-2">
						<div class="radio-inline">
							<form:radiobutton value="1" path="age"/>&lt;18
						</div>
						<div class="radio-inline">
							<form:radiobutton value="2" path="age"/>18-30
						</div>
						<div class="radio-inline">
							<form:radiobutton value="3" path="age"/>31-50
						</div>
						<div class="radio-inline">
							<form:radiobutton value="4" path="age"/>51-70
						</div>
						<div class="radio-inline">
							<form:radiobutton value="5" path="age"/>>70
						</div>
						<div class="radio-inline">
							<form:radiobutton value="0" path="age" checked="true"/>Never mind
						</div>
					</div>
				</div>
				<div class="col-sm-12">
		        	<div class="form-group col-sm-8 col-sm-offset-2 <c:if test="${not empty interestsError}">has-error</c:if>">
						<label class="control-label" for="interests">Culinary Interests:&nbsp;&nbsp;${interestsError}</label>
						<form:textarea class="form-control" rows="4" id="interests" placeholder="Enter your interests" path="interests"></form:textarea>				
					</div>
				</div>
				<div class="form-group col-sm-12">
				</div>
		        <div class="form-group col-sm-2 col-sm-offset-5">
					<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit">Submit</button>
        		</div>
      		</form:form>
		</div>
    </div>
</body>
</html>
