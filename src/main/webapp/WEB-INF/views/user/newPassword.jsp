<!DOCTYPE html>
<html>
<head>

<title>Change Password</title>

<%@include file="../common/head.jsp" %>
<%@include file="../common/js.jsp" %>

</head>

<!-- <body role="document" onload="document.passswordForm.currentpassword.focus();"> -->
<body role="document" onload="document.passwordForm.password.focus();">

	<%@include file="../common/nav.jsp" %>

	<div class="container">
	
		<h2 class="text-center">Change Password</h2>
		
		<div class="row">
			<form:form name="passswordForm" role="form" modelAttribute="passwordDto" method="post">
				<div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="password">New Password</label>
					<form:input class="form-control" type="password" id="password" name="password" placeholder="New Password" path="newPassword" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="confirmpassword">Confirm Password:&nbsp;&nbsp;${confirmError}</label>
					<form:input class="form-control" type="password" id="confirmpassword" placeholder="Confirm password" path="confirmPassword" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
				</div>
		        <div class="form-group col-sm-2 col-sm-offset-5">
					<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit">Submit</button>
        		</div>

				<%-- <div class="col-sm-12 text-center">
					<c:if test="${not empty param.err}">
						<h4 class="control-label text-danger"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></h4>
						<h5>failureUrl.</h5>
					</c:if>
					<c:if test="${not empty param.time}">
						<h4 class="control-label text-danger"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></h4>
						<h5>expiredURL.</h5>
					</c:if>
					<c:if test="${not empty param.invalid}">
						<h4 class="control-label text-danger"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></h4>
						<h5>invalidSessionUrl</h5>
					</c:if>
					<h4 class="control-label text-danger" style="display:none" id="confirmError">Confirmation Password does not match</h4>
					<h4 class="control-label text-danger" style="display:none" id="errormsg">Confirmation Password does not match</h4>
				</div> --%>
				
				<%-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/> --%>
      		</form:form>
		</div>
    </div>
</body>
</html>
