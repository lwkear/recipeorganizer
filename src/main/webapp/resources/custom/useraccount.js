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
	if (score >= 38) {
		document.forms["formWithPswd"].submit();
		return;
	}
	
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

function clearField(fieldName) {
	$('#'+fieldName).val('');
	$('#'+fieldName).parent('div').removeClass('has-error');
	$('#'+fieldName+'ErrMsg').html('');
	$('#'+fieldName+'ErrMsg').hide();
}

function resetOptions() {
	var option = $('#hiddenEmailAdmin').val();
	var yesno = (option == "true" ? "Yes" : "No");
	$('#emailAdmin'+yesno).prop('checked', true);
	option = $('#hiddenEmailRecipe').val();
	yesno = (option == "true" ? "Yes" : "No");
	$('#emailRecipe'+yesno).prop('checked', true);
	option = $('#hiddenEmailMessage').val();
	yesno = (option == "true" ? "Yes" : "No");
	$('#emailMessage'+yesno).prop('checked', true);
}

function displayErrorPanel() {
	if ((!$('#firstNameErrMsg').is(':empty')) ||
		(!$('#lastNameErrMsg').is(':empty'))) {
		$('#panelName').collapse('show');;
	}
	if ((!$('#emailErrMsg').is(':empty')) ||
		(!$('#confirmEmailErrMsg').is(':empty'))) {
		$('#panelEmail').collapse('show');;
	}
	if ((!$('#currentPasswordErrMsg').is(':empty')) ||
		(!$('#passwordErrMsg').is(':empty')) ||
		(!$('#confirmPasswordErrMsg').is(':empty'))) {
		$('#panelPassword').collapse('show');;
	}
}

$(function() {

	displayErrorPanel();
	
	$('#password').pwstrength({
		common : {
			debug: false,
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
	
	//only show one of the panels at a time
	$('#changeGroup').on('show.bs.collapse', function () {
       	$('#changeGroup .in').collapse('hide');
    });
	
	$(document)
	.on('click', '#btnSubmitPassword', function(e){
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
		var newemail = $(this);

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
			newemail.parent('div').removeClass('has-error');
			$('#emailErrMsg').hide();
		})
		.fail(function(jqXHR, status, error) {
			var data = jqXHR.responseJSON;
			console.log('fail data.msg: '+ data.msg);

			//server sets CONFLICT error if name exists
			if (jqXHR.status == 409) {
				//set the error indicator
				newemail.parent('div').addClass('has-error');
				$('#emailErrMsg').html(data.msg);
				$('#emailErrMsg').show();
				return;
			}
		});
	})
	.on('click', '#linkName', function(e){
		clearField('firstName');
		clearField('lastName');
	})
	.on('click', '#linkEmail', function(e){
		clearField('email');
		clearField('confirmEmail');
	})
	.on('click', '#linkPassword', function(e){
		clearField('currentPassword');
		clearField('password');
		clearField('confirmPassword');
		if ($('#pswdScore').is(':visible')) {
			$('#password').pwstrength('forceUpdate');
		}
	})
	.on('click', '#linkNotify', function(e){
		resetOptions();
	})
	.on('click', '#btnCancelName', function(e){
		e.preventDefault();
		$('#panelName').collapse('hide');
		clearField('firstName');
		clearField('lastName');
	})
	.on('click', '#btnCancelEmail', function(e){
		e.preventDefault();
		$('#panelEmail').collapse('hide');
		clearField('email');
		clearField('confirmEmail');
	})
	.on('click', '#btnCancelPassword', function(e){
		e.preventDefault();
		$('#panelPassword').collapse('hide');
		clearField('currentPassword');
		clearField('password');
		clearField('confirmPassword');
	})
	.on('click', '#btnCancelNotification', function(e){
		e.preventDefault();
		$('#panelNotify').collapse('hide');
		resetOptions();
	})
});
