//if the form is returned with an error set the selected category
function setCategory() {
	var id = $('#catID').val();
	console.log("catID=" + id);
	if (id > 0) {
		$("#inputCategory option[data-id='" + id + "']").prop('selected',true);
		$("#inputCategory").removeClass('select-placeholder');
	}			
}

//shorthand for document.ready
$(function() {
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
			$('#dupeErr').hide();
			//$('#nameLabel').html("Name:");
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
				//$('#nameLabel').html("Name:&nbsp;&nbsp;" + respText);
				return;
			}
		})
	})
	.on('click', '#proceed', function(e)
	{
		var num = $('#inputPrepHour').val();
		if (num == null)
			$('#inputPrepHour').val(0);
		num = $('#inputPrepMinute').val();
		if (num == null)
			$('#inputPrepMinute').val(0);
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