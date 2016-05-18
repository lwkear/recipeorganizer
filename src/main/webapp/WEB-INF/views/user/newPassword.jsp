<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="newpswd.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="newPasswordDto.password"><c:set var="passwordError">${status.errorMessage}</c:set></spring:bind>
	<spring:bind path="newPasswordDto.confirmPassword"><c:set var="confirmPasswordError">${status.errorMessage}</c:set></spring:bind>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header"> 		
				<h3><spring:message code="newpswd.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="row">
				<form:form name="formWithPswd" id="formWithPswd" role="form" modelAttribute="newPasswordDto" method="post">
					<div class="col-sm-12" id="pwd-container">
						<div class="col-sm-4 pwstrength_viewport_errors text-danger">
						</div>
						<div class="form-group col-sm-4 <c:if test="${not empty passwordError}">has-error</c:if>">
							<label class="control-label" for="password"><spring:message code="common.password"></spring:message></label>
							<form:input class="form-control maxSize" type="password" id="password" path="password" autocomplete="off" data-max="${sizeMap['password.max']}"/>
							<span class="text-danger" id="passwordErrMsg">${passwordError}</span>
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
	        		<form:hidden path="userId" />
	      		</form:form>
			</div>
			<div class="col-sm-12 text-center">
				<h3 class="control-label text-danger">${errorMsg}</h3>
			</div>
	    </div>
	</div>
	
<%@include file="../common/footer.jsp" %>	
	
</body>

<script src="<c:url value="/resources/custom/usersignup.js" />"></script>

</html>


<%-- <spring:hasBindErrors name="newPasswordDto">
    <c:set var="errorCnt">${errors.errorCount}</c:set>
    <p><b># of Errors:${errorCnt}</b></p>
    <p></p>
	<c:forEach var="error" items="${errors.allErrors}">
		<b><c:out value="${error}" /></b>
		<p></p>
	</c:forEach>
	</spring:hasBindErrors> --%>
	
