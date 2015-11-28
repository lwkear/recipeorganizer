<!DOCTYPE html>
<html>
<head>

<title>Forgot Password</title>

<%@include file="../common/head.jsp" %>

</head>

<!-- <body role="document" onload="document.forgotForm.email.focus();"> -->
<body role="document">

<%@include file="../common/nav.jsp" %>

	<spring:bind path="userEmail.email" htmlEscape="false"><c:set var="emailError">${status.errorMessage}</c:set></spring:bind>
	
	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="forgotpswd.title"></spring:message></h3>
			</div>			
			<div class="row">
				<div class="form-group col-sm-4 col-sm-offset-4 text-center">
					<spring:message code="user.password.resetInstructions"></spring:message>
				</div>
				<form:form role="form" name="forgotForm" modelAttribute="userEmail" method="post">
			        <div class="col-sm-12">
				        <div class="form-group col-sm-4 col-sm-offset-4 <c:if test="${not empty emailError}">has-error</c:if>">
							<label class="control-label" id="emailLabel" for="email"><spring:message code="common.email"></spring:message></label>
							<form:input class="form-control" type="text" id="email" path="email" autocomplete="off"/>
							<span class="text-danger" id="emailErrMsg">${emailError}</span>
						</div>
					</div>
					<div class="form-group col-sm-12">&nbsp;</div>
			        <div class="row">				
				        <div class="form-group col-sm-2 col-sm-offset-5">
							<button class="btn btn-primary btn-block" type="submit" name="submit">
								<spring:message code="common.submit"></spring:message></button>
		        		</div>
	        		</div>
	      		</form:form>
			</div>
    	</div>
    </div>
    
<%@include file="../common/footer.jsp" %>
    
</body>
</html>
