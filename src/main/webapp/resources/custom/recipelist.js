/*******************************/
/*** delete recipe functions ***/
/*******************************/

//notify the user in popup modal
function deleteRecipe(id, name) {
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
		url: '/recipeorganizer/recipe/deleteRecipe',
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
	$("#messageTitle").text(name);
	$("#messageMsg").text(getMessage('recipe.remove.favorite.areyousure'));
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', {userId : userId, recipeId : recipeId}, postRemoveFavorite);
	$('#messageDlg').modal({backdrop: 'static', keyboard: true, show: false});
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
		/*headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	    },*/
		type: 'POST',
		url: '/recipeorganizer/recipe/removeFavorite',
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

/*******************************/
/*** approve recipe function ***/
/*******************************/
function approveRecipe(recipeId) {
	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/admin/approveRecipe',
		dataType: 'json',
		data : {recipeId:recipeId}
	})
	.done(function(data) {
		console.log('recipe approved');
		removeRow(recipeId);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

function postFailed(error) {
	displayOKMsg(getMessage('errordlg.title'), error);
}

$(document).ready(function() {
	$('#recipeList').DataTable({
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
})
