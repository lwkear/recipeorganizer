<!DOCTYPE html>
<html>
<head>

<title>New Password</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document" onload="document.passwordForm.password.focus();">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="newpswd.title"></spring:message></h3>
			</div>			
			<div class="row">
				<form:form name="passswordForm" role="form" modelAttribute="passwordDto" method="post">
					<div class="form-group col-sm-4 col-sm-offset-4">
						<label class="control-label" for="password">
							<spring:message code="password.newpassword"></spring:message></label>
						<form:input class="form-control" type="password" id="password" name="password" path="newPassword" autocomplete="off"/>
					</div>
					<div class="form-group col-sm-4 col-sm-offset-4">
						<label class="control-label" for="confirmpassword">
							<spring:message code="password.confirmpassword"></spring:message>&nbsp;&nbsp;${confirmError}</label>
						<form:input class="form-control" type="password" id="confirmpassword" path="confirmPassword" autocomplete="off"/>
					</div>
					<div class="form-group col-sm-4 col-sm-offset-4">
					</div>
			        <div class="form-group col-sm-2 col-sm-offset-5">
						<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit">
							<spring:message code="common.submit"></spring:message></button>
	        		</div>
	      		</form:form>
			</div>
	    </div>
	</div>
	
<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
