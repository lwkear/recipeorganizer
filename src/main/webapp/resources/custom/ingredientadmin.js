function ingredDatumToken(datum) {
	return Bloodhound.tokenizers.whitespace(datum.name);
};

function ingredPrefetchFilter(data) {
	return $.map(data.ingredients, function (ingredient) {
        return {
			id : ingredient.id,
            name : ingredient.name
        };
    });
};

function ingredRemoteFilter(data) {
	return $.map(data, function (data) {
        return {
			id : data.id,
            name : data.name
        };
    });
};

var remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getIngredients?searchStr=%QUERY', ingredRemoteFilter);
var bhOpts = setBHOptions(20, ingredDatumToken, null, null, remoteOpts);
var ingredBH = new Bloodhound(bhOpts);

function initIngredientsTA() {
	var options = initTypeaheadOptions(true,true,2);
	var dataset = initTypeaheadDataset('ingredients', 'name', 20, ingredBH);
	$('#nameTA').typeahead(options,dataset);
};

/**********************************/
/*** delete ingredient function ***/
/**********************************/
function deleteIngredient(ingredId, ingredUsage, ingredName) {
	$('#delete' + ingredId).tooltip("hide");
	if (ingredUsage > 0) {
		displayOKMsg(ingredName, getMessage('exception.ingredient.cannotdelete'));
		return;
	}	
	
	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/admin/deleteIngredient',
		dataType: 'json',
		data: {ingredId:ingredId}
	})
	.done(function(data) {
		console.log('delete ingredient success');
		removeRow(ingredId);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

/***********************************/
/*** approve ingredient function ***/
/***********************************/
function approveIngredient(ingredId) {
	$('#approve' + ingredId).tooltip("hide");
	$.ajax({
		type: 'POST',
		url: '/recipeorganizer/admin/approveIngredient',
		dataType: 'json',
		data : {ingredId:ingredId}
	})
	.done(function(data) {
		console.log('ingredient approved');
		removeRow(ingredId);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

/***********************************/
/*** update ingredient functions ***/
/***********************************/
//call function to request Ingredient object from server
function updateIngredient(ingredId) {
	$('#update' + ingredId).tooltip("hide");
	getIngredient(ingredId, displayIngredient);	
}

//return object from server passed to callback function, displayIngredient
function getIngredient(ingredId, callback) {
	console.log('getIngredient: ' + ingredId);
	
	$.ajax({
		type: 'GET',
		url: '/recipeorganizer/admin/getIngredient',
		dataType: 'json',
		data: {"ingredId":ingredId}
	})
	.done(function(data) {
		console.log('getIngredient() done');
		console.log('data:' + data);
		callback(data);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//modify the ingredient in popup dialog
function displayIngredient(ingredient) {
	$("#name").val(ingredient.name);
	$(".updateName").text(ingredient.name);
	$("#submitUpdate").one('click', {ingredient : ingredient}, postUpdateIngredient);
	$('#updateIngredient').modal({backdrop: 'static', keyboard: false, show: false});
	$("#updateIngredient").on('hidden.bs.modal', function(){$("#submit").unbind('click');})
	$("#updateIngredient").modal('show');
} 

//request server to update the user
function postUpdateIngredient(e) {
	$("#updateIngredient").modal('hide');
	var ingredient = e.data.ingredient;
	ingredient.name = $("#name").val();

	$.ajax({
	    type: 'POST',
		url: '/recipeorganizer/admin/updateIngredient',
		dataType: 'json',
		data: JSON.stringify(ingredient),
		contentType: 'application/json'
	})
	.done(function(data) {
		console.log('updateIngredient done');
		console.log('data:' + data);
		ingredientUpdate(data);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

/************************************/
/*** replace ingredient functions ***/
/************************************/
//replace the ingredient in popup dialog
function replaceIngredient(ingredId, ingredName) {
	$('#replace' + ingredId).tooltip("hide");
	$(".replaceName").text(ingredName);
	$("#nameTA").typeahead('val',"");
	$('#ingredTAId').val(0);
	$('#ingredTAName').val("");
	$("#submitReplace").one('click', {ingredId: ingredId, ingredName : ingredName}, postReplaceIngredient);
	$('#replaceIngredient').modal({backdrop: 'static', keyboard: false, show: false});
	$("#replaceIngredient").on('hidden.bs.modal', function(){$("#submitReplace").unbind('click');})
	$("#replaceIngredient").modal('show');
} 

//request server to update the user
function postReplaceIngredient(e) {
	$("#replaceIngredient").modal('hide');

	var origName = e.data.ingredName;
	var selName = $('#ingredTAName').val();
	var enterName = $('#nameTA').val();
	
	console.log('origName: ' + origName);
	console.log('selName: ' + selName);
	console.log('enterName: ' + enterName);
	
	if (selName !== enterName) {
		displayOKMsg(origName, getMessage('exception.ingredient.cannotadd'));
		return;
	}
	
	if (origName == enterName) {
		displayOKMsg(origName, getMessage('exception.ingredient.cannotreplacesame'));
		return;
	}
	
	var replaceId = $('#ingredTAId').val();
	var data = {"id" : e.data.ingredId, "name" : selName, "usage" : replaceId};
	
	$.ajax({
	    type: 'POST',
		url: '/recipeorganizer/admin/replaceIngredient',
		dataType: 'json',
		data: JSON.stringify(data),
		contentType: 'application/json'
	})
	.done(function(data) {
		console.log('updateIngredient done');
		console.log('data:' + data);
		removeRow(data.id);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

//update the datatable
function ingredientUpdate(ingredient) {
	var table = $('#ingredientList').DataTable();
	var ndx = table.row('#' + ingredient.id).index();
	table.cell(ndx,0).data(ingredient.name).draw();
};

//update the datatable
function removeRow(ingredId) {
	var table = $('#ingredientList').DataTable();
	var row = table.row('#' + ingredId);
	row.remove();
	table.draw();
}

$(function() {
	
	initIngredientsTA();
	
	$('#ingredientList').DataTable({
    	columnDefs: [{
    		targets: [-1,-2,-3,-4,-5],
    		orderable: false
    	}],
		language : {
	    	emptyTable:     getMessage('ingredadmin.table.emptyTable'),
		    info:           getMessage('ingredadmin.table.info'),
		    infoEmpty:      getMessage('ingredadmin.table.infoEmpty'),
		    infoFiltered:	getMessage('ingredadmin.table.infoFiltered'),
		    lengthMenu:		getMessage('ingredadmin.table.lengthMenu'),
		    zeroRecords:	getMessage('ingredadmin.table.zeroRecords'),
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
		.on('typeahead:autocompleted typeahead:selected', '#nameTA', function(obj, datum)
		{
			console.log("autocomplete:");
			console.log(obj);
			console.log(datum);
		
			if ((datum.name == null) || (datum.name == ''))
				return;
			
			$('#ingredTAName').val(datum.name);
			$('#ingredTAId').val(datum.id);
		})
		.on('typeahead:active', '#nameTA', function(obj, datum)
		{
			console.log("active:");
			console.log(obj);
			console.log(datum);
		
			$('#ingredTAName').val("");
			$('#ingredTAId').val(0);
		})
})
