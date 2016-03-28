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
//enter a note in popup dialog
function sendMessage(fromUserId, toUserId, toUserFirstName, toUserLastName, recipeId, msgId) {
	if (msgId > 0)
		$('#respond' + msgId).tooltip("hide");
	else
		$('#userMessage').tooltip("hide");	
	msg = getMessage('usermessage.to');
	msg = '<strong>' + msg + '</strong>&nbsp' + toUserFirstName + '&nbsp' + toUserLastName;
	$("#messageTo").html(msg);
	$('#message').val("");
	$("#submitMessage").one('click', {fromUserId : fromUserId, toUserId : toUserId, recipeId : recipeId}, postMessage);
	$("#userMessageDlg").on('hidden.bs.modal', function(){$("#submitMessage").unbind('click');})
	$("#userMessageDlg").modal('show');
} 

//request server to update the note
function postMessage(e) {
	$("#userMessageDlg").modal('hide');
	console.log('postMessage: from=' + e.data.fromUserId + ' to='+ e.data.toUserId);

	var fromId = e.data.fromUserId;
	var toId = e.data.toUserId;
	var recipeId = e.data.recipeId;
	var msg = $('#message').val();
	msg = $.trim(msg);
	
	if ((msg == null) || (msg == ''))
		return;

	var data = {"id":0,"fromUserId":fromId,"toUserId":toId,"message":msg,"viewed":false,"recipeId":recipeId,"dateSent":null}; 

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: '/recipeorganizer/user/sendMessage',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('postMessage done');
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

$(function() {
	
	$('#messageList').DataTable({
    	columnDefs: [
    	    {orderable: false, targets: [-1,-2,-3]},
    		{visible: false, targets: 0},
    		{width: '90px', targets: 1}
    	],
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
		}
	});
})
