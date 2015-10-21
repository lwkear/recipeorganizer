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
				<%-- <h3><spring:message code="signup.title"></spring:message></h3> --%>
				<h3>New Password</h3>
			</div>			
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
	      		</form:form>
			</div>
	    </div>
	</div>
	
<%@include file="../common/footer.jsp" %>	
	
</body>
</html>
