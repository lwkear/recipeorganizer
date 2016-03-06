<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="account.upgrade.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document" onload="blurInputFocus()">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="account.upgrade.title"></spring:message></h3>
			</div>			
			<div class="row">
				<div class="form-group col-sm-6 col-sm-offset-3 text-center"><spring:message code="account.upgrade.message1"></spring:message></div>
				<div class="form-group col-sm-6 col-sm-offset-3 text-center"><spring:message code="account.upgrade.message2"></spring:message></div>
				<form name="upgradeForm" role="form" method="post">
					<div class="col-sm-2 col-sm-offset-5 text-center spacer-vert-xs">
						<button class="btn btn-primary" type="submit" name="submit"><spring:message code="common.upgrade"></spring:message></button>
					</div>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				</form>
				<div class="form-group col-sm-6 col-sm-offset-3 text-center spacer-vert-md"><spring:message code="account.upgrade.message3"></spring:message></div>
			</div>
    	</div>
    </div>
    
<%@include file="../common/footer.jsp" %>
    
</body>
</html>
