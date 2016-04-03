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
	$('#madeRight').tooltip("hide");
	$("#submitMadeDate").one('click', {viewerId : viewerId, recipeId : recipeId}, postMadeDate);
	$("#madeDateDlg").on('hidden.bs.modal', function(){$("#submitMadeDate").unbind('click');})
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
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

/************************************/
/*** update recipe note functions ***/
/************************************/
//enter a note in popup dialog
function addNote(recipeNote) {
	$('#noteRight').tooltip("hide");
	$("#submitNote").one('click', {recipeNote: recipeNote}, postNote);
	$("#noteDlg").on('hidden.bs.modal', function(){$("#submitNote").unbind('click');})
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
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

/*******************************/
/*** enter comment functions ***/
/*******************************/
//enter a comment in popup dialog
function addComment(viewerId, recipeId) {
	$('#userComment').val("");
	$('#userCommentErrMsg').html("");
	$('#commentBtn').tooltip("hide");
	$("#submitComment").one('click', {viewerId : viewerId, recipeId : recipeId}, postComment);
	$("#commentDlg").on('hidden.bs.modal', function(){$("#submitComment").unbind('click');})
	$("#commentDlg").modal('show');
}

//request server to update the note
function postComment(e) {
	var isVisible = $('#userCommentErrMsg').is(':visible');
	if (isVisible == true) {
		return false;
	}
	
	$("#commentDlg").modal('hide');
	console.log('postComment: viewer=' + e.data.viewerId + ' recipe='+ e.data.recipeId);

	var viewerId = e.data.viewerId;
	var recipeId = e.data.recipeId;
	var comment = $('#userComment').val();
	comment = $.trim(comment);
	
	if (comment.length === 0)
		return;

	var data = {"id":0,"userId":viewerId,"recipeId":recipeId,"userComment":comment,"dateAdded":null};

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: '/recipeorganizer/recipe/recipeComment',
		dataType: 'html',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('postComment done');
		$('#commentSection').html(data);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

function flagComment(commentId) {
	$('.commentFlag').tooltip("hide");
	var id = commentId;
	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/recipe/flagComment',
		dataType: 'json',
		data : {"commentId" : commentId}
	})
	.done(function(data) {
		console.log('comment flagged');
		$('#flagComment-'+id).hide();
		$('#flagged-'+id).show();
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
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
		console.log('recipe added to favorites');
		$('#favRight').tooltip("hide");
		$('#favRight').hide();
		$('#favLeft').show();
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

/********************************/
/*** remove favorite function ***/
/********************************/
function removeFavorite(viewerId, recipeId) {
	//new entry - format the json for adding it to the database
	var data = {"id":{"userId":viewerId,"recipeId":recipeId},"dateAdded":null};

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: '/recipeorganizer/recipe/removeFavorite',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('recipe removed from favorites');
		$('#favLeft').tooltip("hide");
		$('#favLeft').hide();
		$('#favRight').show();
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

/******************************/
/*** share recipe functions ***/
/******************************/
//email the recipe popup dialog
function shareRecipe(viewerId, recipeId, recipeName) {
	$('#share').tooltip("hide");
	$('#recipientName').val("");
	$('#recipientEmail').val("");
	$('#emailMsg').val("");
	$('#recipientNameErrMsg').html("");
	$('#recipientEmailErrMsg').html("");
	$("#submitShare").one('click', {viewerId : viewerId, recipeId : recipeId, recipeName : recipeName}, postShare);
	$("#shareRecipeDlg").on('hidden.bs.modal', function(){$("#submitShare").unbind('click');})
	$("#shareRecipeDlg").modal('show');
}

//request server to update the note
function postShare(e) {
	var isVisible = $('#recipientNameErrMsg').is(':visible');
	if (isVisible == false) {
		isVisible = $('#recipientEmailErrMsg').is(':visible');
	}
	if (isVisible == true)
		return false;
	
	$("#shareRecipeDlg").modal('hide');
	console.log('postShare: viewer=' + e.data.viewerId + ' recipe='+ e.data.recipeId);

	var viewerId = e.data.viewerId;
	var recipeId = e.data.recipeId;
	var recipeName = e.data.recipeName;
	var recipientName = $('#recipientName').val();
	var recipientEmail = $('#recipientEmail').val();
	var message = $('#emailMsg').val();
	message = $.trim(message);
	
	if ((recipientName.length === 0) || (recipientEmail.length === 0)) 
		return;

	var data = {"userId":viewerId,"recipeId":recipeId,"recipientId":0,"recipientName":recipientName,"recipientEmail":recipientEmail,"emailMsg":message,"recipeName":recipeName};

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: '/recipeorganizer/recipe/shareRecipe',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('postShare done');
		var msg = getMessage('email.recipe.successful');
		var fmt = String.format(msg, recipientName);
		displayOKMsg(recipeName, fmt);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

function getTitle() {
	var title = $("#popoverTitle").html();
	return title;
}

function getContent() {
	var content = $("#popoverContent").html();
	return content;
}

$(function() {

	convertFractions('.ingredqty');	
	setIcons();
	fixTags();
	
	$('#submittedBy').popover({
		trigger: 'hover',
		placement: 'right',
		html: true,
		title: getTitle(),
		content: getContent()
	});
	
	$.datepicker.setDefaults({
		dateFormat: "mm/dd/yy",
		defaultDate: null,
	});
	$('#madeDate').datepicker();

	$(document)
		.on('click', '#htmlPrint', function(e) {
			//a jasper report contains tables; if none are found then the report did not load
			var len = $("#iframerpt").contents().find("body > table").length;
			if (len > 0)
				document.getElementById("iframerpt").contentWindow.print();
			else
				postFailed(getMessage('exception.JRException'));
		})
})
