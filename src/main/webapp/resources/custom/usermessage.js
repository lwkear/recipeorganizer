/******************************/
/*** share recipe functions ***/
/******************************/
//email the recipe popup dialog
function emailRecipe(fromUserId, toUserId, toUserFirstName, toUserLastName, toUserEmail, recipeId, messageId, recipeName) {
	$('#email' + messageId).tooltip("hide");
	$('.recipeName').html(recipeName);
	var msg = getMessage('share.recipient.to.label');
	var fmt = String.format(msg, toUserFirstName, toUserLastName);
	$('#emailToLabel').html(fmt);
	$('#emailMsg').val("");
	$("#submitEmail").one('click', 
			{fromUserId:fromUserId, recipeId:recipeId, recipeName:recipeName, toUserId:toUserId, toUserFirstName:toUserFirstName, toUserLastName:toUserLastName, toUserEmail:toUserEmail},
			postEmail);
	$("#emailRecipeDlg").on('hidden.bs.modal', function(){$("#submitEmail").unbind('click');})
	$("#emailRecipeDlg").modal('show');
}

//request server to update the note
function postEmail(e) {
	console.log('postEmail: viewer=' + e.data.fromUserId + ' recipe='+ e.data.recipeId);

	var userId = e.data.fromUserId;
	var recipeId = e.data.recipeId;
	var recipeName = e.data.recipeName;
	var recipientName = e.data.toUserFirstName + ' ' + e.data.toUserLastName;
	var recipientEmail = e.data.toUserEmail;
	var recipientId = e.data.toUserId;
	var message = $('#emailRecipeMsg').val();
	message = $.trim(message);
	
	var data = {"userId":userId,"recipeId":recipeId,"recipientId":recipientId,"recipientName":recipientName,"recipientEmail":recipientEmail,"emailMsg":message,"recipeName":recipeName};

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: '/recipeorganizer/recipe/shareRecipe',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		$("#emailRecipeDlg").modal('hide');
		console.log('postShare done');
		var msg = getMessage('email.recipe.successful');
		var fmt = String.format(msg, recipientName);
		displayOKMsg(recipeName, fmt);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		$("#emailRecipeDlg").modal('hide');
		postFailed(data.msg);
	});
}

/*******************************/
/*** delete message function ***/
/*******************************/
function deleteMessage(messageId) {
	$('#delete' + messageId).tooltip("hide");
	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/user/deleteMessage',
		dataType: 'json',
		data: {messageId:messageId}
	})
	.done(function(data) {
		console.log('delete message success');
		removeRow(messageId);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//update the datatable
function removeRow(messageId) {
	var table = $('#messageList').DataTable();
	var row = table.row('#' + messageId);
	row.remove();
	table.draw();
}

/***********************************/
/*** send user message functions ***/
/***********************************/
//enter a message in popup dialog
function sendMessage(fromUserId, toUserId, toUserFirstName, toUserLastName, recipeId, msgId, subject) {
	if (msgId > 0)
		$('#respond' + msgId).tooltip("hide");
	else
		$('#userMessage').tooltip("hide");
	if (subject != null)
		$(".subject").html(subject);
	msg = getMessage('usermessage.to');
	msg = '<strong>' + msg + '</strong>&nbsp' + toUserFirstName + '&nbsp' + toUserLastName;
	$("#messageTo").html(msg);
	$('#message').val("");
	$("#submitMessage").one('click', {fromUserId : fromUserId, toUserId : toUserId, recipeId : recipeId, subject : subject}, postMessage);
	$("#userMessageDlg").on('hidden.bs.modal', function(){$("#submitMessage").unbind('click');})
	$("#userMessageDlg").modal('show');
} 

//request server to save the message
function postMessage(e) {
	$("#userMessageDlg").modal('hide');
	console.log('postMessage: from=' + e.data.fromUserId + ' to='+ e.data.toUserId);

	var fromId = e.data.fromUserId;
	var toId = e.data.toUserId;
	var recipeId = e.data.recipeId;
	var subject = e.data.subject;
	var msg = $('#message').val();
	msg = $.trim(msg);
	
	if ((msg == null) || (msg == ''))
		return;

	var data = {"id":0,"fromUserId":fromId,"toUserId":toId,"subject":null,"message":msg,"htmlMessage":null,"viewed":false,"recipeId":recipeId,"dateSent":null}; 

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: '/recipeorganizer/user/sendMessage',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('postMessage done');
		displayOKMsg(subject, getMessage('usermessage.sent'));
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

$(function() {
	
	$('[data-toggle="popover"]').popover({
		trigger: 'hover',
		container: 'body',
		placement: 'left',
		html: true
	});
	
	//Note: in order to truncate the message column the widths must be set manually;
	//also, table-layout is set to 'fixed' in messages.jsp
	$('#messageList').DataTable({
    	columnDefs: [
    	    {orderable: false, targets: [-1,-2,-3]},
    		{visible: false, targets: 0},
    		{width: "20%", targets: 1},
    		{width: "15%", targets: 2},
    		{width: "20%", targets: 3},
    		{width: "33%", targets: 4},
    		{width: "4%", targets: [5,6,7]}
    	],
    	autoWidth: false,
    	responsive : true,
    	order: [1,'desc'],
		language : {
	    	emptyTable:     getMessage('messages.table.emptyTable'),
		    info:           getMessage('messages.table.info'),
		    infoEmpty:      getMessage('messages.table.infoEmpty'),
		    infoFiltered:	getMessage('messages.table.infoFiltered'),
		    lengthMenu:		getMessage('messages.table.lengthMenu'),
		    zeroRecords:	getMessage('messages.table.zeroRecords'),
		    search:			getMessage('common.table.search'),
		    paginate: {
		    	first:      getMessage('common.table.paginate.first'),
		    	last:       getMessage('common.table.paginate.last'),
		    	next:       getMessage('common.table.paginate.next'),
		    	previous:   getMessage('common.table.paginate.previous')
		    }
		},	
		createdRow: function( row, data, dataIndex ) {
			if ( data[0] == 'false') {
				$(row).addClass('active'); 
			}
		},
		//stateSave : false
	});
})
