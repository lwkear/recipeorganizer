<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="account.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>
	
	<spring:bind path="changeEmailDto.email" htmlEscape="false"><c:set var="emailError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="changeEmailDto.confirmEmail" htmlEscape="false"><c:set var="confirmEmailError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="changePasswordDto.password"><c:set var="passwordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="changePasswordDto.confirmPassword"><c:set var="confirmPasswordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="changePasswordDto.currentPassword"><c:set var="currentPasswordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="changeNameDto.firstName"><c:set var="firstNameError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="changeNameDto.lastName"><c:set var="lastNameError">${status.errorMessage}</c:set></spring:bind>

	<spring:bind path="changeEmailDto">
		<c:if test="${status.error}">
			<c:forEach var="code" varStatus="loop" items="${status.errorCodes}">
				<c:if test="${fn:containsIgnoreCase(code, 'EmailMatch')}">
					<c:forEach var="error" begin="${loop.index}" end="${loop.index}" items="${status.errorMessages}" >
						<c:set var="confirmEmailError">${error}</c:set>
					</c:forEach>
				</c:if>
			</c:forEach>
		</c:if>
	</spring:bind>

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
						<c:set var="passwordError">${error}</c:set>
					</c:forEach>
				</c:if>
			</c:forEach>
		</c:if>
	</spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header"> 		
				<h3><spring:message code="account.title"></spring:message></h3>
			</div>
		</div>
		<div class="panel-group" id="changeGroup">
		<!-- <div> -->
			<div class="panel col-sm-12">
				<div class="panel-heading">
					<div class="row form-group ">
						<div class="col-sm-2 col-sm-offset-2">
							<label class="control-label"><spring:message code="account.name"></spring:message></label>
						</div>
						<div class="col-sm-3">
							<span>${changeNameDto.currentFirstName} ${changeNameDto.currentLastName}</span>
						</div>
						<div class="col-sm-2">
							<a id="linkName" data-toggle="collapse" data-target="#panelName" href="#panelName"><spring:message code="account.changename"></spring:message></a>
						</div>
						<div class="col-sm-3">
							${nameSuccessMessage}
						</div>
					</div>
				</div>
				<%-- <div id="panelName" class="panel-collapse <c:if test="${empty firstNameError and empty lastNameError}">collapse</c:if>"> --%>
				<div id="panelName" class="panel-collapse collapse">
					<c:url var="changeName" value="/user/changeName"/>
					<form:form name="nameForm" id="nameForm" role="form" action="${changeName}" method="post" modelAttribute="changeNameDto">
					<form:input type="hidden" path="userId"/>
					<div class="panel-body" style="padding-top:0px;padding-bottom:10px">		
						<div class="row">
				        	<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty firstNameError}">has-error</c:if>">
								<label class="control-label" for="firstname"><spring:message code="signup.firstname"></spring:message></label>
								<form:input class="form-control maxSize" type="text" id="firstName" path="firstName" autocomplete="off" data-max="${sizeMap['ChangeNameDto.firstName.max']}"/>
								<span class="text-danger" id="firstNameErrMsg">${firstNameError}</span>
							</div>
				        	<div class="form-group col-sm-4 <c:if test="${not empty lastNameError}">has-error</c:if>">
								<label class="control-label" for="lastname"><spring:message code="signup.lastname"></spring:message></label>
								<form:input class="form-control maxSize" type="text" id="lastName" path="lastName" autocomplete="off" data-max="${sizeMap['ChangeNameDto.lastName.max']}"/>
								<span class="text-danger" id="lastNameErrMsg">${lastNameError}</span>
							</div>
						</div>
						<div class="form-group col-sm-2 col-sm-offset-5 text-center spacer-vert-xs">
							<button class="btn btn-primary pull-left" type="submit" id="btnSubmitName" name="btnSubmitName"><spring:message code="common.submit"></spring:message></button>
							<button class="btn btn-default pull-right" id="btnCancelName" name="btnCancelName"><spring:message code="common.cancel"></spring:message></button>
		        		</div>
					</div>
					</form:form>
				</div>
			</div>
			<div class="panel col-sm-12">
				<div class="panel-heading">
					<div class="row form-group ">
						<div class="col-sm-2 col-sm-offset-2">
							<label class="control-label"><spring:message code="common.email"></spring:message></label>
						</div>
						<div class="col-sm-3">
							<span>${changeEmailDto.currentEmail}</span>
						</div>
						<div class="col-sm-2">
							<%-- <a id="linkEmail" data-toggle="collapse" data-parent="#changeGroup" data-target="#panelEmail" href="#panelEmail"><spring:message code="account.changeemail"></spring:message></a> --%>
							<a id="linkEmail" data-toggle="collapse" data-target="#panelEmail" href="#panelEmail"><spring:message code="account.changeemail"></spring:message></a>
						</div>
						<div class="col-sm-3">
							${emailSuccessMessage}
						</div>
					</div>
				</div>
				<div id="panelEmail" class="panel-collapse collapse">
					<c:url var="changeEmail" value="/user/changeEmail"/>
					<form:form name="emailForm" id="emailForm" role="form" action="${changeEmail}" method="post" modelAttribute="changeEmailDto">
					<form:input type="hidden" path="userId"/>
					<div class="panel-body" style="padding-top:0px;padding-bottom:10px">		
						<div class="row">
					        <div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty emailError}">has-error</c:if>">
								<label class="control-label" id="emailLabel" for="email"><spring:message code="common.newemail"></spring:message></label>
								<form:input class="form-control maxSize" type="text" id="email" path="email" autocomplete="off" data-max="${sizeMap['ChangeEmailDto.email.max']}"/>
								<span class="text-danger" id="emailErrMsg">${emailError}</span>
							</div>
					        <div class="form-group col-sm-4 <c:if test="${not empty confirmEmailError}">has-error</c:if>">
								<label class="control-label" id="confirmEmailLabel" for="confirmEmail"><spring:message code="signup.confirmemail"></spring:message></label>
								<form:input class="form-control maxSize" type="text" id="confirmEmail" path="confirmEmail" autocomplete="off" data-max="${sizeMap['ChangeEmailDto.confirmEmail.max']}"/>
								<span class="text-danger" id="confirmEmailErrMsg">${confirmEmailError}</span>
							</div>
						</div>
						<div class="form-group col-sm-2 col-sm-offset-5 text-center spacer-vert-xs">
							<button class="btn btn-primary pull-left" type="submit" id="btnSubmitEmail" name="btnSubmitEmail"><spring:message code="common.submit"></spring:message></button>
							<button class="btn btn-default pull-right" id="btnCancelEmail" name="btnCancelEmail"><spring:message code="common.cancel"></spring:message></button>
		        		</div>
					</div>
					</form:form>
				</div>
			</div>
			<div class="panel col-sm-12">
				<div class="panel-heading">
					<div class="row form-group ">
						<div class="col-sm-2 col-sm-offset-2">
							<label class="control-label"><spring:message code="common.password"></spring:message></label>
						</div>
						<div class="col-sm-3">
							<span><em><spring:message code="account.hidden"></spring:message></em></span>
						</div>
						<div class="col-sm-2">
							<a id="linkPassword" data-toggle="collapse" data-target="#panelPassword" href="#panelPassword"><spring:message code="account.changepassword"></spring:message></a>
						</div>
						<div class="col-sm-3">
							${passwordSuccessMessage}
						</div>
					</div>
				</div>
				<%-- <div id="panelPassword" class="panel-collapse <c:if test="${empty passwordError and empty confirmPasswordError and empty currentPasswordError}">collapse</c:if>"> --%>
				<div id="panelPassword" class="panel-collapse collapse">
					<c:url var="changePassword" value="/user/changePassword"/>
					<form:form name="formWithPswd" id="formWithPswd" role="form" action="${changePassword}" method="post" modelAttribute="changePasswordDto">
					<form:input type="hidden" path="userId"/>
					<div class="panel-body" style="padding-top:0px;padding-bottom:10px">
						<div class="row">		
							<div class="form-group col-sm-4 col-sm-offset-2 <c:if test="${not empty currentPasswordError}">has-error</c:if>">
								<label class="control-label" for="password"><spring:message code="password.currentpassword"></spring:message></label>
								<form:input class="form-control maxSize" type="password" id="currentPassword" path="currentPassword" autocomplete="off" data-max="${sizeMap['ChangePasswordDto.currentPassword.max']}"/>
								<span class="text-danger" id="currentPasswordErrMsg">${currentPasswordError}</span>
							</div>
						</div>
						<div class="row" id="pwd-container">
							<div id="pswdErrors" class="col-sm-2 pwstrength_viewport_errors text-danger">
							</div>
							<div class="form-group col-sm-4 <c:if test="${not empty passwordError}">has-error</c:if>">
								<label class="control-label" for="password"><spring:message code="password.newpassword"></spring:message></label>
								<form:input class="form-control maxSize" type="password" id="password" path="password" autocomplete="off" data-max="${sizeMap['ChangePasswordDto.password.max']}"/>
								<span class="text-danger" id="passwordErrMsg">${passwordError}</span>
							</div>
							<div class="form-group col-sm-4 <c:if test="${not empty confirmPasswordError}">has-error</c:if>">
								<label class="control-label" for="confirmpassword"><spring:message code="password.confirmpassword"></spring:message></label>
								<form:input class="form-control maxSize" type="password" id="confirmPassword" path="confirmPassword" autocomplete="off" data-max="${sizeMap['ChangePasswordDto.confirmPassword.max']}"/>
								<span class="text-danger" id="confirmPasswordErrMsg">${confirmPasswordError}</span>
							</div>
							<input type="text" id="pswdScore" style="display:none"/>
						</div>
						<div class="form-group col-sm-2 col-sm-offset-5 text-center">
							<button class="btn btn-primary pull-left" type="submit" id="btnSubmitPassword" name="btnSubmitPassword"><spring:message code="common.submit"></spring:message></button>
							<button class="btn btn-default pull-right" id="btnCancelPassword" name="btnCancelPassword"><spring:message code="common.cancel"></spring:message></button>
		        		</div>
		       		</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<script src="<c:url value="/resources/custom/useraccount.js" />"></script>

</html>
