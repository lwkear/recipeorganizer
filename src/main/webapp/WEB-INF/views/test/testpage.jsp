<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp"%>
<%@ page import="net.kear.recipeorganizer.enums.AudioType"%>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>
	
	<div class="container container-white">
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3>Test Page</h3>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-md">
			<div class="col-sm-2">
    			<button id="startConv">Start</button>
    		</div>
    		<!-- <div class="col-sm-2">
    			<button id="stop">Stop</button>
    		</div> -->
		</div>
		<div class="col-sm-12 spacer-vert-md">
			<h4>Audio:</h4>
			<audio class="audioCtl" controls></audio>
		</div>
		<%-- <div class="col-sm-12 spacer-vert-md">
			<form:form role="form" modelAttribute="watsonConversation" method="post">
				<div class="row col-sm-12">
					<label class="control-label">User input:</label>
					<form:input class="form-control" type="text" path="userInput"/>
					<div class="col-sm-12">
						Context: <form:input class="form-control" type="text" path="context"/>
						Entities: <form:input class="form-control" type="text" path="entities"/>
						Intents: <form:input class="form-control" type="text" path="intents"/>
					</div>
					<form:hidden path="context"/>
					<form:hidden path="entities"/>
					<form:hidden path="intents"/>
				</div>
				<div class="row col-sm-12">
					<label class="control-label">Response:</label>
					<div><h5>${watsonResponse}</h5></div>
				</div>
		        <div class="row col-sm-6 text-center spacer-vert-sm">
					<button class="btn btn-primary" type="submit" name="submit">
						<spring:message code="common.submit"></spring:message></button>
        		</div>
			</form:form>
			<div class="col-sm-12 spacer-vert-md">
				<div class="row col-sm-12">
					<label class="control-label">Concat response:</label>
					<div><h5>${concatText}</h5></div>
				</div>
				<div class="row col-sm-12">
					<label class="control-label">Conversation ID:</label>
					<div><h5>${conversationID}</h5></div>
				</div>
				<div class="row col-sm-12">
					<label class="control-label">Context:</label>
					<div><h5>${watsonContext}</h5></div>
				</div>
				<div class="row col-sm-12">
					<label class="control-label">Intents:</label>
					<div><h5>${watsonIntents}</h5></div>
				</div>
				<div class="row col-sm-12">
					<label class="control-label">Entities:</label>
					<div><h5>${watsonEntities}</h5></div>
				</div>
				<div class="row col-sm-12">
					<label class="control-label">Output:</label>
					<div><h5>${watsonOutput}</h5></div>
				</div>
			</div>
		</div> --%>
	</div>

<%@include file="../common/footer.jsp" %>

<script type="text/javascript">

var stream = null;
var keywordsArray = "";

/***********************/
/*** audio functions ***/
/***********************/
function playRecipeAudio(userId, recipeId, section, type) {
	//$('.audioBtn').blur();
	var audio = null;
	audio = $('.audioCtl').get(0);
	var ready = audio.readyState;
	var paused = audio.paused
	if (paused && ready > 0)
		audio.play();
	else {
		audio.setAttribute('src', appContextPath + '/getRecipeAudio?userId=' + userId + '&recipeId=' + recipeId + '&section=' + section + '&type=' + type);
		audio.play();
	}
}

function checkAudio() {
	var audioOn = canPlay();
	if (audioOn) {
		$('.audioBtn').show();
	}
}

/* function retrieveAudio(data) {
	if (data.result != null) {
		playRecipeAudio(1,15,0,data.result);
	}
} */

function retrieveAudio(result) {
	if (result != null) {
		var audio = null;
		audio = $('.audioCtl').get(0);
		var ready = audio.readyState;
		var paused = audio.paused
		//var dto = JSON.stringify(result);
		/* if (paused && ready > 0)
			audio.play();
		else {
			//audio.setAttribute('src', appContextPath + '/getRecipeAudio?data=' + data.result);
			audio.setAttribute('src', appContextPath + '/getAudio?convDto=' + result);
			audio.play();
		} */

		audio.setAttribute('src', appContextPath + '/getAudio?convDto=' + result);
		//audio.play();
	}
	else {
		alert("retrieveAudio: result null");
	}	
}

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

