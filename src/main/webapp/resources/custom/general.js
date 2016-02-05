var timeout;
var displayModalTimer;
var closeModalTimer;
var maxInactiveInterval;
var remainTime = 30;
var messageMap;

function setMessageMap() {
	map = new Map();
	$('.spring-messages > p').each(function(index) {
		map.set($(this).attr('id'), $(this).text());
	});
	console.log('messageMap updated');
	return map;
}

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

$('.faq_question').click(function() {
	 
    if ($(this).parent().is('.open')){
        $(this).closest('.faq').find('.faq_answer_container').animate({'height':'0'},100); /*500*/
        $(this).closest('.faq').removeClass('open');

        }else{
            var newHeight =$(this).closest('.faq').find('.faq_answer').height() +'px';
            $(this).closest('.faq').find('.faq_answer_container').animate({'height':newHeight},100); /*500*/
            $(this).closest('.faq').addClass('open');
        }

});

//$(document).ready(function() {
$(function() {
	
	$.fn.bootstrapBtn = $.fn.button.noConflict();

	if (!String.format) {
		String.format = function(format) {
			var args = Array.prototype.slice.call(arguments, 1);
			return format.replace(/{(\d+)}/g, function(match, number) { 
				return typeof args[number] != 'undefined'
					? args[number] 
					: match
					;
			});
		};
	}

	if (messageMap == null)
		messageMap = setMessageMap();
	
	setInputFocus();

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
    
    getSessionTimeout();
   
    $('[data-toggle="tooltip"]').tooltip();
    
	$(document)
	.on('blur', '.maxSize', function(e)
	{
		var id = $(this).attr('id');
		var err = '#' + id + 'ErrMsg';
		var max = $(this).attr('data-max');
		var maxNum = parseInt(max);
		var chars = $(this).val().length;
		if (chars > maxNum) {
			var msg = messageMap.get('common.size.max');
			var fmt = String.format(msg, max);
			$(err).html(fmt).show();
		}
		else
			$(err).html(fmt).hide();
	})
});