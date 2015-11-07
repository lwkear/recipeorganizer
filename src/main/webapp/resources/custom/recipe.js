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

//TODO: GUI: test for special characters
function setSourceUrl(url, query) {
	var source = $('#inputSource').val();
	var newurl = url + '?searchStr=' + query + '&type=' + source;
	return newurl;
};

//TODO: GUI: test for special characters
function setTagsUrl(url, query) {
	var userId = $('#userID').val();
	var newurl = url + '?searchStr=' + query + '&userId=' + userId;
	return newurl;
};

function setBHPrefetchOpts(cache, prefetchUrl, prefetchFilter) {

	var prefetchOptions = {};

	prefetchOptions['url'] = prefetchUrl;
	prefetchOptions['cache'] = cache;
	prefetchOptions['filter'] = prefetchFilter;

	return prefetchOptions;
}

function setBHRemoteOpts(cache, wildcard, remoteUrl, remoteFilter) {

	var remoteOptions = {};

	remoteOptions['url'] = remoteUrl;
	remoteOptions['cache'] = cache;
	remoteOptions['wildcard'] = wildcard; 
	remoteOptions['filter'] = remoteFilter;

	return remoteOptions;
}

function setBHOptions(sufficient, datumToken, queryToken, prefetchOptions, remoteOptions) {

	var token = Bloodhound.tokenizers.whitespace; 
	
 	var options = {};
	
 	options['sufficient'] = sufficient; 
	options['datumTokenizer'] = datumToken === null ? token : datumToken;
	options['queryTokenizer'] = queryToken === null ? token : queryToken;

	if (prefetchOptions != null)
		options['prefetch'] = prefetchOptions;

	if (remoteOptions != null)
		options['remote'] = remoteOptions;

	return options;
}	

function initTypeaheadOptions(hint, highlight, minLength) {

	var options = {};
	
	options['hint'] = hint;
	options['highlight'] = highlight;
	options['minLength'] = minLength;

	return options;
};

function initTypeaheadDataset(name, displayKey, limit, source) {

	var dataset = {};

	dataset['name'] = name;
	dataset['displayKey'] = displayKey;
	dataset['limit'] = limit;
    dataset['source'] = source;
	
	return dataset;
};

var prefetchOpts = setBHPrefetchOpts(false, '/recipeorganizer/resources/ingredients.json', ingredPrefetchFilter);
/* var remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/addRecipe/getIngredients?searchStr=%QUERY', ingredRemoteFilter); */
var remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getIngredients?searchStr=%QUERY', ingredRemoteFilter);
var bhOpts = setBHOptions(50, ingredDatumToken, null, prefetchOpts, remoteOpts);
var ingredBH = new Bloodhound(bhOpts);

prefetchOpts = setBHPrefetchOpts(false, '/recipeorganizer/resources/measures.json', null);
bhOpts = setBHOptions(50, null, null, prefetchOpts, null);
var measureBH = new Bloodhound(bhOpts);

/* remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/addRecipe/getQualifiers?searchStr=%QUERY', null); */
remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getQualifiers?searchStr=%QUERY', null);
bhOpts = setBHOptions(50, null, null, null, remoteOpts);
var qualifierBH = new Bloodhound(bhOpts);

/* remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/addRecipe/getSources', null); */
remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getSources', null);
remoteOpts['replace'] = function(url, query) {return setSourceUrl(url, query);}; 
bhOpts = setBHOptions(50, null, null, null, remoteOpts);
var sourceBH = new Bloodhound(bhOpts);

/* remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/addRecipe/getTags', null); */
remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getTags', null);
remoteOpts['replace'] = function(url, query) {return setTagsUrl(url, query);};
bhOpts = setBHOptions(50, null, null, null, remoteOpts);
var tagsBH = new Bloodhound(bhOpts);

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

function initSourceTA() {

	var options = initTypeaheadOptions(true,true,1);
	var dataset = initTypeaheadDataset('source', null, 20, sourceBH);
	$('.srcTA').typeahead(options,dataset);
};