function startConversation() {
	console.log('startConversation');
	//var viewerId = $(viewerId).val();
	//var recipeId = $(recipeId).val();
	var viewerId = 1;
	var recipeId = 15; 
	var data = {"userId":viewerId,"recipeId":recipeId};
	$.ajax({
	    type: 'POST',
	    url: appContextPath + '/startWatsonConversation',
	    //dataType: 'json',
	    dataType: 'blob',
	    //data: data
	})
	.done(function(data) {
		console.log('done data: '+ data);
		var blob = new Blob([jqXHR.response], {type: 'audio/ogg'});
	    var objectUrl = URL.createObjectUrl(blob);
	    audio = $('.audioCtl').get(0);
	    audio.src = objectUrl;
	    // Release resource when it's loaded
	    audio.onload = function(evt) {
	    	URL.revokeObjectUrl(objectUrl);
	    };
	    audio.play();
		//retrieveAudio(data.result);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		alert('Error starting conversation')
	});
}

function startConversation2() {
	console.log('startConversation');
	//var viewerId = $(viewerId).val();
	//var recipeId = $(recipeId).val();
	var viewerId = 1;
	var recipeId = 15;
	var param = {"userId":viewerId,"recipeId":recipeId};
	var data = JSON.stringify(param);
	var url = appContextPath + '/startWatsonConversation';

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	
	var xhr = new XMLHttpRequest();
	xhr.open('GET', url, true);
	//xhr.setRequestHeader('Content-Type', 'application/json');;
	xhr.setRequestHeader(header, token);
	xhr.responseType = 'blob';
	
	xhr.onload = function(e) {
	  if (this.status == 200) {
	    // get binary data as a response
		var blob = new Blob([xhr.response], {type: 'audio/ogg'});
		//var blob = null;
	    //var objectUrl = URL.createObjectUrl(blob);
	    //var objectUrl = window.URL.createObjectUrl(blob);
	    //var objectUrl = (window.URL || window.webkitURL).createObjectUrl(blob);
	    
	    audio = $('.audioCtl').get(0);
	    //audio.src = objectUrl;
	    //audio.src = window.URL.createObjectUrl(blob);
	    var url = window.URL.createObjectUrl(blob);
	    audio.src = url;
	    //audio.mozSrcObject = blob;
	    // Release resource when it's loaded
	    audio.onload = function(evt) {
	    	URL.revokeObjectUrl(objectUrl);
	    };
	    audio.play();
	  }
	};
	
	//xhr.send(data);
	xhr.send();
}

function postResults(results) {
	console.log('postResults: ' + results);
	var speech = JSON.stringify(results);
	//console.log('stringify: ' + data);
	var viewerId = $(viewerId).val();
	var recipeId = $(recipeId).val();
	var data = {"userId":viewerId,"recipeId":recipeId,"message":speech};
	$.ajax({
	    type: 'POST',
	    url: appContextPath + '/postWatsonResult',
	    dataType: 'json',
	    //data: {message:data}
	    data: data
	})
	.done(function(data) {
		console.log('done data: '+ data);
		retrieveAudio(data);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		alert('Error retrieving STT results')
	});
}

function listen(token) {
	console.log('start stt');
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
        //interim_results : false
        keepMicrophone: navigator.userAgent.indexOf('Firefox') > 0
    });
    
	stream.on('error', function(err) {
        console.log(err);
	});

	stream.on('receive-json', function(msg) {
		console.log(msg);
		if (msg.state !== 'listening') {
			if (msg.results[0].final) {
				console.log('receive-json: ' + msg);
				postResults(msg);				
				stream.close;
			}
		}
	});

	stream.setEncoding('utf8');
	
	/* stream.on('data', function(result) {
		if (result.final) { 
			//parseResults(result);
			//postResults(result);
			console.log(result);
			stream.close;
		}
		//console.log(result);
		//stream.close;
		//postResults(result);		
	}); */

	console.log('end stt');
}

$(function() {

	var url = appContextPath + '/getWatsonKeywords';
	$.getJSON(url)
		.done(function (data) {
			console.log("get keywords: " + data);
			keywordsArray = $.makeArray(data);
			console.log("mapped json: " + keywordsArray);
	 	});
	
	$(document)
		.on('click', '#startConv', function(e) {
			startConversation2();
			//getSTTToken();			
		})
		.on('click', '#stop', function(e) {
			if (stream)
				stream.stop();
		})
})

