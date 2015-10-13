<!DOCTYPE html>
<html>
<head>
<title>Error</title>
</head>

<%@include file="../common/js.jsp"%>
<%@include file="../common/head.jsp"%>

<body role="document">

    <%@include file="../common/nav.jsp" %>

	<div class="container">
		<h1>
			Error!  
		</h1>
		<div class="col-sm-12">&nbsp;</div>
		<div class="col-sm-12">&nbsp;</div>
		<c:if test="${param.register}">
			<div class="col-sm-12">
				<div class="span12 center alert alert-danger" style="text-align: center !important;">
					<span>
						Sorry, the registration confirmation information is not valid.  Please click on Sign Up to try again.
					</span>
				</div>
			</div>
		</c:if>
		<c:if test="${param.password}">
			<div class="col-sm-12">
				<div class="span12 center alert alert-danger" style="text-align: center !important;">
					<span>
						Sorry, the password reset information is not valid.
					</span>
				</div>
			</div>
		</c:if>
		<div class="col-sm-12">&nbsp;</div>
		<div class="col-sm-12">&nbsp;</div>
		<c:if test="${param.register}">
			<div class="form-group col-sm-2 col-sm-offset-5">
				<a class="btn btn-default" href="<c:url value="/user/signup"></c:url>" role="button">Sign Up</a>
	        </div>
	    </c:if>
	</div>
	
</body>
<!-- Placed at the end of the document so the pages load faster -->
</html>
