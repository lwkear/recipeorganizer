function checkPasswordScore() {
	//defaultOptions.ui.scores = [14, 26, 38, 50];
	
	var score = $('#pswdScore').val();
	if (score >= 38)
		submitSignup();
	
	$("#messageTitle").text(getMessage('common.warning'));
	$("#messageMsg").text(getMessage('pswd.score.risky'));
	$(".msgDlgBtn").hide();
	$("#okBtn").show();
	$("#cnclBtn").show();
	$("#okBtn").one('click', submitFormWithPassword);
	$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
	$("#messageDlg").on('hidden.bs.modal', function(){$("#okBtn").unbind('click');})
	$("#messageDlg").modal('show');
}

function submitFormWithPassword() {
	$("#messageDlg").modal('hide');
	//submit the form
	document.forms["formWithPswd"].submit();
}

$(function() {

	$('#password').pwstrength({
		common : {
			debug: true,
			onKeyUp: function (evt, data) {
				$("#pswdScore").val(data.score);
			}
		},
		//rules: {},
		ui: {
			verdicts: [getMessage('pswd.verdict.weak'),
			           getMessage('pswd.verdict.normal'),
			           getMessage('pswd.verdict.medium'),
			           getMessage('pswd.verdict.strong'),
			           getMessage('pswd.verdict.verystrong')],
			errorMessages:
			{
				wordLength: getMessage('pswd.wordLength'),
				wordNotEmail: getMessage('pswd.wordNotEmail'),
				wordSimilarToUsername: getMessage('pswd.wordSimilarToUsername'),
				wordTwoCharacterClasses: getMessage('pswd.wordTwoCharacterClasses'),
				wordRepetitions: getMessage('pswd.wordRepetitions'),
				wordSequences: getMessage('pswd.wordSequences')
			},
			container: "#pwd-container",
			viewports: 
			{
				errors: ".pwstrength_viewport_errors"
			},
			showErrors: true,
			showVerdictsInsideProgressBar: true,
			spanError:
				function (options, key) {
					var text = options.ui.errorMessages[key];
					if (!text) { return ''; }
					return '<span class="text-danger">' + text + '</span>';
				}
		}
	});
	
	$(document)
	.on('click', '#btnSubmit', function(e){
		e.preventDefault();
		checkPasswordScore();
	})
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
			data: {"email" : emailStr}
		})
		.done(function(data) {
			console.log('Name ok (not found)');
			//fix the appearance in case a name was entered in error
			username.parent('div').removeClass('has-error');
			$('#emailErrMsg').hide();
		})
		.fail(function(jqXHR, status, error) {
			var data = jqXHR.responseJSON;
			console.log('fail data.msg: '+ data.msg);

			//server sets CONFLICT error if name exists
			if (jqXHR.status == 409) {
				//set the error indicator
				username.parent('div').addClass('has-error');
				$('#emailErrMsg').html(data.msg);
				$('#emailErrMsg').show();
				return;
			}
		});
	});
});
