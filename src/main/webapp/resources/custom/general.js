var timeout;
var displayModalTimer;
var closeModalTimer;
var maxInactiveInterval;
var remainTime = 30;

var messageMap = new Map();
$('.spring-messages > p').each(function(index) {
	messageMap.set($(this).attr('id'), $(this).text());
});
console.log('messageMap updated');

function closeTimeout() {
	$("#sessionTimeout").modal('hide');
	//TODO: SECURITY: check if already logged out (more than one tab/window expires at once)
	submitLogoutForm();
}

function displayTimeout() {
	console.log('displayTimeout()');
	$("#timeLeft").text(remainTime);
	$("#sessionTimeout").modal();
	closeModalTimer = setTimeout(closeTimeout, ((remainTime-15)*1000));	
}

function setSessionTimeout() {
	clearTimeout(closeModalTimer);
	
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
		setTimeout(displayTimeout, timeout);
	})
	.fail(function(jqXHR, status, error) {
		console.log('setTimeout fail');
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

function submitSearchForm() {
	document.forms["searchForm"].submit();
}

function setInputFocus()
{
	//the first two controls on any page are the search input and button
	//:eq(2) ensures that those controls do NOT get the focus
	var control = $(':input:visible:enabled:eq(2)').focus();
}

function blurInputFocus()
{
	//for a few pages that should not have focus on any control
	//add this to the body onload
	$(':input:visible:enabled:focus').blur(); 
}

function displayOKMsg(title, msg) {
	$("#messageTitle").text(title);
	$("#messageMsg").text(msg);
	$(".msgDlgBtn").hide();
	$("#okBtn").show();
	$('#messageDlg').modal({backdrop: 'static', keyboard: false});
}

$(document).ready(function() {
	$.fn.bootstrapBtn = $.fn.button.noConflict();

	setInputFocus();

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
    
    getSessionTimeout();
});
