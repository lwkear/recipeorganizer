<!DOCTYPE html>
<html>
<head>

<title>Change Password</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<%-- <h3><spring:message code="signup.title"></spring:message></h3> --%>
				<h3>Change Password</h3>
			</div>			
			<div class="row">
				<div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="currentpassword">Current Password</label>
					<input class="form-control" type="password" id="currentpassword" name="currentpassword" placeholder="Current Password" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="password">New Password</label>
					<input class="form-control" type="password" id="password" name="password" placeholder="New Password" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="confirmpassword">Confirm Password:&nbsp;&nbsp;${confirmError}</label>
					<input class="form-control" type="password" id="confirmpassword" placeholder="Confirm password" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
				</div>
		        <div class="form-group col-sm-2 col-sm-offset-5">
					<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit" onclick="postPassword()">Submit</button>
        		</div>
			</div>
    	</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<script type="text/javascript">

function postPassword() {
	var oldpass = $("#currentpassword").val();
	var newpass = $("#password").val();
	var valid = newpass == $("#confirmpassword").val();
	if(!valid) {
		$("confirmError").show();
		return;
	}

	var passData = {"oldpassword":oldpass,"newpassword":newpass};

	//NOTE:  this posting method works when you want to send just a couple
	//	of data items; the @Controller method can then include each datum
	//	as a @RequestParam; the data cannot(!) use the JSON.stringify(data)
	//	method;
 	$.ajax({
	    type: 'POST',
		url: '/recipeorganizer/user/changepassword',
		dataType: 'json',
		data: {
			oldpassword : oldpass,
			newpassword : newpass				 
		}
	})
	.done(function(data) {
		console.log('postPassword() done');
		$("#errormsg").show().html(data);
		//window.location.href = "<c:url value='/home'></c:url>";
	})
	.fail(function(jqXHR, status, error) {
		console.log('postPassword() fail');
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
		var respText = jqXHR.responseText;
		console.log('respText: '+ respText);
		$("#errormsg").show().html(respText);		
	})
};

//NOTE: this routine appends the csrf info to an AJAX call
//TODO: GUI: make this available to all .jsp's
$(document).ready(function() {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

</script>
</html>


<%-- 				<div class="col-sm-12 text-center">
					<c:if test="${not empty param.err}">
						<h4 class="control-label text-danger"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></h4>
						<h5>failureUrl.</h5>
					</c:if>
					<c:if test="${not empty param.time}">
						<h4 class="control-label text-danger"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></h4>
						<h5>expiredURL.</h5>
					</c:if>
					<c:if test="${not empty param.invalid}">
						<h4 class="control-label text-danger"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></h4>
						<h5>invalidSessionUrl</h5>
					</c:if>
					<h4 class="control-label text-danger" style="display:none" id="confirmError">Confirmation Password does not match</h4>
					<h4 class="control-label text-danger" style="display:none" id="errormsg">Confirmation Password does not match</h4>
				</div>
 --%>				
				<%-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/> --%>
