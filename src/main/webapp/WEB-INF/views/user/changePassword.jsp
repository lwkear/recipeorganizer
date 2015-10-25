<!DOCTYPE html>
<html>
<head>

<title>Change Password</title>

<%@include file="../common/head.jsp" %>

</head>

<body role="document" onload="document.password.focus();">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="changepswd.title"></spring:message></h3>
			</div>			
			<div class="row">
				<div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="currentpassword">
						<spring:message code="password.currentpassword"></spring:message></label>
					<input class="form-control" type="password" id="currentpassword" name="currentpassword" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="password">
						<spring:message code="password.newpassword"></spring:message></label>
					<input class="form-control" type="password" id="password" name="password" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
					<label class="control-label" for="confirmpassword">
						<spring:message code="password.confirmpassword"></spring:message>&nbsp;&nbsp;${confirmError}</label>
					<input class="form-control" type="password" id="confirmpassword" autocomplete="off"/>
				</div>
				<div class="form-group col-sm-4 col-sm-offset-4">
				</div>
		        <div class="form-group col-sm-2 col-sm-offset-5">
					<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit" onclick="postPassword()">
						<spring:message code="common.submit"></spring:message></button>
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
		//$("#errormsg").show().html(data);
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
