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

function setIcons() {
	var fav = $('#isFav').val();
	var madeCount = $('#displayCount').text();
	var note = $('#inputNote').val();
	
	if (fav === 'true') {
		$('#favLeft').show();
		$('#favRight').hide();
	}
	if (madeCount > 0) {
		$('#madeLeft').show();
		$('#madeRight').hide();
	}
	if (note.length > 0) {
		$('#noteLeft').show();
		$('#noteRight').hide();
	}
}

function toggleComments() {
    $("#commentDiv").toggleClass('hidden');
}

/**********************************/
/*** update made date functions ***/
/**********************************/
//set the last made date in popup dialog
function selectMadeDate(viewerId, recipeId) {
	$("#submitMadeDate").one('click', {viewerId : viewerId, recipeId : recipeId}, postMadeDate);
	$("#madeDateDlg").modal('show');
} 

//request server to update the madeDate
function postMadeDate(e) {
	$("#madeDateDlg").modal('hide');
	console.log('postMadeDate: viewer=' + e.data.viewerId + ' recipe='+ e.data.recipeId);
	
	var viewerId = e.data.viewerId;
	var recipeId = e.data.recipeId;
	var count = $('#displayCount').text();
	count = $.trim(count);
	var madeCount = 1;
	if (count.length > 0) { 
		madeCount = parseInt(count);
		madeCount += 1;
	}
	var madeDate = $('#madeDate').datepicker("getDate");
	var data = {"id":{"userId":viewerId,"recipeId":recipeId},"lastMade":madeDate,"madeCount":madeCount};
	console.log('date: ' + madeDate);

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: '/recipeorganizer/recipe/recipeMade',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('postMadeDate done');
		$('#madeLeft').show();
		$('#madeRight').hide();
		$("#madeLeft").prop("disabled", true);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
		postFailed(error);
	});
}

/************************************/
/*** update recipe note functions ***/
/************************************/
//enter a note in popup dialog
function addNote(recipeNote) {
	$("#submitNote").one('click', {recipeNote: recipeNote}, postNote);
	$("#noteDlg").modal('show');
} 

//request server to update the note
function postNote(e) {
	$("#noteDlg").modal('hide');
	console.log('postNote: viewer=' + e.data.recipeNote.id.userId + ' recipe='+ e.data.recipeNote.id.recipeId);

	var viewerId = e.data.recipeNote.id.userId;
	var recipeId = e.data.recipeNote.id.recipeId;	
	var note = $('#inputNote').val();
	note = $.trim(note);

	var data = {"id":{"userId":viewerId,"recipeId":recipeId},"note":note};

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: '/recipeorganizer/recipe/recipeNote',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		$('#noteLeft').show();
		$('#noteRight').hide();
		console.log('postNote done');
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
		postFailed(error);
	});
}

/*******************************/
/*** enter comment functions ***/
/*******************************/
//enter a comment in popup dialog
function addComment(viewerId, recipeId) {
	$("#submitComment").one('click', {viewerId : viewerId, recipeId : recipeId}, postComment);
	$("#commentDlg").modal('show');
} 

//request server to update the note
function postComment(e) {
	$("#commentDlg").modal('hide');
	console.log('postComment: viewer=' + e.data.viewerId + ' recipe='+ e.data.recipeId);

	var viewerId = e.data.viewerId;
	var recipeId = e.data.recipeId;
	var comment = $('#inputComment').val();
	note = $.trim(comment);

	var data = {"id":0,"userId":viewerId,"recipeId":recipeId,"userComment":comment,"dateAdded":null};

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: '/recipeorganizer/recipe/recipeComment',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('postComment done');
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
		postFailed(error);
	});
}

function flagComment(commentId) {
	var id = commentId;
	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/recipe/flagComment',
		dataType: 'json',
		data : {id : commentId}
	})
	.done(function(data) {
		//TODO: AJAX: probably need to check for a success status?
		console.log('comment flagged');
		$('#flagComment-'+id).hide();
		$('#flagged-'+id).show();
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail request: '+ jqXHR);
		console.log('fail status: '+ status);
		console.log('fail error: '+ error);

		//server currently returns a simple error message
		var respText = jqXHR.responseText;
		console.log('respText: '+ respText);
		postFailed(respText)
	});
}

/*****************************/
/*** add favorite function ***/
/*****************************/
function addFavorite(viewerId, recipeId) {
	//new entry - format the json for adding it to the database
	var data = {"id":{"userId":viewerId,"recipeId":recipeId},"dateAdded":null};

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: '/recipeorganizer/recipe/addFavorite',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		//TODO: AJAX: probably need to check for a success status?
		console.log('recipe added to favorites');
		$('#favLeft').show();
		$('#favRight').hide();
		$("#favLeft").prop("disabled", true);		
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail request: '+ jqXHR);
		console.log('fail status: '+ status);
		console.log('fail error: '+ error);

		//server currently returns a simple error message
		var respText = jqXHR.responseText;
		console.log('respText: '+ respText);
		postFailed(respText)
	});
}

function postFailed(error) {
	displayOKMsg(messageMap.get('errordlg.title'), error);
}

$(function() {

	convertFractions('.ingredqty');	

	setIcons();
	
	$.datepicker.setDefaults({
		dateFormat: "mm/dd/yy",
		defaultDate: null,
	});
	
	$('[data-toggle="tooltip"]').tooltip();
	
	$('#madeDate').datepicker();

	$(document)
		.on('click', '#htmlPrint', function(e) {
			//a jasper report contains tables; if none are found then the report did not load
			var len = $("#iframerpt").contents().find("body > table").length;
			if (len > 0)
				document.getElementById("iframerpt").contentWindow.print();
			else
				postFailed(messageMap.get('exception.JRException'));
		})
})
