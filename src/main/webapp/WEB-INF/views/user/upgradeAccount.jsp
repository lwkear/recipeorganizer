<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="account.upgrade.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document" onload="blurInputFocus()">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<div class="page-header"> 		
				<h3><spring:message code="account.upgrade.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">			
			<div class="row">
				<div class="form-group col-sm-6 col-sm-offset-3 text-center"><h4><spring:message code="account.upgrade.header"></spring:message></h4></div>
				<div class="form-group col-sm-6 col-sm-offset-3">
					<spring:message code="account.upgrade.message1"></spring:message>
					<spring:message code="account.upgrade.message3"></spring:message>
				</div>
				<div class="form-group col-sm-6 col-sm-offset-3 spacer-vert-xs"><spring:message code="account.upgrade.message2"></spring:message></div>
				<form name="upgradeForm" role="form" method="post">
					<div class="col-sm-2 col-sm-offset-5 text-center spacer-vert-xs">
						<button class="btn btn-primary" type="submit" name="submit"><spring:message code="common.upgrade"></spring:message></button>
					</div>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				</form>
				
			</div>
    	</div>
    </div>
    
<%@include file="../common/footer.jsp" %>
    
</body>
</html>
