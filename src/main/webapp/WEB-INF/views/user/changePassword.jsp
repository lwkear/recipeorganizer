<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="changepswd.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="passwordDto.currentPassword"><c:set var="currentPasswordError">${status.errorMessage}</c:set></spring:bind>	
	<spring:bind path="passwordDto.password"><c:set var="passwordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="passwordDto.confirmPassword"><c:set var="confirmPasswordError">${status.errorMessage}</c:set></spring:bind>

	<spring:bind path="passwordDto">
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
				<h3><spring:message code="changepswd.title"></spring:message></h3>
			</div>			
			<div class="row">
				<form:form name="passswordForm" role="form" modelAttribute="passwordDto" method="post">
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-4 <c:if test="${not empty currentPasswordError}">has-error</c:if>">
							<label class="control-label" for="password"><spring:message code="password.currentpassword"></spring:message></label>
							<form:input class="form-control" type="password" id="currentPassword" path="currentPassword" autocomplete="off"/>
							<span class="text-danger">${currentPasswordError}</span>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-4 <c:if test="${not empty passwordError}">has-error</c:if>">
							<label class="control-label" for="password"><spring:message code="common.password"></spring:message></label>
							<form:input class="form-control" type="password" id="password" path="password" autocomplete="off"/>
							<span class="text-danger">${passwordError}</span>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-4 <c:if test="${not empty confirmPasswordError}">has-error</c:if>">
							<label class="control-label" for="confirmpassword"><spring:message code="password.confirmpassword"></spring:message></label>
							<form:input class="form-control" type="password" id="confirmpassword" path="confirmPassword" autocomplete="off"/>
							<span class="text-danger">${confirmPasswordError}</span>
						</div>
					</div>
			        <div class="form-group col-sm-2 col-sm-offset-5 spacer-vert-sm">
						<button class="btn btn-primary btn-block" type="submit" name="submit"><spring:message code="common.submit"></spring:message></button>
	        		</div>
	      		</form:form>
			</div>
    	</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>
</html>
