<!DOCTYPE html>
<html>
<head>

<title>New Password</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document" onload="document.passwordForm.password.focus();">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="newPassword.password"><c:set var="passwordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="newPassword.confirmPassword"><c:set var="confirmPasswordError">${status.errorMessage}</c:set></spring:bind>

	<spring:bind path="newPassword">
		<c:if test="${status.error}">
			<c:forEach var="code" varStatus="loop" items="${status.errorCodes}">
				<c:if test="${fn:containsIgnoreCase(code, 'PasswordMatch')}">
					<c:forEach var="error" begin="${loop.index}" end="${loop.index}" items="${status.errorMessages}" >
						<c:set var="confirmPasswordError">${error}</c:set>
					</c:forEach>
				</c:if>
			</c:forEach>
		</c:if>
	</spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="newpswd.title"></spring:message></h3>
			</div>			
			<div class="row">
				<form:form name="passswordForm" role="form" modelAttribute="newPassword" method="post">
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
					<div class="form-group col-sm-12">&nbsp;</div>
			        <div class="form-group col-sm-2 col-sm-offset-5">
						<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit"><spring:message code="common.submit"></spring:message></button>
	        		</div>
	      		</form:form>
			</div>
			<div class="col-sm-12 text-center">
				<h3 class="control-label text-danger">${errorMsg}</h3>
			</div>
	    </div>
	</div>
	
<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
