<!DOCTYPE html>
<html>
<head>

<title>Forgot Password</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document" onload="document.profileForm.email.focus();">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<%-- <h3><spring:message code="signup.title"></spring:message></h3> --%>
				<h3>Forgot Password</h3>
			</div>			
			<div class="row">
				<form:form role="form" name="forgotPassword" modelAttribute="userEmail" method="post">
			        <div class="row">
						<label class="control-label col-sm-4 col-sm-offset-4" for="email">Email</label>		        
						<div class="col-sm-12 col-sm-offset-4">
							<div class="form-group col-sm-4" style="padding-left:0px;">
								<form:input type="text" class="form-control" id="email" name="email" placeholder="Email" path="email" autocomplete="off"/>
							</div>
						</div>
					</div>
					<div class="form-group col-sm-4 col-sm-offset-4">
					</div>
			        <div class="row">				
				        <div class="form-group col-sm-2 col-sm-offset-5">
							<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit">Submit</button>
		        		</div>
	        		</div>
	      		</form:form>
			</div>
    	</div>
    </div>
    
<%@include file="../common/footer.jsp" %>
    
</body>
</html>

				<%-- <div class="col-sm-12 text-center">
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
				</div> --%>

				<%-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/> --%>				