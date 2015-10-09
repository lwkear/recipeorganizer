<!DOCTYPE html>
<html>
<head>

<title>Change Password</title>

<%@include file="../common/head.jsp" %>
<%@include file="../common/js.jsp" %>
<script type="text/javascript">

$(document).ready(function() {

	var token = $("input[name='_csrf']").val();
    var header = "X-CSRF-TOKEN";
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

function postPassword() {
	var oldpassword = $("#currentpassword").val();
	var newpassword = $("#password").val();
	var valid = newpassword == $("#confirmpassword").val();
	if(!valid) {
		$("confirmError").show();
		return;
	}

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");

	var data = {"oldpassword":oldpassword,"newpassword":newpassword};


/* $.post("<c:url value="/user/changePassword"></c:url>",data ,function(data){
window.location.href = "<c:url value="/home"></c:url>";
})
.fail(function(data) {
$("#errormsg").show().html("ajax error");
}); */
	
 	$.ajax({
		headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	    },
	    type: 'POST',
		//url: '/recipeorganizer/ajax/auth/changepassword',
		url: '/recipeorganizer/user/changepassword',
		dataType: 'json',
		data: JSON.stringify(data)
		/* beforeSend: function(xhr) {
		   	xhr.setRequestHeader(header, token);
		} */
		.success(function(data) {
			console.log('postPassword() done');
			window.location.href = "<c:url value='/home'></c:url>";
		})
	
	/* .success(function(data) {
		console.log('postPassword() done');
		window.location.href = "<c:url value='/home'></c:url>";
	})*/
	.fail(function(jqXHR, status, error) {
		//$("#errormsg").show().html(data.responseJSON.error);
		$("#errormsg").show().html("ajax error");
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
	})
 	});

	}
});

</script>

</head>

<!-- <body role="document" onload="document.passswordForm.currentpassword.focus();"> -->
<body role="document" onload="document.currentpassword.focus();">

	<%@include file="../common/nav.jsp" %>

	<div class="container">
	
		<h2 class="text-center">Change Password</h2>
		
		<div class="row">
			<!-- <form name="passswordForm" role="form"> --> <!--  method="post"> -->
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

				<div class="col-sm-12 text-center">
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
				
				<%-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/> --%>
      		<!-- </form> -->
		</div>
    </div>
</body>
</html>
