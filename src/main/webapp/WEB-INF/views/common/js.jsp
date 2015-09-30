<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="<c:url value="/resources/js/jquery-2.1.3.js" />"></script>
<script src="<c:url value="/resources/jqueryui-notheme/jquery-ui.js" />"></script>
<script src="<c:url value="/resources/bootstrap/js/bootstrap.js" />"></script>
<script src="<c:url value="/resources/typeahead/typeahead.bundle.js" />"></script>
<script src="<c:url value="/resources/typeahead/bloodhound.js" />"></script>
<script src="<c:url value="/resources/bootstrap-tagsinput/bootstrap-tagsinput.js" />"></script>

<script>
var tiemout;
var maxInactiveInterval;
var remainTime = 10;

function displayTimeout() {
	console.log('displayTimeout()');
	var msg = "Your session will expire in " + remainTime + " seconds";
	$("#timeoutText").text(msg);
	$("#sessionTimeout").modal();	
}

function setSessionTimeout() {
	$.ajax({
		type: 'GET',
		url: '/recipeorganizer/setSessionTimeout',
		dataType: 'json'
	})
	.done(function(data) {
		console.log('setSessionTimeout() done');
		setTimeout(displayTimeout, timeout);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
	});
}

function getSessionTimeout() {
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
	//getSessionTimeout();
});  
</script>
