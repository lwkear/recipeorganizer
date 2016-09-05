<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="account.join.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document" onload="blurInputFocus()">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<div class="page-header"> 		
				<h3><spring:message code="account.join.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="row">
				<div class="form-group col-sm-6 col-sm-offset-3 text-center"><h4><spring:message code="account.join.message1"></spring:message></h4></div>
				<div class="form-group col-sm-6 col-sm-offset-3">
					<spring:message code="account.join.message2"></spring:message>
					<spring:message code="account.join.message3"></spring:message>
				</div>
				<div class="col-sm-2 col-sm-offset-5 text-center spacer-vert-xs">
					<a class="btn btn-primary" href="<c:url value="/user/signup"></c:url>" role="button"><spring:message code="menu.signup"></spring:message></a>
				</div>
			</div>
			<div class="row">
				<div class="form-group col-sm-6 col-sm-offset-3 text-center spacer-vert-md"><spring:message code="account.join.message4"></spring:message></div>
				<div class="col-sm-2 col-sm-offset-5 text-center spacer-vert-xs">
					<a class="btn btn-default" href="<c:url value="/user/login"></c:url>" role="button"><spring:message code="menu.login"></spring:message></a>
				</div>
			</div>
    	</div>
    </div>
    
<%@include file="../common/footer.jsp" %>
    
</body>
</html>
