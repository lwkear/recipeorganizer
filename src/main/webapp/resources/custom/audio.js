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
	//on the laptop it appears that the ended event is fired before the audio has actually ended
	//adding a second delay before starting to record seems to work
	setTimeout(function(){ recording = true; }, 1000);
}

//user clicked on the forward VCR button
function playAudio(userId, recipeId, section, type) {
	$('.audioBtn').blur();
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
		if (!recording) {
			if (msg.results) {
				var words = msg.results[0].alternatives[0].transcript;
				if (words) {
					console.log('receive-json: words='+words);
					var command = words.match(/pause|continue|start|play|replay|stop/g);
					if (command != null) {
						console.log('receive-json: match='+command);
						if (command[0].match(/pause/) != null)
							audio.pause();
						if (command[0].match(/continue|start/) != null)
							audio.play();
						if (command[0].match(/stop/) != null) {
							audio.pause();
							audio.currentTime = 0;
						}
						if (command[0].match(/play|replay/) != null) {
							audio.currentTime = 0;
							audio.pause();							
						}
					}
				}
			}
			return;
		}
		if (msg.state !== 'listening') {
			if (msg.results) {
				console.log(msg.results[0].alternatives[0].transcript);
				if (msg.results[0].final) {
					console.log('receive-json: ' + msg);
					recording = false;
					postResults(msg);				
				}
			}
		}
	});
}

//TODO: SPEECH: combine startConversation with postResults (redundant code)
//TODO: SPEECH: blob error was not handled correctly - appeared as console error but no alert, etc.
//TODO: SPEECH: visually indicate that the mic is on
//TODO: SPEECH: click on a live mic to turn it off
//TODO: SPEECH: how can you say "pause", "continue", "stop" or "replay" if the mic is not recording while the audio is playing???

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
			    audio.onload = function(evt) {
			    	window.URL.revokeObjectUrl(audio.src);
			    };
			    audio.play();
			}
			else {
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
	console.log('postResults: start');
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
				console.log('postResults: status=200');
				// get binary data as a response
				var blob = new Blob([xhr.response], {type: 'audio/ogg; codecs=opus'});
			    $('.audioCtl').get(0).src = window.URL.createObjectURL(blob);
			    audio.onload = function(evt) {
			    	window.URL.revokeObjectUrl(audio.src);
			    };
			    console.log('postResults: before play');
			    audio.play();
			    console.log('postResults: after play');
			}
			else {
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
