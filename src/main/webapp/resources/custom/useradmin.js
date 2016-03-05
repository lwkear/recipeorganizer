/*****************************/
/*** delete user functions ***/
/*****************************/

//call function to request recipe count for the user from the server
function checkRecipeCount(userId, userFirst, userLast) {
	getRecipeCount(userId, userFirst, userLast, displayCount);
}

//return value from server passed to callback function, displayCount
function getRecipeCount(userId, userFirst, userLast, callback) {
	$.ajax({
		type: 'GET',
		url: '/recipeorganizer/recipe/userRecipeCount',
		dataType: 'json',
		data: {"userId":userId}
	})
	.done(function(data) {
		console.log('checkRecipeCount() done');
		console.log('data:' + data);
		callback(data, userId, userFirst, userLast);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//notify the admin in popup modal
function displayCount(count, userId, userFirst, userLast) {
	$("#messageTitle").text(userFirst + " " + userLast);
	var msg;
	if (count == 0)
		msg = getMessage('useradmin.delete.areyousure');
	else
		msg = userFirst + " " + userLast + " " + getMessage('useradmin.delete.hasrecipe1') + " " + count + " " +
				getMessage('useradmin.delete.hasrecipe2') +  " " + getMessage('useradmin.delete.areyousure');
	$("#messageMsg").text(msg);
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', {id : userId}, deleteUser);
	$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
	$("#messageDlg").on('hidden.bs.modal', function(){$("#yesBtn").unbind('click');})
	$("#messageDlg").modal('show');
} 

//request server to delete the user
function deleteUser(e) {
	$("#messageDlg").modal('hide');
	console.log('delete user: ' + e.data.id);	
	
	var userId = e.data.id;
	
	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/admin/deleteUser',
		dataType: 'json',
		data: {"userId":userId}
	})
	.done(function(data) {
		console.log('delete user success');
		userDeleted(userId);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//update the datatable
function userDeleted(userId) {
	var table = $('#userList').DataTable();
	var row = table.row('#' + userId);
	row.remove();
	table.draw();	
}

/*****************************/
/*** update user functions ***/
/*****************************/

//call function to request user object from server
function updateUser(userId) {
	getUser(userId, displayUser);	
}

//return object from server passed to callback function, displayUser
function getUser(userId, callback) {
	console.log('getUser: ' + userId);
	
	$.ajax({
		type: 'GET',
		url: '/recipeorganizer/admin/getUser',
		dataType: 'json',
		data: {"userId":userId}
	})
	.done(function(data) {
		console.log('getUser() done');
		console.log('data:' + data);
		callback(data);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//modify the user in popup dialog
function displayUser(user) {
	$("#inputRole option[data-id='" + user.role.id + "']").prop('selected',true);
	$("input[name='enabled'][value='" + user.enabled + "']").prop('checked',true);
	$("input[name='locked'][value='" + user.locked + "']").prop('checked',true);
	$("input[name='pswdExpired'][value='" + user.passwordExpired + "']").prop('checked',true);
	$("input[name='acctExpired'][value='" + user.accountExpired + "']").prop('checked',true);
	$(".userName").text(user.firstName + " " + user.lastName);
	$("#submit").one('click', {user : user}, postUser);
	$('#updateUser').modal({backdrop: 'static', keyboard: false, show: false});
	$("#updateUser").on('hidden.bs.modal', function(){$("#submit").unbind('click');})
	$("#updateUser").modal('show');
} 

//request server to update the user
function postUser(e) {
	$("#updateUser").modal('hide');
	var user = e.data.user;
	user.role.id = $("#inputRole").find(':selected').data('id');
	user.role.name = $("#inputRole").find(':selected').val(); 
	user.enabled = parseInt($("input[name='enabled']:checked").val()); 
	user.locked = parseInt($("input[name='locked']:checked").val());
	user.passwordExpired = parseInt($("input[name='pswdExpired']:checked").val()); 
	user.accountExpired = parseInt($("input[name='acctExpired']:checked").val());

	$.ajax({
	    type: 'POST',
		url: '/recipeorganizer/admin/updateUser',
		dataType: 'json',
		data: JSON.stringify(user),
		contentType: 'application/json'
	})
	.done(function(data) {
		console.log('getUser() done');
		console.log('data:' + data);
		userUpdate(user);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//update the datatable
function userUpdate(user) {
	var table = $('#userList').DataTable();
	var ndx = table.row('#' + user.id).index();
	table.cell(ndx,5).data(user.enabled ? getMessage('common.yes') : getMessage('common.no'));
	table.cell(ndx,6).data(user.locked ? getMessage('common.yes') : getMessage('common.no'));
	table.cell(ndx,7).data(user.passwordExpired ? getMessage('common.yes') : getMessage('common.no'));
	table.cell(ndx,8).data(user.accountExpired ? getMessage('common.yes') : getMessage('common.no'));
	table.cell(ndx,9).data(user.role.name).draw();
};

function postFailed(error) {
	displayOKMsg(getMessage('errordlg.title'), error);
}

$(function() {
	
	$('#userList').DataTable({
    	columnDefs: [{
    		targets: [-1,-2],
    		orderable: false
    	}],
		language : {
	    	emptyTable:     getMessage('useradmin.table.emptyTable'),
		    info:           getMessage('useradmin.table.info'),
		    infoEmpty:      getMessage('useradmin.table.infoEmpty'),
		    infoFiltered:	getMessage('useradmin.table.infoFiltered'),
		    lengthMenu:		getMessage('useradmin.table.lengthMenu'),
		    zeroRecords:	getMessage('useradmin.table.zeroRecords'),
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
