//var userObj = null;

/*****************************/
/*** delete user functions ***/
/*****************************/

//call function to request recipe count for the user from the server
function checkRecipeCount(userId, userFirst, userLast) {
	uId = userId;
	firstName = userFirst;
	lastName = userLast;
	getRecipeCount(userId, userFirst, userLast, displayCount);
}

//return value from server passed to callback function, displayCount
function getRecipeCount(userId, userFirst, userLast, callback) {
	$.ajax({
		type: 'GET',
		url: '/recipeorganizer/recipe/getRecipeCount',
		dataType: 'json',
		data: {"userId":userId}
	})
	.done(function(data) {
		console.log('checkRecipeCount() done');
		console.log('data:' + data);
		callback(data, userId, userFirst, userLast);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
	});
}

//notify the admin in popup modal
function displayCount(count, userId, userFirst, userLast) {
	$("#messageTitle").text(messageMap.get('useradmin.delete.title'));
	var msg;
	if (count == 0)
		msg = messageMap.get('useradmin.delete.areyousure1') + " " + userFirst + " " + userLast + "?";
	else
		msg = userFirst + " " + userLast + " " + messageMap.get('useradmin.delete.hasrecipe1') + " " + count + " " +
				messageMap.get('useradmin.delete.hasrecipe2') +  " " + messageMap.get('useradmin.delete.areyousure2');
	$("#messageMsg").text(msg);
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', {id : userId}, deleteUser);
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
		//data: {"userId":uId}
		data: {"userId":userId}
	})
	.done(function(data) {
		console.log('delete user success');
		userDeleted(userId);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
		postFailed(error);
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
	uId = userId;	
	getUser(userId, displayUser);	
}

//return object from server passed to callback function, displayUser
function getUser(userId, callback) {
	console.log('getUser: ' + userId);
	
	$.ajax({
		type: 'GET',
		url: '/recipeorganizer/admin/getUser/' + userId,
		dataType: 'json'
	})
	.done(function(data) {
		console.log('getUser() done');
		console.log('data:' + data);
		callback(data);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
		postFailed(error);
	});
}

//modify the user in popup dialog
function displayUser(user) {
	//userObj = user;
	$("#inputRole option[data-id='" + user.role.id + "']").prop('selected',true);
	$("input[name='enabled'][value='" + user.enabled + "']").prop('checked',true);
	$("input[name='locked'][value='" + user.locked + "']").prop('checked',true);
	$(".userName").text(user.firstName + " " + user.lastName);
	$("#submit").one('click', {user : user}, postUser);
	$("#updateUser").modal('show');
} 

//request server to update the user
function postUser(e) {
	$("#updateUser").modal('hide');
	userObj = e.data.user;
	userObj.role.id = $("#inputRole").find(':selected').data('id');
	userObj.role.name = $("#inputRole").find(':selected').val(); 
	userObj.enabled = parseInt($("input[name='enabled']:checked").val()); 
	userObj.locked = parseInt($("input[name='locked']:checked").val());
	var user = userObj;

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
		userUpdate(userObj);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
	});
}

//update the datatable
function userUpdate(user) {
	var table = $('#userList').DataTable();
	var ndx = table.row('#' + user.id).index();
	table.cell(ndx,5).data(user.enabled ? messageMap.get('common.yes') : messageMap.get('common.no'));
	table.cell(ndx,6).data(user.locked ? messageMap.get('common.yes') : messageMap.get('common.no'));
	table.cell(ndx,7).data(user.role.name).draw();
};

function postFailed(error) {
	$("#messageTitle").text(messageMap.get('errordlg.title'));
	$("#messageMsg").text(error);
	$(".msgDlgBtn").hide();
	$("#okBtn").show();
	$("#messageDlg").modal();
}

$(document).ready(function() {
	$('#userList').DataTable({
    	columnDefs: [{
    		targets: [-1,-2],
    		orderable: false
    	}],
		language : {
	    	emptyTable:     messageMap.get('user.table.emptyTable'),
		    info:           messageMap.get('user.table.info'),
		    infoEmpty:      messageMap.get('user.table.infoEmpty'),
		    infoFiltered:	messageMap.get('user.table.infoFiltered'),
		    lengthMenu:		messageMap.get('user.table.lengthMenu'),
		    zeroRecords:	messageMap.get('user.table.zeroRecords'),
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
