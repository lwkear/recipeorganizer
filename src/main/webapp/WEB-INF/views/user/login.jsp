<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="login.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="login.title"></spring:message></h3>
			</div>			
			<div class="row">
				<form name="loginForm" action="<c:url value='/user/login'/>" method="post">
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-4 <c:if test="${not empty passwordError}">has-error</c:if>">
							<label class="control-label" for="username"><spring:message code="common.email"></spring:message></label>
							<input class="form-control" type="text" id="username" name="username" autocomplete="off"/>
						</div>
					</div>
					<div class="col-sm-12">
						<label class="control-label col-sm-5 col-sm-offset-4" for="password">
							<spring:message code="common.password"></spring:message></label>
						<div class="col-sm-8 col-sm-offset-4">
							<div class="form-group col-sm-6" style="padding-left:0px;">
								<input class="form-control" type="password" id="password" name="password" autocomplete="off"/>
							</div>
							<div class="col-sm-2">
								<a class="btn btn-default" href="<c:url value="/user/forgotPassword"></c:url>" role="button">
									<spring:message code="login.forgotpassword"></spring:message></a>
							</div>
						</div>
					</div>
					<div class="col-sm-12">
						<div class="form-group col-sm-4 col-sm-offset-4">
							<div class="checkbox-inline">
								<input class="" type="checkbox" id="rememberMe" name="rememberMe"/><strong><spring:message code="login.rememberme"></spring:message></strong>
							</div>
						</div>
					</div>
					<c:if test="${error != null}">
						<div class="col-sm-12 text-center">
							<h5 class="control-label text-danger">${error}</h5>
						</div>
					</c:if>
			        <div class="form-group col-sm-2 col-sm-offset-5 text-center spacer-vert-sm">
						<button class="btn btn-primary" type="submit" name="submit"><spring:message code="common.submit"></spring:message></button>
	        		</div>
			        <div class="col-sm-12 spacer-vert-lg text-center">
			        	<div><spring:message code="login.notamemebr"></spring:message></div>
			        </div>
			        <div class="col-sm-12 spacer-vert-xs text-center">
						<a class="btn btn-default" href="<c:url value="/user/signup"></c:url>" role="button">
								<spring:message code="menu.signup"></spring:message>
						</a>
	        		</div>
	        		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	      		</form>
			</div>
		</div>
    </div>

<%@include file="../common/footer.jsp" %>

</body>
</html>