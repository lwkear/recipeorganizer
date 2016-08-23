var timeout;
var displayModalTimer;
var closeModalTimer;
var maxInactiveInterval;
var remainTime = 30;
var messageMap;

function toFraction(amt) {
	if (amt <= .2) return '&frac18;';
	if (amt <= .3) return '&frac14;';
	if (amt <= .35) return '&frac13;';
	if (amt <= .4) return '&frac38;';
	if (amt <= .5) return '&frac12;';
	if (amt <= .65) return '&frac58;';
	if (amt <= .7) return '&frac23;';
	if (amt <= .8) return '&frac34;';
	if (amt <= .9) return '&frac78;';
}

function convertFractions(element) {
	$(element).each(function(index) {
		var qty = $(this).html();
		var num = Math.floor(qty);
		var dec = (qty - num);
		var code = '';
		if (dec > 0)
			code = toFraction(dec);

		console.log("qty: " + qty);
		console.log("num: " + num);
		console.log("dec: " + dec);
		console.log("code: " + code);

		var frac;
		if (num > 0)
			frac = num + code;
		else
			frac = code;
		
		$(this).html(frac);
	});
}

function setMessageMap() {
	var locale = $('#localeCode').val();
	var load = false;

	if (typeof(Storage) !== "undefined") {
		var len = sessionStorage.length;
		if (len === 0)
			load = true;
		else {
			var lang = sessionStorage.getItem('language');
			if (locale != lang) 
				load = true;
		}
		if (load === true) {
			sessionStorage.setItem('language',locale);
			$('.spring-messages > p').each(function(index) {
				sessionStorage.setItem($(this).attr('id'), $(this).text());
			});
			console.log('messages stored in sessionStorage');
		} else {
			console.log('messages available from sessionStorage');
		}
		
		return null;
		
	} else {
		if (messageMap === null)
			load = true;
		else {
			var lang = messageMap.get('language');
			if (locale != lang) 
				load = true;
		}
		if (load === true) {
			map = new Map();
			$('.spring-messages > p').each(function(index) {
				map.set($(this).attr('id'), $(this).text());
			});
			console.log('messages stored in messageMap');
			return map;
		}
		else {
			console.log('messages available from messageMap');
			return messageMap;
		}			
	}
}

function getMessage(key) {
	if (typeof(Storage) !== "undefined") {
		return sessionStorage.getItem(key);
	}
	if (messageMap !== null) {
		return messageMap.get(key);
	}
	return "";
}

function displayLoginMsg() {
	$("#messageTitle").text(getMessage('timeout.title'));
	$("#messageMsg").text(getMessage('timeout.text3'));
	$(".msgDlgBtn").hide();
	$("#okBtn").show();
	$("#okBtn").one('click', null, redirectLogin);
	$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
	$("#messageDlg").on('hidden.bs.modal', function(){$("#okBtn").unbind('click');})
	$("#messageDlg").modal('show');
}
	
function redirectLogin() {
	$("#messageDlg").modal('hide');
	var loginUrl = appContextPath + "/user/login";
	window.location.href = loginUrl;
}

function closeTimeout() {
	$("#sessionTimeout").modal('hide');

	//prior to logging out the user, double-check that
	//the user has not already been logged out, presumably by
	//a session in another tab; this prevents a CSRF error
	var user = Cookies.get('authUser');
	if (user === 'anonymousUser') {
		var thankyouUrl = appContextPath + "/thankyou";
		window.location.href = thankyouUrl;
	}
	else	
		submitLogoutForm();
}

function displayTimeout() {
	console.log('displayTimeout()');
	
	//prior to displaying the timeout dialog, double-check that
	//the user has not already been logged out, presumably by
	//a session in another tab
	var user = Cookies.get('authUser');
	if (user === 'anonymousUser') {
		clearTimeout(closeModalTimer);
		displayLoginMsg();		
		return;
	}	
	
	$("#timeLeft").text(remainTime);
	$("#sessionTimeout").modal();
	closeModalTimer = setTimeout(closeTimeout, ((remainTime-15)*1000));	
}

