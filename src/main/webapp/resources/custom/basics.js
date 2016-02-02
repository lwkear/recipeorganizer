//if the form is returned with an error set the selected category
function setCategory() {
	var id = $('#catID').val();
	console.log("catID=" + id);
	if (id > 0) {
		$("#inputCategory option[data-id='" + id + "']").prop('selected',true);
		$("#inputCategory").removeClass('select-placeholder');
	}			
}

function checkSections() {
	var num = $('#ingredSections').val();
	if (num != null && (num > 1 || isNaN(num))) {
		$('#ingredYes').prop('checked', true);
		$('.ingredNum').show();
	}

	num = $('#instructSections').val();
	if (num != null && (num > 1 || isNaN(num))) {
		$('#instructYes').prop('checked', true);
		$('.instructNum').show();
	}

	return false;
}

//shorthand for document.ready
$(function() {
	
	checkSections();
	
	//get categories and set the options
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
	
	$(document)
	.on('blur', '#recipeName', function(e)
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
			console.log('Name ok (not found)');
			//fix the appearance in case a name was entered in error
			recipeName.parent('div').removeClass('has-error');
			$('#dupeErr').hide();
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
				$('#dupeErr').html(respText);
				$('#dupeErr').show();
				return;
			}
		})
	})
	.on('click', '#proceed', function(e)
	{
		var num;
		
		num = $('#inputPrepHour').val();
		if (!num || !num.length)
			$('#inputPrepHour').val(0);
		num = $('#inputPrepMinute').val();
		if (!num || !num.length)
			$('#inputPrepMinute').val(0);
		
		num = $('#ingredSections').val();
		if (!num || !num.length || num === "0")
			$('#ingredSections').val(1);
		$('#currIngredSect').val(0);			
		num = $('#instructSections').val();
		if (!num || !num.length || num === "0")
			$('#instructSections').val(1);
		$('#currInstructSect').val(0);
	})
	.on('click', 'input[name="instructSet"]:checked', function(e)
	{
		if ($(this).val() == "true") {
			$('#instructSections').val(2);
			$('.instructNum').show();			
		}
		else {
			$('.instructNum').hide();
			$('#instructSections').val(1);
		}
	})
	.on('click', 'input[name="ingredSet"]:checked', function(e)
	{
		if ($(this).val() == "true") {
			$('#ingredSections').val(2);
			$('.ingredNum').show();			
		}
		else {
			$('.ingredNum').hide();
			$('#ingredSections').val(1);
		}
	})
	
	$('#inputCategory').change(function() {
		var entry = $(this).find(':selected');
		var name = entry.val();
		id = entry.data('id');
		console.log("catID=" + id);		
		console.log("catName=" + name);
		$('#catID').val(id);
		$('#catName').val(name);
		$(this).removeClass('select-placeholder');
	})
})