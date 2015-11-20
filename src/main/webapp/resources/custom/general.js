var timeout;
var displayModalTimer;
var closeModalTimer;
var maxInactiveInterval;
var remainTime = 30;

function closeTimeout() {
	$("#sessionTimeout").modal('hide');
	//TODO: SECURITY: check if already logged out (more than one tab/window expires at once)
	submitLogoutForm();
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
		url: '/recipeorganizer/ajax/anon/getSessionTimeout',
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

function submitLogoutForm() {
	document.forms["logoutForm"].submit();
}

function setInputFocus()
{
	$(':input:visible:enabled:first').focus(); 
}

$(document).ready(function() {
	$.fn.bootstrapBtn = $.fn.button.noConflict();

	setInputFocus();
	
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});
