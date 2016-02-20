/********************************/
/*** delete comment functions ***/
/********************************/
function deleteComment(commentId) {
	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/admin/deleteComment',
		dataType: 'json',
		data: {commentId:commentId}
	})
	.done(function(data) {
		console.log('delete comment success');
		removeRow(commentId);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}


/********************************/
/*** unflag comment functions ***/
/********************************/
function removeCommentFlag(commentId) {
	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/admin/removeCommentFlag',
		dataType: 'json',
		data : {commentId:commentId}
	})
	.done(function(data) {
		console.log('comment unflagged');
		removeRow(commentId);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//update the datatable
function removeRow(commentId) {
	var table = $('#commentList').DataTable();
	var row = table.row('#' + commentId);
	row.remove();
	table.draw();
}

function postFailed(error) {
	displayOKMsg(messageMap.get('errordlg.title'), error);
}

$(function() {
	
	$('#commentList').DataTable({
    	columnDefs: [{
    		targets: [-1,-2],
    		orderable: false
    	}],
		language : {
	    	emptyTable:     messageMap.get('commentadmin.table.emptyTable'),
		    info:           messageMap.get('commentadmin.table.info'),
		    infoEmpty:      messageMap.get('commentadmin.table.infoEmpty'),
		    infoFiltered:	messageMap.get('commentadmin.table.infoFiltered'),
		    lengthMenu:		messageMap.get('commentadmin.table.lengthMenu'),
		    zeroRecords:	messageMap.get('commentadmin.table.zeroRecords'),
		    search:			messageMap.get('common.table.search'),
		    paginate: {
		    	first:      messageMap.get('common.table.paginate.first'),
		    	last:       messageMap.get('common.table.paginate.last'),
		    	next:       messageMap.get('common.table.paginate.next'),
		    	previous:   messageMap.get('common.table.paginate.previous')
		    }
		},	
		stateSave : true
	});
})
