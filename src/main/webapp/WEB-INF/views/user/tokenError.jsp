<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>

<c:if test="${register}">
	<title><spring:message code="registration.title"></spring:message> - <spring:message code="menu.product"></spring:message></title> 		
</c:if>
<c:if test="${password}">
	<title><spring:message code="password.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>
</c:if>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12 title-bar">
			<div class="page-header">
				<c:if test="${register}"> 		
					<h3><spring:message code="registration.title"></spring:message></h3>
				</c:if>
				<c:if test="${password}">
					<h3><spring:message code="password.title"></spring:message></h3>
				</c:if>
			</div>
		</div>
		<div class="col-sm-6 col-sm-offset-3 spacer-vert-sm">
			<h4>${msgHeader}</h4>
		</div>
		<div class="col-sm-6 col-sm-offset-3 spacer-vert-xs">
			<c:forEach var="msg" items="${messages}">
				${msg}
			</c:forEach>
		</div>
        <div class="form-group col-sm-12 text-center spacer-vert-sm">
        	<c:if test="${register && expired}">
        		<a class="btn btn-default" href="<c:url value="/user/resendRegistrationToken"><c:param name="token" value="${token}"/></c:url>" role="button">
        			<spring:message code="common.resend"></spring:message></a>
			</c:if>
        	<c:if test="${register && invalid}">
        		<a class="btn btn-default" href="<c:url value="/user/signup"></c:url>" role="button">
        			<spring:message code="menu.signup"></spring:message></a>
			</c:if>
			<c:if test="${password && expired}">
				<a class="btn btn-default" href="<c:url value="/user/resendPasswordToken"><c:param name="token" value="${token}"/></c:url>" role="button">
					<spring:message code="common.resend"></spring:message></a>
			</c:if>
			<c:if test="${password && invalid}">
				<a class="btn btn-default" href="<c:url value="/user/resetPassword"><c:param name="token" value="${token}"/></c:url>" role="button">
					<spring:message code="password.button"></spring:message></a>
			</c:if>
      	</div>
	</div>     

<%@include file="../common/footer.jsp" %>

</body>
</html>