</script>

</body>
</html>

<!-- <script type="text/javascript">

function viewtext() {
	var text = $('#inputTags option:selected').map(function () {
				return $(this).text();}).get();
	alert(text);	
}

$(function() {
	$('#inputTags').selectize({
		maxItems: 5,
	    persist: false,
	    diacritics: true,
	    create: function(input) {
	        return {
	            value: input,
	            text: input
	        }
	    }
	});
})

</script> -->

<!-- <script type="text/javascript">

$(function() {

	$('#play').on('click', function(e) {
		var audio = $('#recipeAudio').get(0);
		var ready = audio.readyState;
		var network = audio.networkState;
		var paused = audio.paused
		if (paused && ready > 0)
			audio.play();
		else {
			audio.setAttribute('src', '/recipeorganizer/test/getAudio?userId=1&recipeId=16&section=0');
		}
	});

	$('#pause').on('click', function(e) {
		var audio = $('#recipeAudio').get(0);
		audio.pause();
	});
})
 
</script>
-->

<!-- <script type="text/javascript">

function hasGetUserMedia() {
	  return !!(navigator.getUserMedia || navigator.webkitGetUserMedia ||
	            navigator.mozGetUserMedia || navigator.msGetUserMedia);
}

function playVoice() {
	var audio = $('#sampleVoice').get(0);
	audio.setAttribute('src', appContextPath + '/getSample?voiceName=en-US_MichaelVoice');
	audio.play();
}


		<div class="col-sm-12 spacer-vert-md">
			<label>Ingredients</label>
			<audio id="sampleVoice"></audio>
		</div>
		
		<div class="col-sm-12 spacer-vert-md">
			<div class="col-sm-2">
    			<button id="play">Play</button>
    		</div>
    		<div class="col-sm-2">
    			<button id="pause">Pause</button>
    		</div>
		</div>

$(function() {

	/* if (hasGetUserMedia()) {
		alert('getUserMedia() works');
	} 
	else {
		alert('getUserMedia() is not supported in your browser');
	} */

	/* navigator.getUserMedia = navigator.getUserMedia ||
    	navigator.webkitGetUserMedia ||
    	navigator.mozGetUserMedia; */

  	/* if (annyang) {
  		  // Let's define our first command. First the text we expect, and then the function it should call
		var commands = {
			'voice': playVoice, 'hello': playVoice			
		};
		
		// Add our commands to annyang
		annyang.addCommands(commands);
		
		// Start listening. You can call this here, or attach this call to an event, button, etc.
		annyang.start();
	} */	
	
})
</script>

</html>
-->

			<%-- <div class="col-sm-12">
				Encrypted text: ${encryptText}
			</div>
			<div class="col-sm-12">
				Decrypted text: ${decryptText}
			</div> --%>

			<%-- <div class="col-sm-12">
				Full Date/Time: ${fullDate}
			</div>
			<div class="col-sm-12">
				Medium Date/Time: ${medDate}
			</div>
			<div class="col-sm-12">
				Short Time: ${shrtDate}
			</div> --%>


			<%-- <div class="col-sm-12">
			<<div class="row">
				<form:form name="approvalForm" role="form" method="post" modelAttribute="recipeMessageDto">
					<div class="form-group">
						<c:forEach var="aaction" items="${approvalActions}">
							<div class="radio-inline">
								<form:radiobutton path="action" value="${aaction}" label="${aaction}"/>								
							</div>
						</c:forEach>
					</div>
					
					<div class="form-group">
						<form:radiobuttons element="div class='radio-inline'" cssStyle="'font-weight:400'" path="action" items="${approvalActions}"/>
					</div>
				
					<div class="form-group">
						<form:select path="action">
							<form:options items="${approvalActions}"/> 
						</form:select>
					</div>
				
				
				</form:form>
			</div>
			</div> --%>


			<%-- <div class="col-sm-12">
			<div class="row">
				<form name="approvalForm" role="form" method="post">
					<div class="form-group">
						<c:forEach var="item" items="${approvalActions}">
							<div class="radio-inline">
								<input type="radio" value="${item}"/>								
							</div>
						</c:forEach>
					</div>
					
					<div class="form-group">
						<form:radiobuttons element="div class='radio-inline'" cssStyle="'font-weight:400'" path="action" items="${approvalActions}"/>
					</div>
				</form>
			</div>
			</div> --%>


	<%-- <div class="col-sm-12">
		<iframe id="iframerpt1" name="iframerpt1" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1463"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1101"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1522"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=741"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1141"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=421"/>"></iframe>
	</div>
	<div class="col-sm-12">
		<iframe id="iframerpt2" name="iframerpt2" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt?uid=3&rid=1462"/>"></iframe>
	</div> --%>

