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

function measuresFile() {
	var locale = $('#localeCode').val();
	var jsonFile = appContextPath + '/resources/measures_' + locale.toUpperCase() + '.json';
	return jsonFile;
}

var remoteOpts = setBHRemoteOpts(false, '%QUERY', appContextPath + '/recipe/getIngredients?searchStr=%QUERY', ingredRemoteFilter);
var bhOpts = setBHOptions(20, ingredDatumToken, null, null, remoteOpts);
var ingredBH = new Bloodhound(bhOpts);

prefetchOpts = setBHPrefetchOpts(false, measuresFile(), null);
bhOpts = setBHOptions(20, null, null, prefetchOpts, null);
var measureBH = new Bloodhound(bhOpts);

remoteOpts = setBHRemoteOpts(false, '%QUERY', appContextPath + '/recipe/getQualifiers?searchStr=%QUERY', null);
bhOpts = setBHOptions(20, null, null, null, remoteOpts);
var qualifierBH = new Bloodhound(bhOpts);

function initIngredientsTA() {
	var options = initTypeaheadOptions(true,true,2);
	var dataset = initTypeaheadDataset('ingredients', 'name', 20, ingredBH);
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

function getIngredSelection(obj) {
	//get the index of the current Description element
	var ndx = obj.parents('.form-group').find('.ingredDesc').index(obj);
	//typeahead adds an element with same class name so need to divide by 2 and round down
	ndx = Math.floor(ndx / 2);
	//find the corresponding ingredient ID element and save it off for the .done function
	var ingredId = obj.parents('.form-group').find('.ingredSelection').eq(ndx);

	return ingredId;
}

function fixIngredArrayIndexes(element, sequence) {
	console.log("fixIngredArray:" + element);
	$(element).each(function(index) {
		console.log("element:" + $(this).val());
		$(this).attr('name',$(this).attr('name').replace(/recipeIngredients\[[0-9]+\]/,'recipeIngredients['+index+']'));
		if (sequence) {
			$(this).val(index+1);
		}
	});
};

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
		  	//scroll the screen using the proceed button as an anchor
		  	scrollPage("ingred");
		
			//restore the typeaheads
		  	initIngredientsTA();
		  	initMeasuresTA();
		  	initQualifiersTA();      	
		})    
		.on('click', '.removeIngredient', function(e)
		{
			e.preventDefault();
			//remove the parent <div> of this ingredient
			$(this).closest('.ingredGrp').remove();
			return false;
		})
		.on('click', '.row-adjust', function(e)
		{
			//remove the typeahead html added to the form which included the class w/o the name attribute 
		    $('.ingredQtyType').typeahead('destroy');
		    $('.ingredQual').typeahead('destroy');
		    $('.ingredDesc').typeahead('destroy');
		
			//remove rows that are completely empty
		    var fields = ['.ingredQty','.ingredQtyType','.ingredDesc','.ingredQual'];
		    removeEmptyRows('.ingredGrp',fields);
		    
			//each of the individual elements require their array indexes to be set/reset
		    fixIngredArrayIndexes('.recipeIngredID', false);
			fixIngredArrayIndexes('.ingredID', false);
			fixIngredArrayIndexes('.ingredQty', false);
			fixIngredArrayIndexes('.ingredQtyType', false);
			fixIngredArrayIndexes('.ingredDesc', false);
			fixIngredArrayIndexes('.ingredQual', false);
			fixIngredArrayIndexes('.ingredSeq', true);
			
			//set new row id's to zero to avoid empty string conversion error
		    $('.recipeIngredID').each(function() {
				var id = $(this).val();
				if (id == "")
					$(this).val(0);
			});	    

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
			var ingredSel = getIngredSelection($(this));
			
			ingredId.val(datum.id);
			ingredSel.val(datum.name);
		})
		.on('typeahead:active', '.ingredDesc', function(obj, datum)
		{
			console.log("active:");
			console.log(obj);
			console.log(datum);
		
			var ingredId = getIngredId($(this));
			var ingredSel = getIngredSelection($(this));
		
			//zero out the id in case a previously entered ingredient is written over with a new one
			ingredId.val(0);
			ingredSel.val("");
		})
		.on('blur', '.ingredDesc', function(e)
		{
			e.preventDefault();
		
			var desc = $(this).val();
			//nothing to do
			if ((desc == null) || (desc == ''))
				return;
			if (desc.length < 3) {
				displayOKMsg(getMessage('recipe.ingredients.title'), getMessage('recipe.ingredients.length'));
				return;
			}
		
			//save off these variables for the .done and .fail methods
			var ingredDesc = $(this);
			var ingredId = getIngredId($(this));
			var ingredSel = getIngredSelection($(this));
			
			//nothing to do
			if (ingredId.val() != 0)
			{
				if (ingredSel.val() === desc) {
					console.log('ingredient selected from list: '+ ingredSel.val());
					return;
				}
			}
		
			//trim leading/trailing spaces
			desc = desc.trim();
			
			//capitalize the first letter of each word
			desc = desc.toLowerCase().replace(/\b[a-z]/g, function(letter) {
			    return letter.toUpperCase();
			});
		
			//new entry - format the json for adding it to the database; default to english
			var data = {"id":"0","name":desc,"reviewed":0,"lang":"en"};
		
			$.ajax({
			    contentType: 'application/json',
				type: 'POST',
				url: appContextPath + '/recipe/addIngredient',
				dataType: 'json',
				data: JSON.stringify(data)
			})
			.done(function(data) {
				console.log('Setting Ajax ingredient ID: '+ data.id);
				ingredId.val(data.id);
				ingredDesc.val(desc);
			})
			.fail(function(jqXHR, status, error) {
				var data = jqXHR.responseJSON;
				console.log('fail data: '+ data);
				//set the error indicator
				ingredDesc.parents('div:first').addClass('has-error');
				//get the parent <div> for this edit box, then set the error message and show the error 
				var currentEntry = ingredDesc.parents('.ingredGrp:first');
				currentEntry.find('.jsonIgredErr').html(data.msg);
				currentEntry.find('.ingredErrGrp').show();
			})
		})
})


/*["Cuiller à café","Cuillers à café","Cuiller à soupe","Cuillers à soupe""Milligram","Milligrams","Gram","Grams","Kilogram","Kilograms","Milliliter","Milliliters","Liter","Liters"]*/