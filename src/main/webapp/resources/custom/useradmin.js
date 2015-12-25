var uId = "";
var firstName = "";
var lastName = "";
var userObj = null;

/*** delete user functions ***/

//call function to request recipe count for the user from the server
function checkRecipeCount(userId, userFirst, userLast) {
	uId = userId;
	firstName = userFirst;
	lastName = userLast;
	getRecipeCount(userId, displayCount);
}

//return value from server passed to callback function, displayCount
function getRecipeCount(userId, callback) {
	$.ajax({
		type: 'GET',
		url: '/recipeorganizer/recipe/getRecipeCount',
		dataType: 'json',
		data: {"userId":userId}
	})
	.done(function(data) {
		console.log('checkRecipeCount() done');
		console.log('data:' + data);
		callback(data);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
	});
}

//notify the admin in popup modal
function displayCount(count) {
	$("#recipeCount").text(count);
	$(".userName").text(firstName + " " + lastName);
	$("#noRecipes").hide();
	$("#hasRecipes").hide();
	if (count == 0)
		$("#noRecipes").show();
	else
		$("#hasRecipes").show();
	$("#deleteUser").modal();
} 

//request server to delete the user
function deleteUser() {
	console.log('delete user: ' + uId);

	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/admin/deleteUser',
		dataType: 'json',
		data: {"userId":uId}
	})
	.done(function(data) {
		console.log('delete user success');
		userDeleted();
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
		deleteFailed(error);
	});
}

//update the datatable
function userDeleted() {
	var table = $('#userList').DataTable();
	var row = table.row('#' + uId);
	row.remove();
	table.draw();	
}

function deleteFailed(error) {
	$("#errorMsg").text(error);
	$("#errorDlg").modal();		
}

/*** update user functions ***/

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
	});
}

//modify the user in popup dialog
function displayUser(user) {
	userObj = user;
	$("#inputRole option[data-id='" + user.role.id + "']").prop('selected',true);
	$("input[name='enabled'][value='" + user.enabled + "']").prop('checked',true);
	$("input[name='locked'][value='" + user.locked + "']").prop('checked',true);
	$(".userName2").text(user.firstName + " " + user.lastName);
	$("#updateUser").modal();
} 

//request server to update the user
function postUser() {
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
	table.cell(ndx,5).data(user.enabled ? yesText : noText);
	table.cell(ndx,6).data(user.locked ? yesText : noText);
	table.cell(ndx,7).data(user.role.name).draw();
};

$(document).ready(function() {
	$('#userList').DataTable({
    	columnDefs: [{
    	      targets: [-1,-2],
    	      orderable: false
    	    }]		
	});

})