<%--

$F{source.type} == null ? "" : 
($F{source.cookbook} != null ? $F{source.cookbook} : 
($F{source.magazine} != null ? $F{source.magazine} : 
($F{source.newspaper} != null ? $F{source.newspaper} : 
($F{source.other} != null ? $F{source.other} : 
($F{source.person} != null ? $F{source.person} : 
($F{source.websiteUrl} != null ? $F{source.websiteUrl} : 
""))))))

 +
$V{SourcePerson} + $V{SourceOther} + $V{SourceMagazine} + $V{SourceNewspaper}

	<variable name="SourceLabel" class="java.lang.String">
		<variableExpression><![CDATA[$F{source.type} == null ? "" :  $R{report.source} + " "]]></variableExpression>
	</variable>
	<variable name="Source" class="java.lang.String">
		<variableExpression><![CDATA[$F{source.type} == null ? "" : ($F{source.cookbook} != null ? $F{source.cookbook}  : "")]]></variableExpression>
	</variable>

			<printWhenExpression><![CDATA[($F{servings} != null || ($F{prepHours} > 0 || $F{prepMinutes} > 0) || $F{source.type} != null) ? new Boolean(true) : new Boolean(false)]]></printWhenExpression>

			<textField isBlankWhenNull="true">
				<reportElement x="300" y="10" width="250" height="20" uuid="9b0fbcfc-52d6-46fc-853b-7d9440699f56">
					<property name="com.jaspersoft.studio.unit.x" value="pixel"/>
					<property name="com.jaspersoft.studio.unit.y" value="pixel"/>
					<property name="com.jaspersoft.studio.unit.height" value="pixel"/>
					<property name="com.jaspersoft.studio.unit.width" value="pixel"/>
					<printWhenExpression><![CDATA[$F{source.type} != null]]></printWhenExpression>
				</reportElement>
				<textElement markup="styled">
					<font size="14"/>
				</textElement>
				<textFieldExpression><![CDATA["<style isBold=\"true\">" + $V{SourceLabel} + "</style>" + $V{Source}]]></textFieldExpression>
			</textField>

 			<div class="page-header"> 		
				<h3><spring:message code="maintenance.title"></spring:message></h3>
			</div>

			<form:form class="form-horizontal" role="form" method="post" modelAttribute="maintenanceDto">
			<div class="col-sm-12">
				<div class="text-center bold-maroon"><h5>${maintWindow}</h5></div>
		    	<div class="row col-sm-offset-4 spacer-vert-xs">
					<div class="col-sm-4">
						<div class="row">
			            	<label class="control-label" for="emergencyMaintenance"><spring:message code="maintenance.emergency"></spring:message></label>
				        </div>
				        <div class="row">
							<div class="radio-inline">
								<form:radiobutton value="true" path="emergencyMaintenance"/><spring:message code="common.yes"></spring:message>
							</div>
							<div class="radio-inline">
								<form:radiobutton value="false" path="emergencyMaintenance" checked="true"/><spring:message code="common.no"></spring:message>
							</div>
				        </div>
					</div>
					<div class="col-sm-4 <c:if test="${not empty emergencyDurationError}">has-error</c:if>">
				        <div class="row">
			        		<label class="control-label" for="emergencyDuration"><spring:message code="maintenance.emergency.duration"></spring:message></label>
				        </div>
				        <div class="row">
				            <div class="row col-sm-5">				            	
				                <form:input class="form-control" type="text" id="duration" path="emergencyDuration" autocomplete="off"/>
				            </div>
				        </div>
				        <span class="text-danger" style="margin-left:-15px;margin-right:-15px;" id="emergencyDurationErrMsg">${emergencyDurationError}</span>
					</div>
		    	</div>
		    </div>
			<div class="col-sm-12">
		    	<div class="col-sm-offset-3">
		    		<div class="row">
			    		<div class="col-sm-8">
			    			<hr class="hr-black">
			    		</div>
			    	</div>
		    	</div>
		    </div>
			<div class="col-sm-12">
				<div class="row col-sm-offset-4">				
					<div class="row">
						<div class="col-sm-4">
		            		<label class="control-label" for="maintenanceEnabled"><spring:message code="maintenance.enablemaintenance"></spring:message></label>
		            	</div>
						<div class="radio-inline">
							<form:radiobutton value="true" path="maintenanceEnabled"/><spring:message code="common.yes"></spring:message>
						</div>
						<div class="radio-inline">
							<form:radiobutton value="false" path="maintenanceEnabled"/><spring:message code="common.no"></spring:message>
						</div>
					</div>
				</div>
				<div class="row col-sm-offset-4 spacer-vert-xs">
					<div class="row">
						<div class="col-sm-4">
							<div class="row">
								<div class="col-sm-12">
				            		<label class="control-label" for="startTime"><spring:message code="maintenance.startTime"></spring:message></label>
						    	</div>
						        <div class="col-sm-6">
					                <form:input class="form-control" type="text" id="startTime" path="startTime" autocomplete="off"/> 
					            </div>
								<span class="text-danger" style="margin-right:-15px;" id="startTimeErrMsg">${startTimeError}</span>
							</div>
					        <div class="row">
					        	<div class="col-sm-12">
			        				<label class="control-label" for="duration"><spring:message code="maintenance.duration"></spring:message></label>
			        			</div>
					            <div class="col-sm-5">				            	
					                <form:input class="form-control" type="text" id="duration" path="duration" autocomplete="off"/>
					            </div>
						        <span class="text-danger" style="margin-right:-15px;" id="durationErrMsg">${durationError}</span>
					        </div>				        
					    </div>
						<div class="col-sm-6">
							<div class="row">
								<div class="row col-sm-12">
									<label class="control-label"><spring:message code="maintenance.daysOfWeek"></spring:message></label>
								</div>
							</div>
							<div class="row col-sm-12">
								<div class="form-group" id="dayGroup">
									<div class="col-sm-12">
										<form:checkboxes element="div class='col-sm-12 checkbox'" items="${dayMap}" path="daysOfWeek"/>
									</div>
								</div>
							</div>					
						</div>
					</div>
				</div>
				<div class="col-sm-12">				
					<div class="form-group col-sm-12 spacer-vert-sm">
						<div class="col-sm-offset-5 col-sm-2 text-center">
							<button type="submit" class="btn btn-primary" id="btnSubmit" name="btnSubmit"><spring:message code="common.save"></spring:message></button>
						</div>
					</div>
				</div>
			</div>				
			</form:form>


		</div>
	</div>
 --%>