function initTagsTA() {

	var options = initTypeaheadOptions(true,true,1);
	var dataset = initTypeaheadDataset('tags', null, 20, tagsBH);
	$('#inputTags').typeahead(options,dataset);
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

//if the form is returned with an error set the selected category
function setCategory() {
	var id = $('#catID').val();
	console.log("catID=" + id);
	if (id > 0) {
		$("#inputCategory option[data-id='" + id + "']").prop('selected',true);
		$("#inputCategory").removeClass('select-placeholder');
	}			
}

function initSource() {
	$('#inputBookName').val('');
	$('#inputBookPage').val('');
	$('#inputMagName').val('');
	$('#inputMagDate').val(null);
	$('#inputNewsName').val('');
	$('#inputNewsDate').val(null);
	$('#inputPersonName').val('');
	$('#inputWebURL').val('');
	$('#inputRecipeURL').val('');
	$('#inputOtherDetails').val('');
}

//if the form is returned with an error show the appropriate inputs
function setSource() {
	var ndx = $('#inputSource option:selected').index();
	//the first option is the placeholder
	if (ndx > 0) {
		var today = new Date();

		var option = $('#inputSource').val();
		if (option.length > 0) {
			$('.srcGroup').hide(); 
			if (option === 'Cookbook') {
				$('.bookGroup').show();			
			}		
			if (option === 'Magazine') {
				$('.magGroup').show();
				$('.magGroup').css('z-index','999999 !important');			
				$('.ui-datepicker').css('z-index','999999 !important');
			}		
			if (option === 'Newspaper') {
				$('.newsGroup').show();
				$('.newsGroup').css('z-index','999999 !important');
				$('.ui-datepicker').css('z-index','999999 !important');	
			}		
			if (option === 'Person') {
				$('.personGroup').show();			
			}		
			if (option === 'Website') {
				$('.webGroup').show();			
			}		
			if (option === 'Other') {
				$('.otherGroup').show();			
			}
			$('#inputSource').removeClass('select-placeholder');
		}
	}				
};

function fixArrayIndexes(element, sequence) {
	console.log("fixArray:" + element);
	$(element).each(function(index) {
		$(this).attr('name',$(this).attr('name').replace(/\[[0-9]+\]/,'['+index+']'));
		if (sequence) {
			$(this).val(index+1);
		}
	});
};

function resetArrays(element, removeBtn) {
	console.log("resetArray:" + element);
	var numItems = $(removeBtn).length;
	if (numItems > 1) {
		$(element).each(function(index) {
			var currentEntry = $(this).parents('.form-group:first');
			var display = currentEntry.find(removeBtn).css('display');
			if (display !== 'none') {
				currentEntry.remove();
			}				
		})
	}
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

//TODO: GUI: replace these JQuery UI controls with Bootstrap controls?
//TODO: GUI: try to get the calendar icon to appear
$.datepicker.setDefaults({
	dateFormat: "mm/dd/yy",
	defaultDate: null,
	//buttonImage: "/recipeorganizer/resources/jqueryui-smoothness/images/calendar-icon.png",
	//showOn: "both",
    beforeShow: function() {
    	$(this).css("z-index", 999);	//bootstrap assigns a z-index of 2 to a form-control which hides the datepicker
    }
});

$('#inputNewsDate').datepicker();
$('#inputMagDate').datepicker();

//TODO: GUI: typeahead for tagsinput doesn't always work the first time - an initialization issue?
$('#inputTags').tagsinput({
	tagClass: function(item) {
    	return 'label label-primary';
    },
    maxtags: 10,
    maxchars: 25,
    trimvalue: true,
    typeaheadjs: [ 
		{
			hint: true,
			highlight: true,
			minLength: 1
		},
		{
			name: 'tags',
			limit: 20,
			source: tagsBH 
		}
	]
});

//get categories and set the options
/* $.getJSON("/recipeorganizer/recipe/addRecipe/getCategories") */
$.getJSON("/recipeorganizer/recipe/getCategories")
	.done(function (data) {
		$.each(data, function (index, item) {
	    	$('#inputCategory').append(
	        	$('<option>')
	        		.val(item.name)
	        		.html(item.name)
	        		.attr('data-id',item.id)
			);
		});
		setCategory();
 	});

setSource();
initIngredientsTA();
initMeasuresTA();
initQualifiersTA();
initSourceTA();
initTagsTA();

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
  	newEntry.find('.ingredErrGrp2').hide();
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
	$(this).parents('.form-group:first').prev('.ingredErrGrp2').remove();
	$(this).parents('.form-group:first').remove();    	
	return false;
})
.on('click', '.addInstruction', function(e)
{
	e.preventDefault();
	//see addIngredient notes above 
	var currentEntry = $(this).parents('.form-group:first');
	var	newEntry = $(currentEntry.clone()).insertAfter(currentEntry);
	newEntry.find('textarea').val('');
	newEntry.removeClass('has-error');
	currentEntry.find('.removeInstruction').show();		
})    
.on('click', '.removeInstruction', function(e)
{
	e.preventDefault();
	//there are 2 separate <div> sections that need to be removed
	$(this).parents('.form-group:first').prev('.instructErrGrp').remove();
	$(this).parents('.form-group:first').remove();
	return false;
})
.on('click', '#save', function(e)
{
	//remove the typeahead html added to the form which included the class w/o the name attribute 
    $('.ingredQtyType').typeahead('destroy');
    $('.ingredQual').typeahead('destroy');

	//remove rows that are completely empty
	var fields = ['.instructDesc'];
    removeEmptyRows('.instructGrp',fields);
    fields = ['.ingredQty','.ingredQtyType','.ingredDesc','.ingredQual'];
    removeEmptyRows('.ingredGrp',fields);
    
	//each of the individual elements require their array indexes to be set/reset
	fixArrayIndexes('.instructDesc', false);
	fixArrayIndexes('.instructSeq', true);
	fixArrayIndexes('.ingredID', false);
	fixArrayIndexes('.ingredQty', false);
	fixArrayIndexes('.ingredQtyType', false);
	fixArrayIndexes('.ingredQual', false);
	fixArrayIndexes('.ingredSeq', true);

	var ndx = $('#inputSource option:selected').index();
	if (ndx == 0 || ndx == 7) {
		$('#inputSource').val("");
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
	.fail(function(jqXHR, status, error){
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
		currentEntry.find('.ingredErrGrp2').show();
	})
})
//TODO: GUI: cannot make this work - lots of threads regarding the issues involved
/* .on('blur', '#inputQty', function(e) */
/* .on('blur', '.ingredQty', function(e)
{
	var qty = $(this).val();
	if (qty.length === 0) {
		// rslt = confirm("Are you sure you don't want to enter a quantity?")
		//if (rslt == true)
		//	return;
		// $(this).triggerHandler("focus");
		setTimeout(function(){
		    $(this).focusin();
			// $(this).trigger('focus');
			//$(this).triggerHandler('focus');
		}, 50);

		e.preventDefault();
		return false;
	}
}) */	
.on('click', '#reset', function(e)
{
 	$('#inputCategory').addClass('select-placeholder');
 	$('#inputSource').addClass('select-placeholder');
 	$('.srcGroup').hide();
 	$('.ingredErrGrp1').remove();
 	$('.ingredErrGrp2').remove();
 	$('.instructErrGrp').remove();
 	$('.has-error').removeClass('has-error');
 	$('.text-danger').removeClass('text-danger');
 	$('#inputTags').tagsinput('removeAll');
 	$('#nameLabel').html('Name:');
 	$('#ingredLabel').html('Ingredients:');
 	$('#instructLabel').html('Instructions:');
 	$('#cookbookLabel').html('Cookbook Name:');
 	$('#zineLabel').html('Magazine Name:');
 	$('#zineDateLabel').html('Pub Date:');
 	$('#newsLabel').html('Newspaper Name:');
 	$('#newsDateLabel').html('Pub Date:');
 	$('#personLabel').html('Name:');
 	$('#webURLLabel').html('Website URL:');
 	$('#recipeURLLabel').html('Recipe URL;');
 	$('#otherLabel').html('Details:');
 	$('#categoryLabel').html('Category:');
 	$('#servingsLabel').html('Servings:');
 	$('#tagsLabel').html('Tags:');

 	resetArrays('.ingredQual','.removeIngredient');
 	resetArrays('.instructDesc','.removeInstruction');

 	//TODO: GUI: need to add back the "Select a source" option (display:none) 
})
.on('blur', '#inputName', function(e)
{
	e.preventDefault();

	var nameStr = $(this).val();
	//nothing to do
	if (!nameStr)
		return;

	var userIdStr = $('#userID').val();
	//save off the variable for the .done and .fail methods
	var recipeName = $(this);

	//allow jquery to create the query string from the data parameters to handle special characters
	$.ajax({
		type: 'GET',
		url: '/recipeorganizer/recipe/lookupRecipeName',
		dataType: 'json',
		data: {
			name : nameStr,
			userId : userIdStr				 
		}
	})
	.done(function(data) {
		//TODO: AJAX: probably need to check for a success status?
		console.log('Name ok (not found)');
		//fix the appearance in case a name was entered in error
		recipeName.parent('div').removeClass('has-error');
		$('#nameLabel').html("Name:");
	})
	.fail(function(jqXHR, status, error) {
		console.log('fail status: '+ jqXHR.status);
		console.log('fail error: '+ error);

		//server sets CONFLICT error if name exists
		if (jqXHR.status == 409) {
			//server currently returns a simple error message
			var respText = jqXHR.responseText;
			console.log('respText: '+ respText);
			//set the error indicator
			recipeName.parent('div').addClass('has-error');
			$('#nameLabel').html("Name:&nbsp;&nbsp;" + respText);
			return;
		}
	})
})

//attempt to remove the placeholder from tagsinput - doesn't work...
/* .on('beforeItemAdd', '.tt-input', function(e) {
 	console.log("tt-input beforeItemAdd fired");
	$(this).removeAttr('placeholder');
}) 	
.on('itemAdded', '.tt-input', function(e) {
 	console.log("tt-input itemAdded fired");
	$(this).removeAttr('placeholder');
}) */ 	

//TODO: GUI: the placeholder text is saved off my tagsinput.js and removed from the #inputTags element 
/*$('#inputTags').on('itemAdded', function(e) {
 	console.log("inputTags itemAdded fired");
	$(this).removeAttr('placeholder');
}); 	
$('#inputTags').on('beforeItemAdd', function(e) {
 	console.log("inputTags beforeItemAdd fired");
	$(this).removeAttr('placeholder');
}); */ 	

$('#inputCategory').change(function() {
	var entry = $(this).find(':selected');
	var name = entry.val();
	//var id = $(this).find(':selected').data('id');
	id = entry.data('id');
	console.log("catID=" + id);		
	console.log("catName=" + name);
	$('#catID').val(id);
	$('#catName').val(name);
	$(this).removeClass('select-placeholder');
});

$('#inputSource').change(function() {

	//prepare a date in the correct format
	var d = new Date();
	var day = d.getDate();
	day = (day < 10) ? '0' + day : day;
	var mon = d.getMonth() + 1;
	mon = (mon < 10) ? '0' + mon : mon;
	var dateStr = mon + '/' + day + '/' + d.getFullYear();

	initSource();
	
	var option = $(this).val();
	$('.srcGroup').hide(); 
	if (option === 'Cookbook') {
		$('.bookGroup').show();			
	}		
	if (option === 'Magazine') {
		$('.magGroup').show();
		$( '#inputMagDate' ).val(dateStr);			
	}		
	if (option === 'Newspaper') {
		$('.newsGroup').show();
		$( '#inputNewsDate' ).val(dateStr);			
	}		
	if (option === 'Person') {
		$('.personGroup').show();			
	}		
	if (option === 'Website') {
		$('.webGroup').show();			
	}		
	if (option === 'Other') {
		$('.otherGroup').show();			
	}
	$(this).removeClass('select-placeholder');				
	});
})
