<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="signup.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

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
							<form:input class="form-control maxSize" type="text" id="email" path="email" autocomplete="off" data-max="${sizeMap['email.max']}"/>
							<span class="text-danger" id="emailErrMsg">${emailError}</span>
						</div>
				        <div class="form-group col-sm-4 <c:if test="${not empty confirmEmailError}">has-error</c:if>">
							<label class="control-label" id="confirmEmailLabel" for="confirmEmail"><spring:message code="signup.confirmemail"></spring:message></label>
							<form:input class="form-control maxSize" type="text" id="confirmEmail" path="confirmEmail" autocomplete="off" data-max="${sizeMap['confirmEmail.max']}"/>
							<span class="text-danger" id="confirmEmailErrMsg">${confirmEmailError}</span>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty passwordError}">has-error</c:if>">
							<label class="control-label" for="password"><spring:message code="common.password"></spring:message></label>
							<form:input class="form-control maxSize" type="password" id="password" path="password" autocomplete="off" data-max="${sizeMap['password.max']}"/>
							<span class="text-danger" id="passwordErrMsg">${passwordError}</span>
						</div>
						<div class="form-group col-sm-4 <c:if test="${not empty confirmPasswordError}">has-error</c:if>">
							<label class="control-label" for="confirmpassword"><spring:message code="password.confirmpassword"></spring:message></label>
							<form:input class="form-control maxSize" type="password" id="confirmPassword" path="confirmPassword" autocomplete="off" data-max="${sizeMap['confirmPassword.max']}"/>
							<span class="text-danger" id="confirmPasswordErrMsg">${confirmPasswordError}</span>
						</div>
					</div>
			        <div class="col-sm-12">
			        	<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty firstNameError}">has-error</c:if>">
							<label class="control-label" for="firstname"><spring:message code="signup.firstname"></spring:message></label>
							<form:input class="form-control maxSize" type="text" id="firstName" path="firstName" autocomplete="off" data-max="${sizeMap['firstName.max']}"/>
							<span class="text-danger" id="firstNameErrMsg">${firstNameError}</span>
						</div>
			        	<div class="form-group col-sm-4 <c:if test="${not empty lastNameError}">has-error</c:if>">
							<label class="control-label" for="lastname"><spring:message code="signup.lastname"></spring:message></label>
							<form:input class="form-control maxSize" type="text" id="lastName" path="lastName" autocomplete="off" data-max="${sizeMap['lastName.max']}"/>
							<span class="text-danger" id="lastNameErrMsg">${lastNameError}</span>
						</div>
					</div>
					<div class="col-sm-12 spacer-vert-xs">
						<div class="form-group col-sm-8 col-sm-offset-2">
							<div class="row">
								<div class="col-sm-8">
									<label class="control-label" for="submitRecipes"><spring:message code="signup.submitrecipe"></spring:message></label>
								</div>
								<div class="col-sm-3">
									<div class="radio-inline">
										<form:radiobutton value="true" path="submitRecipes" checked="true"/><spring:message code="common.yes"></spring:message>
									</div>
									<div class="radio-inline">
										<form:radiobutton value="true" path="submitRecipes"/><spring:message code="common.no"></spring:message>
									</div>
								</div>
							</div>
						</div>
					</div>
			        <div class="form-group col-sm-2 col-sm-offset-5 text-center spacer-vert-sm">
						<button class="btn btn-primary" type="submit" name="submit"><spring:message code="common.submit"></spring:message></button>
	        		</div>
	      		</form:form>
			</div>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<script src="<c:url value="/resources/custom/usersignup.js" />"></script>

</html>