<%--

	<spring:bind path="webGreeting.greeting"><c:set var="greetingError">${status.errorMessage}</c:set></spring:bind>	

	<div class="container container-white"> 
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3>Test</h3>
			</div>
		</div>
		<div class="col-sm-12">
			${text}
		</div>
		<div class="col-sm-12">
			${expired}
		</div>
		<div class="col-sm-12">
			<form:form role="form" modelAttribute="webGreeting" method="post">
				<div>
					<form:input id="greeting" type="text" path="greeting"/>
					<span class="text-danger">${greetingError}</span>
				</div>
		        <div class="form-group col-sm-2 col-sm-offset-5 text-center spacer-vert-sm">
					<button class="btn btn-primary" onclick="viewtext()" type="submit" name="submit">
						<spring:message code="common.submit"></spring:message></button>
        		</div>
			</form:form>
		</div>
        <div class="col-sm-12">
			<div class="row">
				<div class="col-sm-5 <c:if test="${not empty tagsError}">has-error</c:if>">
					<select class="form-control col-sm-5" id="inputTags" multiple>
						<option value="">Enter a tag</option>
						<option value="Ch">Christmas</option>
						<option value="Th">Thanksgiving</option>
						<option value="It">Italian</option>
						<option value="Cn">Chinese</option>
					</select>
					<span class="text-danger">${tagsError}</span>
				</div>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="col-sm-8 col col-sm-offset-2">			
				<div class="center alert text-center strong">
					<div><h4>${message}</h4></div>
				</div>
			</div>
		</div>
	</div>
 --%>
