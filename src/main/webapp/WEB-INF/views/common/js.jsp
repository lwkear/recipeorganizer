<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="<c:url value="/resources/js/jquery-2.1.3.js" />"></script>
<script src="<c:url value="/resources/jqueryui-notheme/jquery-ui.js" />"></script>
<script src="<c:url value="/resources/bootstrap/js/bootstrap.js" />"></script>
<script src="<c:url value="/resources/typeahead/typeahead.bundle.js" />"></script>
<script src="<c:url value="/resources/typeahead/bloodhound.js" />"></script>
<script src="<c:url value="/resources/bootstrap-tagsinput/bootstrap-tagsinput.js" />"></script>
<script src="<c:url value="/resources/js/js.cookie.js" />"></script>

<script>
var timeout;
var displayModalTimer;
var closeModalTimer;
var maxInactiveInterval;
var remainTime = 30;

function closeTimeout() {
	$("#sessionTimeout").modal('hide');
	submitLogoutForm();
	/* window.location.href = "<c:url value='/errors/expiredSession'/>"; */
}

function displayTimeout() {
	console.log('displayTimeout()');
	var msg = "Your session will expire in " + remainTime + " seconds";
	$("#timeoutText").text(msg);
	$("#sessionTimeout").modal();
	closeModalTimer = setTimeout(closeTimeout, ((remainTime-15)*1000));	
}

function setSessionTimeout() {
	clearTimeout(closeModalTimer);
	
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");

	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/ajax/auth/setSessionTimeout',
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
		url: '/recipeorganizer/ajax/all/getSessionTimeout',
		dataType: 'json'
	})
	.done(function(data) {
		console.log('getSessionTimeout() done');
		maxInactiveInterval = data;
		timeout = (maxInactiveInterval-remainTime)*1000; 
		displayModalTimer = setTimeout(displayTimeout, timeout);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
	});
}

$( document ).ready(function() {
	$.fn.bootstrapBtn = $.fn.button.noConflict();
});  
</script>
