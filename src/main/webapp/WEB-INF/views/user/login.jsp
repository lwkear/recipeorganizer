<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>
<%@include file="../common/js.jsp" %>

<title>Login</title>

</head>

<body role="document" onload="document.loginForm.username.focus();">

	<%@include file="../common/nav.jsp" %>

	<div class="container">
	
		<h2 class="text-center">Please Login</h2>
		
		<div class="row">
			<form name="loginForm" action="<c:url value='/user/login'/>" method="post">
		        <div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="username">Email</label>
					<input class="form-control" type="text" id="username" name="username" placeholder="Email" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="password">Password</label>
					<input class="form-control" type="password" id="password" name="password" placeholder="Password" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
					<div class="checkbox-inline">
						<input class="" type="checkbox" id="rememberMe" name="rememberMe"/><strong>Remember Me</strong>
					</div>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
				</div>
		        <div class="form-group col-sm-2 col-sm-offset-5">
					<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit">Submit</button>
        		</div>

				<div class="col-sm-12 text-center">
					<c:if test="${not empty param.err}">
						<h4 class="control-label text-danger"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></h4>
						<h5>failureUrl.</h5>
					</c:if>
					<c:if test="${not empty param.time}">
						<h4 class="control-label text-info"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></h4>
						<h5>expiredURL.</h5>
					</c:if>
					<c:if test="${not empty param.invalid}">
						<h4 class="control-label text-info"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></h4>
						<h5>invalidSessionUrl</h5>
					</c:if>
				</div>
				
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      		</form>
		</div>
    </div>
</body>
</html>
