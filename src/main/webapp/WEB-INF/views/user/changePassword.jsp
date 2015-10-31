<!DOCTYPE html>
<html>
<head>

<title>Change Password</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document" onload="document.passswordForm.currentpassword.focus();">

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
					<div class="form-group col-sm-12">&nbsp;</div>
			        <div class="form-group col-sm-2 col-sm-offset-5">
						<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit"><spring:message code="common.submit"></spring:message></button>
	        		</div>
	        		<%-- <form:hidden path="userId" /> --%>
	      		</form:form>
			</div>
    	</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- <script type="text/javascript">

function postPassword() {
	var oldpass = $("#currentpassword").val();
	var newpass = $("#password").val();
	var valid = newpass == $("#confirmpassword").val();
	if(!valid) {
		$("confirmError").show();
		return;
	}

	var passData = {"oldpassword":oldpass,"newpassword":newpass};

	//NOTE:  this posting method works when you want to send just a couple
	//	of data items; the @Controller method can then include each datum
	//	as a @RequestParam; the data cannot(!) use the JSON.stringify(data)
	//	method;
 	$.ajax({
	    type: 'POST',
		url: '/recipeorganizer/user/changepassword',
		dataType: 'json',
		data: {
			oldpassword : oldpass,
			newpassword : newpass				 
		}
	})
	.done(function(data) {
		console.log('postPassword() done');
		//$("#errormsg").show().html(data);
	})
	.fail(function(jqXHR, status, error) {
		console.log('postPassword() fail');
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
		var respText = jqXHR.responseText;
		console.log('respText: '+ respText);
		$("#errormsg").show().html(respText);		
	})
};

</script> -->

</html>
