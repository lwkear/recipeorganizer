/***********************/
/*** audio functions ***/
/***********************/
function canPlay() {
	console.log('function canPlay');
	var a = document.createElement('audio');
	return !!(a.canPlayType && a.canPlayType('audio/ogg; codecs="vorbis"').replace(/no/, ''));
}

function canTalk() {
	console.log('function canTalk');
	var locale = $('#localeCode').val();
	var recipeLocale = $('#recipeLocaleCode').val();
	var result = false;
	if ((locale === 'fr') || (recipeLocale === 'fr')) {
		console.log('function canTalk: french');
		return;
	}
	else {
		if (navigator.mediaDevices.enumerateDevices) {
			navigator.mediaDevices.enumerateDevices()
			.then(function gotDevices(devices) {
				devices.forEach(function(device) {
					if (device.kind === 'audioinput') {
						console.log('function canTalk .then: found audioinput');
						$('#startSpeech').show();						
						return;
					}
				})
			})
		}
	}
}

var stream = null;
var keywordsArray = "";
var recording = false;
var micOn = false;
var permissionGranted = false;
var stream;
var audio;
//var testCounter = 0;	//testing

//show the VCR buttons for compatible browsers (canPlay() is in general.js)
function checkAudio() {
	if (canPlay()) {
		$('.audioBtn').show();
		audio = $('.audioCtl').get(0);
		audio.addEventListener("ended", startListening);
	}
	canTalk();
}

function getPermission() {
	permissionGranted = false;
	navigator.mediaDevices
	.getUserMedia({
		video: false, audio: true
	})
	.then(function(stream){
		alert('got it');
		//if (stream)
		//	stream.stop();
		permissionGranted = true;
	})
	.catch(function(err) {
		alert(err);
	});	
	/*try {
		navigator.mediaDevices
		.getUserMedia({
			video: false, audio: true
		});		
	}
	catch(err) {
		alert('try/catch: ' + err);
	};*/
}

function startListening() {
	if (micOn) {
		console.log('startListening');
		//on the laptop it appears that the ended event is fired before the audio has actually ended
		//adding a second and a half delay before starting to record seems to work
		setTimeout(function(){ recording = true; }, 1500);
	}
}

function turnOffConversation() {
	console.log('turnOffConversation');
	micOn = false;
	recording = false;
	$('#startSpeech').removeClass('mic-btn-link-active');
	$('#startSpeech').prop('disabled', true);
	//the user may allow the microphone to be used while the unavailable message is being played
	//wait a couple of seconds to turn it off
	setTimeout(function() { 
		if (stream)
			stream.stop();
	}, 3000);
}

//user clicked on the forward VCR button
function playAudio(userId, recipeId, section, type) {
	console.log('function playAudio');
	$('.audioBtn').blur();
	var origin = location.origin;
	var url = appContextPath + '/getRecipeAudio?userId=' + userId + '&recipeId=' + recipeId + '&section=' + section + '&type=' + type;
	var currUrl = audio.src;
	var matchUrl = origin + url;
	var ready = audio.readyState;
	var paused = audio.paused
	if (paused && (ready === 4) && (matchUrl === currUrl)) {
		audio.play();
	}
	else {
		audio.setAttribute('src', url);
		audio.play();			
	}
}

function getKeywords() {
	console.log('function getKeywords');
	var recipeId = $('#recipeId').val();
	//var recipeId = 999;	//testing
	var url = appContextPath + '/getWatsonKeywords?recipeId=' + recipeId;
	$.getJSON(url)
		.done(function (data) {
			console.log("get keywords: " + data);
			keywordsArray = $.makeArray(data);
			console.log("mapped json: " + keywordsArray);
			//get the token next
			getSTTToken();
	 	})
	 	.fail(function (jqXHR, status, err) {
	 		var data = jqXHR.responseJSON;
			console.log('fail: '+ data.msg);
			turnOffConversation();
			postFailed(data.msg);
	 	});
}

