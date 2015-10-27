<!DOCTYPE html>
<html>
<head>
<title>Expired</title>
</head>

<%@include file="../common/head.jsp"%>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12">
			<div class="page-header">
				<c:if test="${register}"> 		
					<h3><spring:message code="registration.expired.title"></spring:message></h3>
				</c:if>
				<c:if test="${password}">
					<h3><spring:message code="password.expired.title"></spring:message></h3>
				</c:if>
			</div>			
			<div class="row">
				<div class="span12 center alert alert-danger text-center">
					<span>${message}</span>
				</div>
				<div class="form-group col-sm-12">&nbsp;</div>
		        <div class="form-group col-sm-2 col-sm-offset-5">
		        	<c:if test="${register}">
		        		<a class="btn btn-default" href="<c:url value="/user/resendRegistrationToken"><c:param name="token" value="${token}"/></c:url>" role="button">
		        			<spring:message code="common.resend"></spring:message></a>
					</c:if>
					<c:if test="${password}">
						<a class="btn btn-default" href="<c:url value="/user/resendPasswordToken"><c:param name="token" value="${token}"/></c:url>" role="button">
							<spring:message code="common.resend"></spring:message></a>
					</c:if>
	       		</div>
	       	</div>
		</div>
	</div>     

<%@include file="../common/footer.jsp" %>

</body>
</html>