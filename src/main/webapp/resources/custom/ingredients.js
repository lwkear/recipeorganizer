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

var prefetchOpts = setBHPrefetchOpts(false, '/recipeorganizer/resources/ingredients.json', ingredPrefetchFilter);
var remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getIngredients?searchStr=%QUERY', ingredRemoteFilter);
var bhOpts = setBHOptions(50, ingredDatumToken, null, prefetchOpts, remoteOpts);
var ingredBH = new Bloodhound(bhOpts);

prefetchOpts = setBHPrefetchOpts(false, '/recipeorganizer/resources/measures.json', null);
bhOpts = setBHOptions(50, null, null, prefetchOpts, null);
var measureBH = new Bloodhound(bhOpts);

remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getQualifiers?searchStr=%QUERY', null);
bhOpts = setBHOptions(50, null, null, null, remoteOpts);
var qualifierBH = new Bloodhound(bhOpts);

function initIngredientsTA() {

	var options = initTypeaheadOptions(true,true,1);
	var dataset = initTypeaheadDataset('ingredients', 'name', 50, ingredBH);
	$('.ingredDesc').typeahead(options,dataset);
};

function initMeasuresTA() {

	var options = initTypeaheadOptions(true,true,1);
	var dataset = initTypeaheadDataset('measures', null, 50, measureBH);
	$('.ingredQtyType').typeahead(options,dataset);
};

function initQualifiersTA() {

	var options = initTypeaheadOptions(true,true,1);
	var dataset = initTypeaheadDataset('qualifiers', null, 50, qualifierBH);
	$('.ingredQual').typeahead(options,dataset);
};

function getIngredId(obj) {
	//get the index of the current Description element
	var ndx = obj.parents('.form-group').find('.ingredDesc').index(obj);
	//typeahead adds an element with same class name so need to divide by 2 and round down
	ndx = Math.floor(ndx / 2);
	//find the corresponding ingredient ID element and save it off for the .done function
	var ingredId = obj.parents('.form-group').find('.ingredID').eq(ndx);

	return ingredId;
}

function fixArrayIndexes(element, sequence) {
	console.log("fixArray:" + element);
	$(element).each(function(index) {
		$(this).attr('name',$(this).attr('name').replace(/\[[0-9]+\]/,'['+index+']'));
		if (sequence) {
			$(this).val(index+1);
		}
	});
};

function removeEmptyRows(element, fields) {
	console.log("removeEmptyRows");
	console.log("element=" + element);
	$(element).each(function(index) {
		console.log("row#" + index);
		var currentRow = $(this);
		var notEmpty = false;
		for	(ndx = 0; ndx < fields.length; ndx++) {
			console.log("field[" + ndx + "]=" + fields[ndx]);
			var str = currentRow.find(fields[ndx]).val().trim();
			if (str.length > 0) {
				console.log("notempty");
				notEmpty = true;
				break;
			}
		}
		if (notEmpty === false) {
			console.log("removing row#" + index);
			currentRow.remove();
		}									
	});		
}

