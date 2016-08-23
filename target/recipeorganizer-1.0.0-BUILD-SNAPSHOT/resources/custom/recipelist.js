/*******************************/
/*** delete recipe functions ***/
/*******************************/
//notify the user in popup modal
function deleteRecipe(id, name) {
	$('#delete' + id).tooltip("hide");
	$("#messageTitle").text(name);
	$("#messageMsg").text(getMessage('recipe.delete.areyousure'));
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', {recipeId : id}, postDeleteRecipe);
	$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
	$("#messageDlg").on('hidden.bs.modal', function(){$("#yesBtn").unbind('click');})
	$("#messageDlg").modal('show');
} 

//request server to delete the recipe
function postDeleteRecipe(e) {
	$("#messageDlg").modal('hide');
	console.log('delete recipe: ' + e.data.recipeId);
	
	var recipeId = e.data.recipeId;
	
	$.ajax({
		type: 'POST',
		url: appContextPath + '/recipe/deleteRecipe',
		dataType: 'json',
		data: {"recipeId":recipeId}
	})
	.done(function(data) {
		console.log('delete recipe success');
		recipeDeleted(recipeId);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//update the datatable
function recipeDeleted(recipeId) {
	var table = $('#recipeList').DataTable();
	var row = table.row('#' + recipeId);
	row.remove();
	table.draw();
}

/*********************************/
/*** remove favorite functions ***/
/*********************************/
//notify the user in popup modal
function removeFavorite(userId, recipeId, name) {
	$('#fav' + recipeId).tooltip("hide");
	$("#messageTitle").text(name);
	$("#messageMsg").text(getMessage('recipe.remove.favorite.areyousure'));
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', {userId : userId, recipeId : recipeId}, postRemoveFavorite);
	$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
	$("#messageDlg").on('hidden.bs.modal', function(){$("#yesBtn").unbind('click');})
	$("#messageDlg").modal('show');
} 

//request server to remove the favorite
function postRemoveFavorite(e) {
	$("#messageDlg").modal('hide');
	console.log('remove favorite: user=' + e.data.userId + ' recipe='+ e.data.recipeId);
	
	var userId = e.data.userId;
	var recipeId = e.data.recipeId;
	var data = {"id":{"userId":userId,"recipeId":recipeId},"dateAdded":null};
	
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: appContextPath + '/recipe/removeFavorite',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('recipe remove from favorites');
		removeRow(recipeId);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//update the datatable
function removeRow(recipeId) {
	var table = $('#recipeList').DataTable();
	var row = table.row('#' + recipeId);
	row.remove();
	table.draw();
}

function updateRow(recipeId, action) {
	var table = $('#recipeList').DataTable();
	var ndx = table.row('#' + recipeId).index();
	var label;
	if (action == 'BLOCK')
		label = $('#blockedStatus').val();
	if (action == 'PENDING')
		label = $('#pendingStatus').val();
	table.cell(ndx,6).data(label).draw();
}

/*******************************/
/*** approve recipe function ***/
/*******************************/
//enter a note in popup dialog
function recipeAction(toUserId, recipeId, name) {
	$('#action' + recipeId).tooltip("hide");
	$(".recipeName").text(name);
	$("#action").prop('selectedIndex', 0);
	$('#message').val("");
	$('#reasons').multiselect('deselectAll', false);
	$('#reasons').multiselect('updateButtonText');
	$('#reasons').multiselect('disable');
	$("#submitActionMessage").one('click', {toUserId : toUserId, recipeId : recipeId, name : name}, postActionMessage);
	$('#recipeActionDlg').modal({backdrop: 'static', keyboard: false, show: false});
	$('#recipeActionDlg').on('shown.bs.modal', function () {$(this).find('#action').focus()})
	$("#recipeActionDlg").on('hidden.bs.modal', function(){$("#submitActionMessage").unbind('click');})
	$("#recipeActionDlg").modal('show');
} 

//request server to update the note
function postActionMessage(e) {
	$("#recipeActionDlg").modal('hide');

	var toId = e.data.toUserId;
	var recipeId = e.data.recipeId;
	var recipeName = e.data.name;
	var action = $("#action").val();
	var reasons = $('#reasons').val();
	var msg = $('#message').val();
	msg = $.trim(msg);
	
	var data = {"toUserId":toId,"recipeId":recipeId,"recipeName":recipeName,"action":action,"reasons":reasons,"message":msg};

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: appContextPath + '/admin/approveRecipe',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('postMessage done');
		if (action == 'APPROVE' || action == 'DELETE')
			removeRow(recipeId);
		else
			updateRow(recipeId, action);
		displayOKMsg(recipeName, getMessage('usermessage.sent'));
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

$(function() {
	$('#reasons').multiselect({
		nonSelectedText: getMessage('common.none'),
		numberDisplayed: 2,
		disabledText: ''
	});
	
	$('#recipeList').DataTable({
		searching: false,
		lengthChange: false,
		responsive : true,
		language : {
	    	emptyTable:     getMessage('recipe.table.emptyTable'),
		    info:           getMessage('recipe.table.info'),
		    infoEmpty:      getMessage('recipe.table.infoEmpty'),
		    infoFiltered:	getMessage('recipe.table.infoFiltered'),
		    lengthMenu:		getMessage('recipe.table.lengthMenu'),
		    zeroRecords:	getMessage('recipe.table.zeroRecords'),
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
	
	$(document)
		.on ('change', '#action', function(e) {
			var option = $(this).prop('selectedIndex');
			if (option == 0) {
				$('#reasons').multiselect('disable');
				$('#reasons').multiselect('deselectAll', false);
				$('#reasons').multiselect('updateButtonText');
			}
			else
				$('#reasons').multiselect('enable');
		})
})
