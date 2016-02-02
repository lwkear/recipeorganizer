/*******************************/
/*** delete recipe functions ***/
/*******************************/

//notify the user in popup modal
function deleteRecipe(id, name) {
	$("#messageTitle").text(name);
	$("#messageMsg").text(messageMap.get('recipe.delete.areyousure'));
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', {recipeId : id}, postDeleteRecipe);
	$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});  
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
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);
		postFailed(error);
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

//notify the admin in popup modal
function removeFavorite(userId, recipeId, name) {
	$("#messageTitle").text(name);
	$("#messageMsg").text(messageMap.get('recipe.remove.favorite.areyousure'));
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', {userId : userId, recipeId : recipeId}, postRemoveFavorite);
	$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false})
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
		headers: { 
	        'Accept': 'application/json',
	        'Content-Type': 'application/json' 
	    },
		type: 'POST',
		url: '/recipeorganizer/recipe/removeFavorite',
		dataType: 'json',
		data: JSON.stringify(data)
	})
	.done(function(data) {
		console.log('recipe remove from favorites');
		favoriteRemoved(recipeId);
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail request: '+ jqXHR);
		console.log('fail status: '+ status);
		console.log('fail error: '+ error);
		postFailed(error);
	});
}

//update the datatable
function favoriteRemoved(recipeId) {
	var table = $('#recipeList').DataTable();
	var row = table.row('#' + recipeId);
	row.remove();
	table.draw();
}

function postFailed(error) {
	displayOKMsg(messageMap.get('errordlg.title'), error);
}

$(document).ready(function() {
	$('#recipeList').DataTable({
		language : {
	    	emptyTable:     messageMap.get('recipe.table.emptyTable'),
		    info:           messageMap.get('recipe.table.info'),
		    infoEmpty:      messageMap.get('recipe.table.infoEmpty'),
		    infoFiltered:	messageMap.get('recipe.table.infoFiltered'),
		    lengthMenu:		messageMap.get('recipe.table.lengthMenu'),
		    zeroRecords:	messageMap.get('recipe.table.zeroRecords'),
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