//shorthand for document.ready
$(function() {

	initIngredientsTA();
	initMeasuresTA();
	initQualifiersTA();
	
	//these events must reside in $(document) because the dynamically added elements are not
	//visible to the DOM otherwise
	$(document)
		.on('click', '.addIngredient', function(e)
		{
		    e.preventDefault();
			//disable all typeaheads (new entries won't be enabled)
		    $('.ingredDesc').typeahead('destroy');
		    $('.ingredQtyType').typeahead('destroy');
		    $('.ingredQual').typeahead('destroy');
		    
			//get the parent <div> with all the controls, labels, etc. that belong to the clicked button, then insert a clone after the current set
			var currentEntry = $(this).parents('.ingredGrp:first');		
		    var newEntry = $(currentEntry.clone()).insertAfter(currentEntry);
		    //clear any entered text from the current entry, remove has-error class, hide the ajax error messages (just in case)
		  	newEntry.find('input').val('');
		  	newEntry.find('.has-error').removeClass('has-error');
		  	newEntry.find('.ingredErrGrp').hide();
		  	//show the "minus" button for the current entry
		  	currentEntry.find('.removeIngredient').show();
		
			//restore the typeaheads
		  	initIngredientsTA();
		  	initMeasuresTA();
		  	initQualifiersTA();      	
		})    
		.on('click', '.removeIngredient', function(e)
		{
			e.preventDefault();
			//there are 2 separate <div> sections that need to be removed
			$(this).parents('.form-group:first').prev('.ingredErrGrp').remove();
			$(this).parents('.form-group:first').remove();    	
			return false;
		})
		.on('click', '.row-adjust', function(e)
		{
			//remove the typeahead html added to the form which included the class w/o the name attribute 
		    $('.ingredQtyType').typeahead('destroy');
		    $('.ingredQual').typeahead('destroy');
		
			//remove rows that are completely empty
		    var fields = ['.ingredQty','.ingredQtyType','.ingredDesc','.ingredQual'];
		    removeEmptyRows('.ingredGrp',fields);
		    
			//each of the individual elements require their array indexes to be set/reset
		    /*fixArrayIndexes('.recipeIngredID', false);*/
			fixArrayIndexes('.ingredID', false);
			fixArrayIndexes('.ingredQty', false);
			fixArrayIndexes('.ingredQtyType', false);
			fixArrayIndexes('.ingredQual', false);
			fixArrayIndexes('.ingredSeq', true);
			
		    var name = $(this).attr('name');
		    if (name == '_eventId_proceed') {
		    	//set default initial value in case the user has navigated back from instructions 
			    console.log("_eventId_proceed");
				console.log("currInstructSect set val to 0");
				$('#currInstructSect').val(0);
		    }
		})
		.on('typeahead:autocompleted typeahead:selected', '.ingredDesc', function(obj, datum)
		{
			console.log("autocomplete:");
			console.log(obj);
			console.log(datum);
		
			if ((datum.name == null) || (datum.name == ''))
				return;
			
			var ingredId = getIngredId($(this));
			
			ingredId.val(datum.id);
		})
		.on('typeahead:active', '.ingredDesc', function(obj, datum)
		{
			console.log("active:");
			console.log(obj);
			console.log(datum);
		
			var ingredId = getIngredId($(this));
		
			//zero out the id in case a previously entered ingredient is written over with a new one
			ingredId.val(0);
		})
		.on('blur', '.ingredDesc', function(e)
		{
			e.preventDefault();
		
			var desc = $(this).val();
			//nothing to do
			if ((desc == null) || (desc == ''))
				return;
			//TODO: GUI: make this a nicer looking modal popup
			if (desc.length < 3) {
				alert("An ingredient must be more than 2 characters long.")
				return;
			}
		
			//save off these variables for the .done and .fail methods
			var ingredDesc = $(this);
			var ingredId = getIngredId($(this)); 
			
			//nothing to do
			if (ingredId.val() != 0)
			{
				console.log('ingredient ID already set: '+ ingredId.val());
				return;
			}
		
			//capitalize the first letter of each word
			desc = desc.toLowerCase().replace(/\b[a-z]/g, function(letter) {
			    return letter.toUpperCase();
			});
		
			//new entry - format the json for adding it to the database
			var data = {"id":"0","name":desc};
		
			$.ajax({
				headers: { 
			        'Accept': 'application/json',
			        'Content-Type': 'application/json' 
			    },
				type: 'POST',
				/* url: '/recipeorganizer/recipe/addRecipe/addIngredient', */
				url: '/recipeorganizer/recipe/addIngredient',
				dataType: 'json',
				data: JSON.stringify(data)
			})
			.done(function(data) {
				//TODO: AJAX: probably need to check for a success status?
				console.log('Setting Ajax ingredient ID: '+ data.id);
				ingredId.val(data.id);
				ingredDesc.val(desc);
			})
			.fail(function(jqXHR, status, error) {
				console.log('fail request: '+ jqXHR);
				console.log('fail status: '+ status);
				console.log('fail error: '+ error);
		
				//server currently returns a simple error message
				var respText = jqXHR.responseText;
				console.log('respText: '+ respText);
				//set the error indicator
				ingredDesc.parents('div:first').addClass('has-error');
				//get the parent <div> for this edit box, then set the error message and show the error 
				var currentEntry = ingredDesc.parents('.ingredGrp:first');
				currentEntry.find('.jsonIgredErr').html(respText);
				currentEntry.find('.ingredErrGrp').show();
			})
		})
})
