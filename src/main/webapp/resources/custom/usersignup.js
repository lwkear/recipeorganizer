function checkPasswordScore() {
	//defaultOptions.ui.scores = [14, 26, 38, 50];
	
	var pswd = $('#password').val();
	var confirmPswd = $('#confirmPassword').val();
	
	//do not show warning if either are empty
	if (!pswd || !confirmPswd) {
		document.forms["formWithPswd"].submit();
		return;
	}
	
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
		rules: {
			activated: {
				wordNotEmail: true,
				wordLength: true,
				wordSimilarToUsername: false,
				wordSequences: true,
				wordTwoCharacterClasses: false,
				wordRepetitions: true,
				wordLowercase: true,
				wordUppercase: true,
				wordOneNumber: true,
				wordThreeNumbers: false,
				wordOneSpecialChar: true,
				wordTwoSpecialChar: false,
				wordUpperLowerCombo: false,
				wordLetterNumberCombo: false,
				wordLetterNumberCharCombo: false
			}
		},
		ui: {
			verdicts: [getMessage('pswd.verdict.weak'),
			           getMessage('pswd.verdict.normal'),
			           getMessage('pswd.verdict.medium'),
			           getMessage('pswd.verdict.strong'),
			           getMessage('pswd.verdict.verystrong')],
			errorMessages:
			{
				wordNotEmail: getMessage('pswd.wordNotEmail'),
				wordLength: getMessage('pswd.wordLength'),
				wordSequences: getMessage('pswd.wordSequences'),
				wordRepetitions: getMessage('pswd.wordRepetitions'),
				wordLowercase: getMessage('pswd.wordLowercase'),
				wordUppercase: getMessage('pswd.wordUppercase'),
				wordOneNumber: getMessage('pswd.wordOneNumber'),
				wordOneSpecialChar: getMessage('pswd.wordOneSpecialChar'),
				wordPassword: getMessage('pswd.wordPassword')
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
	$("#password").pwstrength("addRule", "wordPassword", function (options, word, score) {
		if (word.match(/password/ig))
			return score;
		return 0;},
		-100, true);
	
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
			url: appContextPath + '/lookupUser',
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
