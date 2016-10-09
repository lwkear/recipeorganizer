/***********************/
/*** audio functions ***/
/***********************/
function canPlay() { 
	var a = document.createElement('audio');
	return !!(a.canPlayType && a.canPlayType('audio/ogg; codecs="vorbis"').replace(/no/, ''));
}

function canTalk() { 
	if (navigator.mediaDevices.enumerateDevices) {
		navigator.mediaDevices.enumerateDevices()
		.then(function gotDevices(devices) {
			devices.forEach(function(device) {
				if (device.kind === 'audioinput') {
					return true;
				}
			});
		})
	};

	return false;
}

var stream = null;
var keywordsArray = "";
var recording = false;
var stream;
var audio;

//show the VCR buttons for compatible browsers (canPlay() is in general.js)
function checkAudio() {
	if (canPlay()) {
		$('.audioBtn').show();
		audio = $('.audioCtl').get(0);
		audio.addEventListener("ended", startListening);
	}
	if (canPlay()) {
		$('#startSpeech').show();
	}	
}

function startListening() {
	console.log('startListening');
	recording = true;
}

//user clicked on the forward VCR button
function playAudio(userId, recipeId, section, type) {
	$('.audioBtn').blur();
	//var audio = null;
	//audio = $('.audioCtl').get(0);
	var url = appContextPath + '/getRecipeAudio?userId=' + userId + '&recipeId=' + recipeId + '&section=' + section + '&type=' + type;
	var currUrl = audio.src;
	var ready = audio.readyState;
	var paused = audio.paused
	if (paused && (ready > 0) && (url === currUrl))
		audio.play();
	else {
		audio.setAttribute('src', url);
		audio.play();
	}
}

//token required to initiate Watson STT
function getSTTToken() {
	$.ajax({
	    type: 'GET',
		contentType: 'application/json',
	    url: appContextPath + '/getWatsonToken'
	})
	.done(function(data) {
		console.log('done data: '+ data);
		listen(data);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
	});
}

function listen(token) {
	$('#response').html('');
    stream = WatsonSpeech.SpeechToText.recognizeMicrophone({
        token: token,
        readableObjectMode: true,
        objectMode: true,
        word_confidence: true,
        format: false,
        keywords: keywordsArray,
        keywords_threshold : 0.5,
        continuous : true,
        inactivity_timeout : -1,
        //interim_results : false,
        keepMicrophone: navigator.userAgent.indexOf('Firefox') > 0
    });

	stream.setEncoding('utf8');    
    
	stream.on('error', function(err) {
        console.log('error:' + err);
	});

	stream.on('receive-json', function(msg) {
		console.log(msg);
		console.log('receive-json: recording='+recording);
		if (!recording)
			return;
		if (msg.state !== 'listening') {
			if (msg.results) {
				console.log(msg.results[0].alternatives[0].transcript);
				if (msg.results[0].final) {
					console.log('receive-json: ' + msg);
					recording = false;
					postResults(msg);				
					//stream.close;
					/*audio = $('.audioCtl').get(0);
					if (audio.ended) {
						console.log('receive-json: ' + msg);
						postResults(msg);				
						stream.close;
					}*/
				}
			}
		}
	});

	/* this function returns only the text result 
	stream.on('data', function(result) {
		if (result.final) { 
			//parseResults(result);
			//postResults(result);
			console.log(result);
			stream.close;
		}
		//console.log(result);
		//stream.close;
		//postResults(result);		
	});
	*/
}

/*function postResults(results) {
	console.log('postResults: ' + results);
	var speech = JSON.stringify(results);
	var viewerId = $(viewerId).val();
	var recipeId = $(recipeId).val();
	var data = {"userId":viewerId,"recipeId":recipeId,"message":speech};
	$.ajax({
	    type: 'POST',
	    url: appContextPath + '/postWatsonResult',
	    dataType: 'json',
	    data: data
	})
	.done(function(data) {
		console.log('done data');
		retrieveAudio(data);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		alert('Error retrieving STT results')
	});
}*/


function startConversation() {
	console.log('startConversation');
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	var viewerId = $('#viewerId').val();
	var recipeId = $('#recipeId').val();
	var url = appContextPath + '/startWatsonConversation?userId=' + viewerId + '&recipeId=' + recipeId;
	
	var xhr = new XMLHttpRequest();
	xhr.open('GET', url, true);
	xhr.setRequestHeader(header, token);
	xhr.responseType = 'blob';
	xhr.onload = function(e) {
		if (this.readyState == 4) {
			if (this.status == 200) {
				// get binary data as a response
				var blob = new Blob([xhr.response], {type: 'audio/ogg; codecs=opus'});
			    $('.audioCtl').get(0).src = window.URL.createObjectURL(blob);
			    //var audio = $('.audioCtl').get(0);
			    audio.onload = function(evt) {
			    	window.URL.revokeObjectUrl(audio.src);
			    };
			    audio.play();
			}
			else {
				//var text = xhr.responseText;
				var resp = xhr.response;				
				alert('startConversation server error');
			}
		}		
	};
	xhr.onerror = function(e) {
		var resp = xhr.response;
		alert('startConversation network error');
	};
	xhr.send();
}

function postResults(results) {
	console.log('postResults');
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	var viewerId = $('#viewerId').val();
	var recipeId = $('#recipeId').val();
	var speech = JSON.stringify(results);
	var url = appContextPath + '/postWatsonResult?userId=' + viewerId + '&recipeId=' + recipeId + '&message=' + speech;
	
	var xhr = new XMLHttpRequest();
	xhr.open('GET', url, true);
	xhr.setRequestHeader(header, token);
	xhr.responseType = 'blob';
	xhr.onload = function(e) {
		if (this.readyState == 4) {
			if (this.status == 200) {
				// get binary data as a response
				var blob = new Blob([xhr.response], {type: 'audio/ogg; codecs=opus'});
			    $('.audioCtl').get(0).src = window.URL.createObjectURL(blob);
			    //var audio = $('.audioCtl').get(0);
			    audio.onload = function(evt) {
			    	window.URL.revokeObjectUrl(audio.src);
			    };
			    audio.play();
			}
			else {
				//var text = xhr.responseText;
				var resp = xhr.response;				
				alert('postResults server error');
			}
		}		
	};
	xhr.onerror = function(e) {
		var resp = xhr.response;
		alert('postResults network error');
	};
	xhr.send();
}

$(function() {
	checkAudio();

	var url = appContextPath + '/getWatsonKeywords';
	$.getJSON(url)
		.done(function (data) {
			console.log("get keywords: " + data);
			keywordsArray = $.makeArray(data);
			console.log("mapped json: " + keywordsArray);
	 	});
	
	$(document)
	.on('click', '.pauseBtn', function(e) {
		$('.audioBtn').blur();
		audio = $('.audioCtl').get(0);
		if (!audio.ended)
			audio.pause();
	})
	.on('click', '.stopBtn', function(e) {
		$('.audioBtn').blur();
		audio = $('.audioCtl').get(0);
		if (!audio.ended)
			audio.load();
	})
	.on('click', '#startSpeech', function(e) {
		startConversation();
		getSTTToken();		
	})
})
