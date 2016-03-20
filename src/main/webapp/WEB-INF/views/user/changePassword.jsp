<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="changepswd.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="changePasswordDto.currentPassword"><c:set var="currentPasswordError">${status.errorMessage}</c:set></spring:bind>	
	<spring:bind path="changePasswordDto.password"><c:set var="passwordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="changePasswordDto.confirmPassword"><c:set var="confirmPasswordError">${status.errorMessage}</c:set></spring:bind>

	<spring:bind path="changePasswordDto">
		<c:if test="${status.error}">
			<c:forEach var="code" varStatus="loop" items="${status.errorCodes}">
				<c:if test="${fn:containsIgnoreCase(code, 'PasswordMatch')}">
					<c:forEach var="error" begin="${loop.index}" end="${loop.index}" items="${status.errorMessages}" >
						<c:set var="confirmPasswordError">${error}</c:set>
					</c:forEach>
				</c:if>
				<c:if test="${fn:containsIgnoreCase(code, 'PasswordNotDuplicate')}">
					<c:forEach var="error" begin="${loop.index}" end="${loop.index}" items="${status.errorMessages}" >
						<c:set var="duplicatePasswordError">${error}</c:set>
					</c:forEach>
				</c:if>
			</c:forEach>
		</c:if>
	</spring:bind>

	<div class="container container-white">	
		<c:if test="${not empty warningMaint}">
			<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
		</c:if>
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="changepswd.title"></spring:message></h3>
			</div>
			<div class="row">
				<form:form name="formWithPswd" id="formWithPswd" role="form" modelAttribute="changePasswordDto" method="post">
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-4 <c:if test="${not empty currentPasswordError}">has-error</c:if>">
							<label class="control-label" for="password"><spring:message code="password.currentpassword"></spring:message></label>
							<form:input class="form-control maxSize" type="password" id="currentPassword" path="currentPassword" autocomplete="off" data-max="${sizeMap['currentPassword.max']}"/>
							<span class="text-danger" id="currentPasswordErrMsg">${currentPasswordError}</span>
						</div>
					</div>
					<div class="col-sm-12" id="pwd-container">
						<div class="col-sm-4 pwstrength_viewport_errors text-danger">
						</div>
						<div class="form-group col-sm-4 <c:if test="${not empty passwordError || not empty duplicatePasswordError}">has-error</c:if>">
							<label class="control-label" for="password"><spring:message code="common.password"></spring:message></label>
							<form:input class="form-control maxSize" type="password" id="password" path="password" autocomplete="off" data-max="${sizeMap['password.max']}"/>
							<span class="text-danger" id="passwordErrMsg">${passwordError}</span>
							<span class="text-danger" id="passwordErrMsg">${duplicatePasswordError}</span>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-4 <c:if test="${not empty confirmPasswordError}">has-error</c:if>">
							<label class="control-label" for="confirmpassword"><spring:message code="password.confirmpassword"></spring:message></label>
							<form:input class="form-control maxSize" type="password" id="confirmPassword" path="confirmPassword" autocomplete="off" data-max="${sizeMap['confirmPassword.max']}"/>
							<span class="text-danger" id="confirmPasswordErrMsg">${confirmPasswordError}</span>
						</div>
					</div>
					<input type="text" id="pswdScore" style="display:none"/>
			        <div class="form-group col-sm-2 col-sm-offset-5 text-center spacer-vert-sm">
						<button class="btn btn-primary" type="submit" id="btnSubmit" name="btnSubmit"><spring:message code="common.submit"></spring:message></button>
	        		</div>
	      		</form:form>
			</div>
    	</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<script src="<c:url value="/resources/custom/usersignup.js" />"></script>

</html>