//token required to initiate Watson STT
function getSTTToken() {
	console.log('function getSTTToken');
	$.ajax({
	    type: 'GET',
		contentType: 'application/json',
	    url: appContextPath + '/getWatsonToken'
	})
	.done(function(data) {
		console.log('done data: '+ data);
		//initialize the microphone
		listen(data);
		//start the conversation
		getGreeting();
	})
	.fail(function(jqXHR, status, error) {
 		var data = jqXHR.responseJSON;
		console.log('fail: '+ data.msg);
		turnOffConversation();
		postFailed(data.msg);
	});
}

function getGreeting() {
	console.log('function getGreeting');
	$('#startSpeech').addClass('mic-btn-link-active');				
	micOn = true;
	var start = {start:null};
	postResults(start);
}

function listen(token) {
    stream = WatsonSpeech.SpeechToText.recognizeMicrophone({
    	token: token,
    	keepMicrophone: true,
        readableObjectMode: true,
        objectMode: true,
        word_confidence: true,
        format: false,
        keywords: keywordsArray,
        keywords_threshold : 0.2,
        continuous : true,
        inactivity_timeout : -1,
    });

	stream.setEncoding('utf8');    
    
	stream.on('error', function(err) {
		//TODO: SPEECH: handle this error?
        console.log('stream.on(error): ' + err);
        alert('stream.on(error): ' + err);
	});

	stream.on('receive-json', function(msg) {
		console.log(msg);
		console.log('receive-json: recording='+recording);
		console.log('receive-json: micOn='+micOn);		
		if (!micOn) {
			return;
		}
		if (!recording) {
			if (msg.results) {
				var words = msg.results[0].alternatives[0].transcript;
				if (words) {
					console.log('receive-json: words='+words);
					var command = words.match(/pause|continue|replay|stop/g);
					if (command != null) {
						console.log('receive-json: match='+command);
						if (command[0].match(/pause/) != null)
							audio.pause();
						if (command[0].match(/continue/) != null)
							audio.play();
						if (command[0].match(/stop/) != null) {
							audio.pause();
							startListening();
						}
						if (command[0].match(/replay/) != null) {
							audio.load();
							audio.play();
						}
					}
				}
			}
			return;
		}
		else {
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
		}
	});
}

function postResults(results) {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	var viewerId = $('#viewerId').val();
	var recipeId = $('#recipeId').val();
	//testing
	/*testCounter = testCounter + 1;	
	//force a server error
	if (testCounter > 3) {
		viewerId = 999;
		recipeId = 999;
		testCounter = 0;
	}*/
	//viewerId = 999;
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
			    audio.currentTime = 0;
			    audio.play();
			}
			else {
				console.log('postResults: status='+this.status);
				var type = this.response.type;
				if ((type === 'blob') || (type === 'application/xml')) {
					// get binary data as a response
					var blob = new Blob([xhr.response], {type: 'audio/ogg; codecs=opus'});
				    $('.audioCtl').get(0).src = window.URL.createObjectURL(blob);
				    audio.onload = function(evt) {
				    	window.URL.revokeObjectUrl(audio.src);
				    };
				    audio.play();
				}
				else {
					if ((type === 'text/html') || (type === 'application/json')) {
						var blob = new Blob([xhr.response], {type: type});
						var reader = new FileReader();
						reader.onload = function(e) {
							var obj = JSON.parse(e.target.result);
							alert(obj.msg);
						};
						reader.readAsText(blob);
					}
				}
				turnOffConversation();
			}
		}		
	};
	xhr.onerror = function(e) {
		var resp = xhr.response;
		console.log('xhr.onerror:'+resp)
		//TODO: SPEECH: replace this alert
		//alert('postResults xhr.error');
	};
	xhr.send();
}

$(function() {
	checkAudio();

	$(document)
	.on('click', '.pauseBtn', function(e) {
		e.preventDefault();
		$('.audioBtn').blur();
		if (!audio.ended) {
			audio.pause();
		}
	})
	.on('click', '.stopBtn', function(e) {
		e.preventDefault();
		$('.audioBtn').blur();
		if (!audio.ended)
			audio.load();
	})
	.on('click', '#startSpeech', function(e) {
		e.preventDefault();
		$(this).blur();
		if (micOn) {			
			$(this).removeClass('mic-btn-link-active');
			micOn = false;
			recording = false;
			if (stream)
				stream.stop();
		}
		else {
			//getPermission();			
			//if (permissionGranted === true)
				getKeywords();
		}
	})
})
