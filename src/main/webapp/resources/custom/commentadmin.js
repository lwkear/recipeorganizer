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
	displayOKMsg(getMessage('errordlg.title'), error);
}

$(function() {
	
	$('#commentList').DataTable({
    	columnDefs: [{
    		targets: [-1,-2],
    		orderable: false
    	}],
		language : {
	    	emptyTable:     getMessage('commentadmin.table.emptyTable'),
		    info:           getMessage('commentadmin.table.info'),
		    infoEmpty:      getMessage('commentadmin.table.infoEmpty'),
		    infoFiltered:	getMessage('commentadmin.table.infoFiltered'),
		    lengthMenu:		getMessage('commentadmin.table.lengthMenu'),
		    zeroRecords:	getMessage('commentadmin.table.zeroRecords'),
		    search:			getMessage('common.table.search'),
		    paginate: {
		    	first:      getMessage('common.table.paginate.first'),
		    	last:       getMessage('common.table.paginate.last'),
		    	next:       getMessage('common.table.paginate.next'),
		    	previous:   getMessage('common.table.paginate.previous')
		    }
		},	
		stateSave : true
	});
})