function setSessionTimeout() {
	clearTimeout(closeModalTimer);
	
	$.ajax({
		type: 'POST',
		url: appContextPath + '/setSessionTimeout'
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
	var uri = document.documentURI;
	console.log('document.documentURI: ' + uri);
	var url = document.URL;
	console.log('document.URL: ' + url);
	console.log('appContextPath: ' + appContextPath);
	
	var user = Cookies.get('authUser');
	if (user === 'anonymousUser')
		return;

	$.ajax({
		type: 'GET',
		url: appContextPath + '/getSessionTimeout',
		dataType: 'json'
	})
	.done(function(data) {
		console.log('getSessionTimeout() done');
		maxInactiveInterval = data;
		timeout = (maxInactiveInterval-remainTime)*1000; 
		displayModalTimer = setTimeout(displayTimeout, timeout);
	})
	.fail(function(jqXHR, status, error) {
		console.log('getTimeout fail');
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
	//TODO: GUI: the above is not true in iPad-sized viewport - the search input is hidden
	var control = $(':input:visible:enabled:eq(2)').focus();
}

function blurInputFocus()
{
	//for a few pages that should not have focus on any control
	//add this to the body onload
	$(':input:focus').blur();
}

function findFirstError() {
	$('.has-error:first > .form-control:first').focus();
}

function scrollPage(section) {
	//first check if the page is in add recipe mode
	var btn = document.getElementById('proceedBtn');
	if (btn != null) {
		document.getElementById('proceedBtn').scrollIntoView();
		return;
	}
	
	//opted not to do anything for now if the page is edit recipe mode
	//there are too many potential issues and variables, e.g., multiple panels open, multiple sections for ingreds and/or instructs, etc.
	/*if (section === 'ingred') {
		var div = document.getElementsByClassName('ingredBottom');
		if (div != null) {
			document.getElementsByClassName('ingredBottom')[0].scrollIntoView(false);
			return;
		}
	}
	
	if (section === 'instruct') {
		var div = document.getElementsByClassName('instructBottom');
		if (div != null) {
			document.getElementsByClassName('instructBottom')[0].scrollIntoView(false);
			return;
		}
	}*/
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

function cancelSubmitRecipe() {
	$("#messageTitle").text(getMessage('common.cancel'));
	$("#messageMsg").text(getMessage('recipe.submit.cancel'));
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', continueSubmitCancel);
	$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
	$("#messageDlg").on('hidden.bs.modal', function(){$("#yesBtn").unbind('click');})
	$("#messageDlg").modal('show');
} 

function continueSubmitCancel(e) {
	$("#messageDlg").modal('hide');
	$("#cancelSubmitBtn").click();
}

function postFailed(error) {
	displayOKMsg(getMessage('errordlg.title'), error);
}

function fixTags() {
	var tags = $("#tags").html();
	if (tags) {
		console.log(tags);		
		var newtags = tags.substr(1,tags.length-2);
		$("#tags").html(newtags);
		console.log(newtags);
	}
}

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

    $('[data-toggle="tooltip"]').tooltip({
    	container : 'body'
    });
   	$('#submittedCarousel').carousel({
   		interval: 20000	//: false
   	});
   	$('#viewedCarousel').carousel({
   		interval: 15000	//: false
   	});
    
	messageMap = setMessageMap();
	
	setInputFocus();
	findFirstError();

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
    
    getSessionTimeout();
   
	$(document)
	.on('blur', '.maxSize', function(e)
	{
		var id = $(this).attr('id');
		var err = '#' + id + 'ErrMsg';
		var max = $(this).attr('data-max');
		var maxNum = parseInt(max);
		var chars = $(this).val().length;
		if (chars > maxNum) {
			var msg = getMessage('common.size.max');
			var fmt = String.format(msg, max);
			$(err).html(fmt).show();
			e.preventDefault();
		}
		else
			$(err).html(fmt).hide();
	})
	.on('click', '#fakeSubmitCancel', function(e)
	{
		e.preventDefault();
		cancelSubmitRecipe();
	})
});