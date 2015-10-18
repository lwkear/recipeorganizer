<!DOCTYPE html>
<html>
<head>

<title>Signup</title>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/head.jsp" %>
<%-- <%@include file="../common/js.jsp" %> --%>

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
			username.parent('div').removeClass('has-error');
			$('#userLabel').html("*Email:");
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
	<div id="wrap">

<%@include file="../common/nav.jsp" %>
	
		<spring:bind path="userDto.email" htmlEscape="false"><c:set var="emailError">${status.errorMessage}</c:set></spring:bind>
		<spring:bind path="userDto.password"><c:set var="passwordError">${status.errorMessage}</c:set></spring:bind>
		<spring:bind path="userDto.confirmPassword"><c:set var="confirmError">${status.errorMessage}</c:set></spring:bind>
		<spring:bind path="userDto.firstName"><c:set var="firstNameError">${status.errorMessage}</c:set></spring:bind>
		<spring:bind path="userDto.lastName"><c:set var="lastNameError">${status.errorMessage}</c:set></spring:bind>

		<p><b><c:out value="${emailError}" /></b></p>
		<p><b><c:out value="${passwordError}" /></b></p>
		<p><b><c:out value="${confirmError}" /></b></p>
		<p><b><c:out value="${firstNameError}" /></b></p>
		<p><b><c:out value="${lastNameError}" /></b></p>

		<spring:bind path="userDto">
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
		
		<spring:hasBindErrors name="userDto">
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
	
		<div class="container">
			<div class="page-header"> 		
				<h1><spring:message code="signup.title"></spring:message></h1>
			</div>			
			<div class="row">
				<form:form role="form" method="post" modelAttribute="userDto">
			        <div class="col-sm-12">
				        <div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty emailError}">has-error</c:if>">
							<label class="control-label" id="userLabel" for="username">
								*<spring:message code="signup.email"></spring:message>&nbsp;&nbsp;${emailError}</label>
							<form:input class="form-control" type="text" id="username" placeholder="Email" path="email" autocomplete="off"/>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty passwordError}">has-error</c:if>">
							<label class="control-label" for="password">
								*<spring:message code="signup.password"></spring:message>&nbsp;&nbsp;${passwordError}</label>
							<form:input class="form-control" type="password" id="password" placeholder="Password" path="password" autocomplete="off"/>
						</div>
						<div class="form-group col-sm-4 <c:if test="${not empty confirmError}">has-error</c:if>">
							<label class="control-label" for="confirmpassword">
								*<spring:message code="signup.confirmpassword"></spring:message>&nbsp;&nbsp;${confirmError}</label>
							<form:input class="form-control" type="password" id="confirmpassword" placeholder="Confirm password" path="confirmPassword" autocomplete="off"/>
						</div>
					</div>
			        <div class="col-sm-12">
			        	<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty firstNameError}">has-error</c:if>">
							<label class="control-label" for="firstname">
								*<spring:message code="signup.firstname"></spring:message>&nbsp;&nbsp;${firstNameError}</label>
							<form:input class="form-control" type="text" id="firstname" placeholder="First name" path="firstName" autocomplete="off"/>
						</div>
			        	<div class="form-group col-sm-4 <c:if test="${not empty lastNameError}">has-error</c:if>">
							<label class="control-label" for="lastname">
								*<spring:message code="signup.lastname"></spring:message>&nbsp;&nbsp;${lastNameError}</label>
							<form:input class="form-control" type="text" id="lastname" placeholder="Last name" path="lastName" autocomplete="off"/>
						</div>
					</div>
					<div class="form-group col-sm-12">
					</div>
			        <div class="form-group col-sm-2 col-sm-offset-5">
						<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit">
							<spring:message code="common.submit"></spring:message></button>
	        		</div>
	      		</form:form>
			</div>
	    </div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>
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
		
