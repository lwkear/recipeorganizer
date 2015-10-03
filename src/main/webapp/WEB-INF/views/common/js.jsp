<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="<c:url value="/resources/js/jquery-2.1.3.js" />"></script>
<script src="<c:url value="/resources/jqueryui-notheme/jquery-ui.js" />"></script>
<script src="<c:url value="/resources/bootstrap/js/bootstrap.js" />"></script>
<script src="<c:url value="/resources/typeahead/typeahead.bundle.js" />"></script>
<script src="<c:url value="/resources/typeahead/bloodhound.js" />"></script>
<script src="<c:url value="/resources/bootstrap-tagsinput/bootstrap-tagsinput.js" />"></script>
<script src="<c:url value="/resources/js/js.cookie.js" />"></script>

<script>
var tiemout;
var maxInactiveInterval;
var remainTime = 30;

function closeTimeout() {
	$("#sessionTimeout").modal('hide');	
	window.location.href = "<c:url value='/errors/expiredSession'/>";
}

function displayTimeout() {
	console.log('displayTimeout()');
	var msg = "Your session will expire in " + remainTime + " seconds";
	$("#timeoutText").text(msg);
	$("#sessionTimeout").modal();
	/* setTimeout(closeTimeout, (remainTime-5)*1000); */	
}

function setSessionTimeout() {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");

	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/setSessionTimeout',
		dataType: 'json',
		data: token,
		beforeSend: function(xhr) {
		   	xhr.setRequestHeader(header, token);
		}
	})
	.done(function(data) {
		console.log('setSessionTimeout() done');
		alert('setTimeout done')
		setTimeout(displayTimeout, timeout);
	})
	.fail(function(jqXHR, status, error) {
		alert('setTimeout fail')
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
	});
}

function getSessionTimeout() {
	var user = Cookies.get('authUser');
	if (user === 'anonymousUser')
		return;
	
	$.ajax({
		type: 'GET',
		url: '/recipeorganizer/getSessionTimeout',
		dataType: 'json'
	})
	.done(function(data) {
		console.log('getSessionTimeout() done');
		maxInactiveInterval = data;
		timeout = (maxInactiveInterval-remainTime)*1000; 
		setTimeout(displayTimeout, timeout);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
	});
}


$( document ).ready(function() {
	$.fn.bootstrapBtn = $.fn.button.noConflict();
});  

/* $(function () {
    var token = $("input[name='_csrf']").val();
    var header = "X-CSRF-TOKEN";
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
}); */
</script>
