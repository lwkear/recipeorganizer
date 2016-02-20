$(function() {

	$(document)
	.on('blur', '#email', function(e)
	{
		e.preventDefault();

		var emailStr = $(this).val();
		//nothing to do
		if (!emailStr)
			return;
		
		//the error is visible, so it must be because of a maxSize error being displayed
		if ($('#emailErrMsg').is(':visible'))
			return;

		//save off the variable for the .done and .fail methods
		var username = $(this);

		//allow jquery to create the query string from the data parameters to handle special characters
		$.ajax({
			type: 'GET',
			url: '/recipeorganizer/lookupUser',
			dataType: 'json',
			data: {
				email : emailStr
			}
		})
		.done(function(jqXHR, status, msg) {
			console.log('Name ok (not found)');
			//fix the appearance in case a name was entered in error
			username.parent('div').removeClass('has-error');
			$('#emailErrMsg').html("");
		})
		.fail(function(jqXHR, status, error) {
			var data = jqXHR.responseJSON;
			console.log('fail data: '+ data);

			//server sets CONFLICT error if name exists
			if (jqXHR.status == 409) {
				//set the error indicator
				username.parent('div').addClass('has-error');
				$('#emailErrMsg').html(data.msg);
				return;
			}
		});
	});
});
