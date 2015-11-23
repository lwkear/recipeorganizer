<!DOCTYPE html>
<html>
<head>

<title>Edit Recipe</title>

<%@include file="../common/head.jsp"%>
<%@include file="../common/js.jsp"%>

<style type="text/css">
.glyphicon {
	font-size: 10px;
}

.select-placeholder{color: #999;}
.select-placeholder option:first-child{color: #999; display: none;}
.select-placeholder option{color: #555;} // bootstrap default color

</style>

<script type="text/javascript">

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

    function setSourceUrl(url, query) {
		var source = $('#inputSource').val();
		var newurl = url + '?searchStr=' + query + '&type=' + source;
		return newurl;
    };

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
			/* url: '/recipeorganizer/recipe/addRecipe/lookupRecipeName', */
			url: '/recipeorganizer/recipe/lookupRecipeName',
			dataType: 'json',
			data: {
				name : nameStr,
				userId : userIdStr				 
			}
		})
		.done(function(data) {
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
 	
 	/*$('#inputTags').on('itemAdded', function(e) {
 	 	console.log("inputTags itemAdded fired");
 		$(this).removeAttr('placeholder');
	}); 	
 	$('#inputTags').on('beforeItemAdd', function(e) {
 	 	console.log("inputTags beforeItemAdd fired");
 		$(this).removeAttr('placeholder');
	}); */ 	

	$('#inputCategory').change(function() {
		var id = $(this).find(':selected').data('id')
		console.log("catID=" + id);		
		$('#catID').val(id);
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
	
</script>

</head>

<body role="document">

<%@include file="../common/nav.jsp"%>

	<div class="container">

		<c:if test="${not empty dataError}">	
			<h3 class="text-danger">An error has occurred: ${dataError}</h3>
		</c:if>
	
		<h1>Edit a Recipe</h1>
		
	    <spring:hasBindErrors name="recipe">
	    <c:set var="errorCnt">${errors.errorCount}</c:set>
	    <p><b># of Errors:${errorCnt}</b></p>
	    <p></p>
		<c:forEach var="error" items="${errors.allErrors}">
			<b><c:out value="${error}" /></b>
			<p></p>
		</c:forEach>
		</spring:hasBindErrors>
		<p></p>
		<p></p>
		
		<!-- determine if instructions or ingredients have any errors -->
		<spring:hasBindErrors name="recipe">
			<c:if test="${errors.hasFieldErrors('instructions')}">
				<spring:bind path="recipe.instructions">
					<c:set var="instructListError">${status.errorMessage}</c:set>
			    	<%-- <c:out value="${status.errorMessage}"/>
			    	<c:out value="${status.displayValue}"/>
			    	<c:out value="${status.errorCode}"/> --%>
				</spring:bind>
				<c:set var="instructErr" value="true"/>		
			</c:if>
			<c:forEach items="${recipe.instructions}" varStatus="loop">
				<c:if test="${errors.hasFieldErrors('instructions[' += loop.index += '].description')}">
					<c:set var="instructErr" value="true"/>
				</c:if>				
			</c:forEach>
			<c:if test="${errors.hasFieldErrors('recipeIngredients')}">
				<spring:bind path="recipe.recipeIngredients">
					<c:set var="ingredListError">${status.errorMessage}</c:set>
			    	<%-- <c:out value="${status.errorMessage}"/>
			    	<c:out value="${status.displayValue}"/>
			    	<c:out value="${status.errorCode}"/> --%>
				</spring:bind>
				<c:set var="ingredErr" value="true"/>		
			</c:if>
			<c:forEach items="${recipe.recipeIngredients}" varStatus="loop">
				<c:if test="${errors.hasFieldErrors('recipeIngredients[' += loop.index += '].quantity')}">
					<c:set var="ingredQtyErr" value="true"/>
					<c:set var="ingredErr" value="true"/>
				</c:if>				
				<c:if test="${errors.hasFieldErrors('recipeIngredients[' += loop.index += '].qtyType')}">
					<c:set var="ingredQtyTypeErr" value="true"/>
					<c:set var="ingredErr" value="true"/>
				</c:if>
				<c:if test="${errors.hasFieldErrors('recipeIngredients[' += loop.index += '].ingredientId')}">
					<c:set var="ingredIdErr" value="true"/>
					<c:set var="ingredErr" value="true"/>
				</c:if>
				<c:if test="${errors.hasFieldErrors('recipeIngredients[' += loop.index += '].qualifier')}">
					<c:set var="ingredQualErr" value="true"/>
					<c:set var="ingredErr" value="true"/>
				</c:if>
			</c:forEach>
 		</spring:hasBindErrors>
	      
	    <!-- extract applicable error messages -->
	    <spring:bind path="recipe.name">
	    	<c:set var="nameError">${status.errorMessage}</c:set>
	    	<c:set var="nameDisplayValue">${status.displayValue}</c:set>
	    	<c:set var="nameCode">${status.errorCode}</c:set>
	    </spring:bind>
		<c:if test="${fn:contains(nameCode,'Size')}">
			<c:set var="nameLen">${fn:length(nameDisplayValue)}</c:set>
			<c:if test="${nameLen gt 0}">
				<c:set var="nameError">${nameError += " (you entered " += nameLen += ")"}</c:set>  
			</c:if>
		</c:if>		
	    
	    <spring:bind path="recipe.category.name"><c:set var="categoryError">${status.errorMessage}</c:set></spring:bind>
	    <spring:bind path="recipe.servings"><c:set var="servingsError">${status.errorMessage}</c:set></spring:bind>
	    <spring:bind path="recipe.tags"><c:set var="tagsError">${status.errorMessage}</c:set></spring:bind>
	    <c:if test="${errors.hasFieldErrors('sources')}">
		    <spring:bind path="recipe.sources[0].cookbook"><c:set var="cookbookError">${status.errorMessage}</c:set></spring:bind>
		    <spring:bind path="recipe.sources[0].magazine"><c:set var="magazineError">${status.errorMessage}</c:set></spring:bind>
		    <spring:bind path="recipe.sources[0].magazinePubdate"><c:set var="magazinePubdateError">${status.errorMessage}</c:set></spring:bind>
		    <spring:bind path="recipe.sources[0].newspaper"><c:set var="newspaperError">${status.errorMessage}</c:set></spring:bind>
		    <spring:bind path="recipe.sources[0].newspaperPubdate"><c:set var="newspaperPubdateError">${status.errorMessage}</c:set></spring:bind>
		    <spring:bind path="recipe.sources[0].person"><c:set var="personError">${status.errorMessage}</c:set></spring:bind>
		    <spring:bind path="recipe.sources[0].other"><c:set var="otherError">${status.errorMessage}</c:set></spring:bind>
		    <spring:bind path="recipe.sources[0].websiteUrl"><c:set var="websiteUrlError">${status.errorMessage}</c:set></spring:bind>
		    <spring:bind path="recipe.sources[0].recipeUrl"><c:set var="recipeUrlError">${status.errorMessage}</c:set></spring:bind>
		</c:if>

		<div class="row">
			<form:form class="form-horizontal" role="form" method="post" modelAttribute="recipe" enctype="multipart/form-data">
				<%-- <spring:bind path="recipe"> --%>
				<%-- <form:hidden id="userID" path="userId" value="3" /> --%>
				<form:hidden id="userID" path="user.id" value="3" />
				<div class="form-group col-sm-9 <c:if test="${not empty nameError}">has-error</c:if>">
					<label class="control-label" id="nameLabel" for="inputName">Name:&nbsp;&nbsp;${nameError}</label>
					<%-- <form:input type="text" size="10" class="form-control recipeName" id="inputName" placeholder="Name" path="recipe.name" autocomplete="off"/>		<!-- changed --> --%>
					<form:input type="text" size="10" class="form-control recipeName" id="inputName" placeholder="Name" path="name" autocomplete="off"/>		<!-- changed -->
				</div>
				<div class="form-group col-sm-12">
					<label class="control-label" for="inputDesc">Description:</label>
					<%-- <form:textarea class="form-control" rows="3" id="inputDesc" placeholder="Describe this recipe" path="recipe.description"></form:textarea>		<!-- changed --> --%>
					<form:textarea class="form-control" rows="3" id="inputDesc" placeholder="Describe this recipe" path="description"></form:textarea>		<!-- changed -->
				</div>
				<div class="form-group col-sm-12">
					<label class="control-label" for="inputBack">Background:</label>
					<%-- <form:textarea class="form-control" rows="3" id="inputBack" placeholder="Enter the history or background to this recipe" path="recipe.background"></form:textarea>	<!-- changed --> --%>
					<form:textarea class="form-control" rows="3" id="inputBack" placeholder="Enter the history or background to this recipe" path="background"></form:textarea>	<!-- changed -->
				</div>
				
				<!-- must bind the ingredients array, even on initial display -->
				<spring:bind path="recipe.recipeIngredients[0]"></spring:bind>
				<div class="form-group col-sm-12">	<!-- removed ingredients class -->
					<div class="form-group <c:if test="${ingredErr}">text-danger</c:if>" style="margin-bottom:0">
						<label class="control-label col-sm-12" style="text-align: left" id="ingredLabel">
							Ingredients:&nbsp;&nbsp;${ingredListError}
						</label>
					</div>
					<div class="form-group" style="margin-bottom:0">
						<label class="control-label col-sm-1 <c:if test="${ingredQtyErr}">text-danger</c:if>" style="text-align: left" >Quantity:</label>
						<label class="control-label col-sm-2 <c:if test="${ingredQtyTypeErr}">text-danger</c:if>" style="text-align: left" >Measure:</label>
						<label class="control-label col-sm-5 <c:if test="${ingredIdErr}">text-danger</c:if>" style="text-align: left" >Ingredient:</label>
						<label class="control-label col-sm-4 <c:if test="${ingredQualErr}">text-danger</c:if>" style="text-align: left" >Qualifier:</label>
					</div>
					<c:forEach items="${recipe.recipeIngredients}" var="ingred" varStatus="loop">
						<!-- bind server-side validation errors -->
						<spring:bind path="recipe.recipeIngredients[${loop.index}].quantity"><c:set var="qtyError">${status.errorMessage}</c:set></spring:bind>
						<spring:bind path="recipe.recipeIngredients[${loop.index}].qtyType"><c:set var="qtyTypeError">${status.errorMessage}</c:set></spring:bind>
						<spring:bind path="recipe.recipeIngredients[${loop.index}].ingredientId"><c:set var="ingredError">${status.errorMessage}</c:set></spring:bind>
						<spring:bind path="recipe.recipeIngredients[${loop.index}].qualifier"><c:set var="qualError">${status.errorMessage}</c:set></spring:bind>
						<!-- display server-side validation errors if present -->
						<c:if test="${ingredErr}">
							<div class="form-group ingredErrGrp1" style="margin-bottom:0">
								<label class="control-label col-sm-1 text-danger" style="text-align: left; margin-bottom:0;"><b>${qtyError}</b></label>
								<label class="control-label col-sm-2 text-danger" style="text-align: left; margin-bottom:0;"><b>${qtyTypeError}</b></label>
								<label class="control-label col-sm-5 text-danger" style="text-align: left; margin-bottom:0;"><b>${ingredError}</b></label>
								<label class="control-label col-sm-4 text-danger" style="text-align: left; margin-bottom:0;"><b>${qualError}</b></label>
							</div>
						</c:if>
						<div  class="ingredGrp">
							<!-- display ajax validation errors -->						
							<div class="form-group ingredErrGrp2" style="margin-bottom:0; display:none">
								<label class="control-label col-sm-3" style="text-align: left; margin-bottom:0; "></label>
								<label class="control-label col-sm-5 text-danger jsonIgredErr" style="text-align: left; margin-bottom:0;"><b>Error</b></label>
								<label class="control-label col-sm-4" style="text-align: left; margin-bottom:0;"></label>
							</div>
							<div class="form-group">
								<!--setting the path displays the previously entered content in an error display -->
								<%-- <form:hidden class="ingredID" id="ingredientID" path="recipe.recipeIngredients[${loop.index}].ingredientId" />	<!-- changed --> --%>
								<form:hidden class="ingredID" id="ingredientID" path="recipeIngredients[${loop.index}].ingredientId" />	<!-- changed -->
								<%-- <form:hidden class="ingredSeq" path="recipe.recipeIngredients[${loop.index}].sequenceNo"/>		<!-- changed --> --%>
								<form:hidden class="ingredSeq" path="recipeIngredients[${loop.index}].sequenceNo"/>		<!-- changed -->
								<div class="col-sm-1 <c:if test="${not empty qtyError}">has-error</c:if>">
									<%-- <form:input type="text" class="form-control ingredQty" id="inputQty" placeholder="Qty." path="recipe.recipeIngredients[${loop.index}].quantity" autocomplete="off"/>	<!-- changed --> --%>
									<form:input type="text" class="form-control ingredQty" id="inputQty" placeholder="Qty." path="recipeIngredients[${loop.index}].quantity" autocomplete="off"/>	<!-- changed -->
								</div>
								<div class="col-sm-2 <c:if test="${not empty qtyTypeError}">has-error</c:if>">
									<%-- <form:input type="text" class="form-control ingredQtyType" id="inputQtyType" placeholder="Measure" path="recipe.recipeIngredients[${loop.index}].qtyType" />	<!-- changed --> --%>
									<form:input type="text" class="form-control ingredQtyType" id="inputQtyType" placeholder="Measure" path="recipeIngredients[${loop.index}].qtyType" />	<!-- changed -->
								</div>
								<div class="col-sm-5 <c:if test="${not empty ingredError}">has-error</c:if>">
									<input type="text" class="form-control ingredDesc" id="ingredient" placeholder="Add an ingredient" value="${ingredientList[loop.index].name}"/>
								</div>
								<div class="col-sm-4 <c:if test="${not empty qualError}">has-error</c:if>">
									<div class="entry input-group">
										<%-- <form:input type="text" class="form-control ingredQual" id="inputQual" placeholder="Special qualifier" path="recipe.recipeIngredients[${loop.index}].qualifier" autocomplete="off"/>	<!-- changed --> --%>
										<form:input type="text" class="form-control ingredQual" id="inputQual" placeholder="Special qualifier" path="recipeIngredients[${loop.index}].qualifier" autocomplete="off"/>	<!-- changed -->
										<span class="input-group-btn">
											<button class="btn btn-danger removeIngredient" type="button" style="<c:if test="${loop.last}">display:none</c:if>">
												<span class="glyphicon glyphicon-minus"></span>
											</button>
											<button class="btn btn-success addIngredient" type="button">
												<span class="glyphicon glyphicon-plus"></span>
											</button>
										</span>
									</div>
								</div>
							</div>
						</div>
					</c:forEach>
				</div>

				<!-- must bind the instruction array, even on initial display -->
				<spring:bind path="recipe.instructions[0]"></spring:bind>
				<div class="form-group col-sm-12">
					<div class="form-group <c:if test="${instructErr}">text-danger</c:if>" style="margin-bottom:0">
						<label class="control-label col-sm-12" style="text-align: left" id="instructLabel">
							Instructions:&nbsp;&nbsp;${instructListError}
						</label>
					</div>
					<c:forEach items="${recipe.instructions}" var="instruction" varStatus="loop">
						<spring:bind path="recipe.instructions[${loop.index}].description"><c:set var="instructError">${status.errorMessage}</c:set></spring:bind>
						<div class="form-group instructErrGrp" style="margin-bottom:0">
							<c:if test="${not empty instructError}">
								<label class="control-label col-sm-12 text-danger" style="text-align:left; margin-bottom:0"><b>${instructError}</b></label>
							</c:if>
						</div>						
						<div class="form-group col-sm-12 <c:if test="${not empty instructError}">has-error</c:if>">
							<div class="input-group instructGrp">
								<!--setting the path displays the previously entered content in an error display -->
								<%-- <form:hidden class="instructSeq instruct" path="recipe.instructions[${loop.index}].sequenceNo"/>	<!-- changed --> --%>									
								<form:hidden class="instructSeq instruct" path="instructions[${loop.index}].sequenceNo"/>	<!-- changed -->
								<%-- <form:textarea class="form-control instructDesc instruct" rows="2" path="recipe.instructions[${loop.index}].description" placeholder="Add a step"/>		<!-- changed --> --%>
								<form:textarea class="form-control instructDesc instruct" rows="2" path="instructions[${loop.index}].description" placeholder="Add a step"/>		<!-- changed -->
								<span class="input-group-btn">
									<button class="btn btn-danger removeInstruction" type="button" style="<c:if test="${loop.last}">display:none</c:if>">
										<span class="glyphicon glyphicon-minus"></span>
									</button>
									<button class="btn btn-success addInstruction" type="button">
										<span class="glyphicon glyphicon-plus"></span>
									</button>
								</span>
							</div>
						</div>
					</c:forEach>
				</div>

				<div class="form-group col-sm-12">
					<div class="form-group" style="margin-bottom:0">
						<label class="control-label col-sm-3 <c:if test="${not empty categoryError}">text-danger</c:if>" id="categoryLabel" style="text-align: left;">
							Category:&nbsp;&nbsp;${categoryError}</label>
						<label class="control-label col-sm-3 <c:if test="${not empty servingsError}">text-danger</c:if>" id="servingsLabel" style="text-align: left;">
							Servings:&nbsp;&nbsp;${servingsError}</label>
						<label class="control-label col-sm-2" style="text-align: left;">Share this recipe?</label>
						<label class="control-label col-sm-4" style="text-align: left;">Photo:</label>
		            </div>
					<div class="form-group" style="margin-bottom:0">
						<form:hidden id="catID" path="category.id"/>
						<div class="col-sm-3 <c:if test="${not empty categoryError}">has-error</c:if>">
							<form:select class="form-control col-sm-3 select-placeholder" id="inputCategory" path="category.name">
								<option value="" style="display:none">Select a category</option>
		            		</form:select>
		            	</div>
						<div class="col-sm-3 <c:if test="${not empty servingsError}">has-error</c:if>">
							<%-- <form:input type="text" class="form-control col-sm-1" id="inputServings" placeholder="Enter # of servings" path="recipe.servings" autocomplete="off"/>	<!-- changed --> --%>
							<form:input type="text" class="form-control col-sm-1" id="inputServings" placeholder="Enter # of servings" path="servings" autocomplete="off"/>	<!-- changed -->
						</div>
						<div class="col-sm-2">
							<div class="radio-inline">
								<%-- <form:radiobutton value="true" path="recipe.allowShare" checked="true"/>Yes		<!-- changed --> --%>
								<form:radiobutton value="true" path="allowShare" checked="true"/>Yes		<!-- changed -->
							</div>
							<div class="radio-inline">
								<%-- <form:radiobutton value="false" path="recipe.allowShare" />No	<!-- changed --> --%>
								<form:radiobutton value="false" path="allowShare" />No	<!-- changed -->
							</div>
						</div>
						<div class="col-sm-4" >
							<input type="file" name="file"/>
						</div>
					</div>
				</div>
				<%-- </spring:bind> --%>

				<c:if test="${errors.hasFieldErrors('sources')}">
					<spring:bind path="recipe.sources[0]"></spring:bind>
				</c:if>
				<div class="form-group col-sm-12">
					<div class="form-group" style="margin-bottom:0">
						<label class="control-label col-sm-2" style="text-align: left">Source:</label>
						<label class="control-label col-sm-5 srcGroup bookGroup <c:if test="${not empty cookbookError}">text-danger</c:if>" style="text-align: left; display:none" id="cookbookLabel">
							Cookbook Name:&nbsp;&nbsp;${cookbookError}</label>
						<label class="control-label col-sm-1 srcGroup bookGroup" style="text-align: left; display:none">Page#:</label>
						<label class="control-label col-sm-5 srcGroup magGroup <c:if test="${not empty magazineError}">text-danger</c:if>" style="text-align: left; display:none" id="zineLabel">
							Magazine Name:&nbsp;&nbsp;${magazineError}</label>
						<label class="control-label col-sm-3 srcGroup magGroup <c:if test="${not empty magazinePubdateError}">text-danger</c:if>" style="text-align: left; display:none" id="zineDateLabel">
							Pub Date:&nbsp;&nbsp;${magazinePubdateError}</label>						
						<label class="control-label col-sm-5 srcGroup newsGroup <c:if test="${not empty newspaperError}">text-danger</c:if>" style="text-align: left; display:none" id="newsLabel">
							Newspaper Name:&nbsp;&nbsp;${newspaperError}</label>
						<label class="control-label col-sm-3 srcGroup newsGroup <c:if test="${not empty newspaperPubdateError}">text-danger</c:if>" style="text-align: left; display:none" id="newsDateLabel">
							Pub Date:&nbsp;&nbsp;${newspaperPubdateError}</label>
						<label class="control-label col-sm-4 srcGroup personGroup <c:if test="${not empty personError}">text-danger</c:if>" style="text-align: left; display:none" id="personLabel">
							Name:&nbsp;&nbsp;${personError}</label>
						<label class="control-label col-sm-5 srcGroup webGroup <c:if test="${not empty websiteUrlError}">text-danger</c:if>" style="text-align: left; display:none" id="webURLLabel">
							Website URL:&nbsp;&nbsp;${websiteUrlError}</label>
						<label class="control-label col-sm-5 srcGroup webGroup <c:if test="${not empty recipeUrlError}">text-danger</c:if>" style="text-align: left; display:none" id="recipeURLLabel">
							Recipe URL:&nbsp;&nbsp;${recipeUrlError}</label>
						<label class="control-label col-sm-7 srcGroup otherGroup <c:if test="${not empty otherError}">text-danger</c:if>" style="text-align: left; display:none" id="otherLabel">
							Details:&nbsp;&nbsp;${otherError}</label>
					</div>
					<div class="form-group" style="margin-bottom:0">
						<div class="col-sm-2">
							<form:select class="form-control col-sm-2 select-placeholder" id="inputSource" path="sources[0].type" >
		            			<form:option style="display:none" value="">Select a source</form:option>
		            			<form:option value="Cookbook"/>
		            			<form:option value="Magazine"/>
		            			<form:option value="Newspaper"/>
		            			<form:option value="Person"/>
		            			<form:option value="Website"/>
		            			<form:option value="Other"/>
		            			<form:option value="None"/>
							</form:select>
						</div>
						<div class="col-sm-5 srcGroup bookGroup <c:if test="${not empty cookbookError}">has-error</c:if>" style="display:none">
							<form:input type="text" class="form-control col-sm-5 srcGroup bookGroup srcTA" id="inputBookName" style="display:none" placeholder="Enter name" path="sources[0].cookbook"/>
						</div>
						<div class="col-sm-1 srcGroup bookGroup" style="display:none">
							<form:input type="text" class="form-control col-sm-1 srcGroup bookGroup" id="inputBookPage" style="display:none" placeholder="Page" path="sources[0].cookbookPage" autocomplete="off"/>
						</div>
						<div class="col-sm-5 srcGroup magGroup <c:if test="${not empty magazineError}">has-error</c:if>" style="display:none">
							<form:input type="text" class="form-control col-sm-5 srcGroup magGroup srcTA" id="inputMagName" style="display:none" placeholder="Enter name" path="sources[0].magazine"/>
						</div>
						<div class="col-sm-3 srcGroup magGroup <c:if test="${not empty magazinePubdateError}">has-error</c:if>" style="display:none">
							<form:input type="text" class="form-control col-sm-2 srcGroup magGroup" id="inputMagDate" style="display:none" path="sources[0].magazinePubdate"/>
						</div>
						<div class="col-sm-5 srcGroup newsGroup <c:if test="${not empty newspaperError}">has-error</c:if>" style="display:none">
							<form:input type="text" class="form-control col-sm-5 srcGroup newsGroup srcTA" id="inputNewsName" style="display:none" placeholder="Enter name" path="sources[0].newspaper"/>
						</div>
						<div class="col-sm-3 srcGroup newsGroup <c:if test="${not empty newspaperPubdateError}">has-error</c:if>" style="display:none">
							<form:input type="text" class="form-control col-sm-2 srcGroup newsGroup" id="inputNewsDate" style="display:none" path="sources[0].newspaperPubdate"/>
						</div>
						<div class="col-sm-4 srcGroup personGroup <c:if test="${not empty personError}">has-error</c:if>" style="display:none">
							<form:input type="text" class="form-control col-sm-4 srcGroup personGroup srcTA" id="inputPersonName" style="display:none" placeholder="Enter name" path="sources[0].person"/>
						</div>
						<div class="col-sm-5 srcGroup webGroup <c:if test="${not empty websiteUrlError}">has-error</c:if>" style="display:none">
							<form:input type="text" class="form-control col-sm-5 srcGroup webGroup srcTA" id="inputWebURL" style="display:none" placeholder="Enter URL" path="sources[0].websiteUrl"/>
						</div>
						<div class="col-sm-5 srcGroup webGroup <c:if test="${not empty recipeUrlError}">has-error</c:if>" style="display:none">
							<form:input type="text" class="form-control col-sm-5 srcGroup webGroup" id="inputRecipeURL" style="display:none" placeholder="Enter URL" path="sources[0].recipeUrl"/>
						</div>
						<div class="col-sm-7 srcGroup otherGroup <c:if test="${not empty otherError}">has-error</c:if>" style="display:none">
							<form:input type="text" class="form-control col-sm-7 srcGroup otherGroup" id="inputOtherDetails" style="display:none" placeholder="Enter details" path="sources[0].other" autocomplete="off"/>
						</div>
					</div>
				</div>
	
				<div class="form-group col-sm-12 <c:if test="${not empty tagsError}">has-error</c:if>">
					<label class="control-label" id="tagsLabel" for="inputTags">Tags:&nbsp;&nbsp;${tagsError}</label>
					<div class="form-group col-sm-12 <c:if test="${not empty tagsError}">has-error</c:if>" style="margin-bottom:0">
						<%-- <form:input class="form-control col-sm-6" type="text" id="inputTags" autocomplete="off" placeholder="Enter tags for this recipe" path="recipe.tags"/>	<!-- changed --> --%>
						<form:input class="form-control col-sm-6" type="text" id="inputTags" autocomplete="off" placeholder="Enter tags for this recipe" path="tags"/>	<!-- changed -->
					</div>					
				</div>
	
				<div class="form-group col-sm-12">
					<label class="control-label" for="inputNotes">Notes:</label>
					<%-- <form:textarea class="form-control" rows="3" id="inputNotes" placeholder="Enter any special notes, tips or instructions" path="recipe.notes"></form:textarea>	<!-- changed --> --%>					
					<form:textarea class="form-control" rows="3" id="inputNotes" placeholder="Enter any special notes, tips or instructions" path="notes"></form:textarea>	<!-- changed -->
				</div>
	
				<div class="form-group col-sm-12"></div>
	
				<div class="form-group col-sm-12">
					<div class="col-sm-offset-5 col-sm-2 text-center">
						<button type="submit" class="btn btn-primary pull-left" id="save" name="save" value="Save">Save</button>
						<input type="reset" class="btn btn-default pull-right" id="reset" value="Reset">
					</div>
				</div>
			</form:form>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- Placed at the end of the document so the pages load faster -->

</html>
