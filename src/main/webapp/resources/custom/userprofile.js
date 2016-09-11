function stateDatumToken(datum) {
	return Bloodhound.tokenizers.whitespace(datum.name);
};

function statePrefetchFilter(data) {
	return $.map(data, function (data) {
        return {
            name : data.name,
        	abbrev : data.abbreviation
        };
    });
};

var prefetchOpts = setBHPrefetchOpts(false, appContextPath + '/resources/states.json', statePrefetchFilter);
var bhOpts = setBHOptions(50, stateDatumToken, null, prefetchOpts, null);
var statesBH = new Bloodhound(bhOpts);

function initStatesTA() {
	var options = initTypeaheadOptions(true,true,1);
	var dataset = initTypeaheadDataset('states', 'name', 50, statesBH);
	$('#state').typeahead(options,dataset);
};

function checkAvatarOptions() {
	var opt = $('input[name="photoOpts"]:checked').val();
	if (opt == 'remove') {
		var photo = $('#avatar').val();
		var prefix = $('#removePrefix').val();
		$('#avatar').val(prefix + photo);
	}
	if (opt == 'change') {
		var filename = $('#photoname').val();
		if (filename == null || filename == "") {
			displayOKMsg(getMessage('profile.image'), getMessage('profile.image.noneselected'));
			return false;
		}
	}	
};

function checkForFileError() {
	var avatarErr = $("#avatarErr").val();
	if (avatarErr.length) {
		$("#messageTitle").text(getMessage('exception.file.failure'));
		$("#messageMsg").text(avatarErr);
		$(".msgDlgBtn").hide();
		$("#okBtn").show();
		$("#cnclBtn").show();
		$("#okBtn").one('click', saveProfile);
		$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
		$("#messageDlg").on('hidden.bs.modal', function(){$("#okBtn").unbind('click');})
		$("#messageDlg").modal('show');
	}
}

function setVoice() {
	var audioOn = canPlay();
	if (!audioOn) {
		$('#selVoice').prop('disabled', true);
		$('.audioBtn').prop('disabled', true);
		$('#noAudioMsg').prop('hidden', false);
		return;
	}
	
	var ndx = $('#selVoice option:selected').index();
	//the first option is the placeholder
	if (ndx > 0) {
		$("#selVoice").removeClass('select-placeholder');
		$('.audioBtn').prop('disabled', false);
	}
}

function saveProfile(e) {
	$("#messageDlg").modal('hide');
	//remove the file object
	$("#file").val("");
	$("#photoname").val("");
	document.forms["profileForm"].submit();
}

//shorthand for document.ready
$(function() {

	initStatesTA();
	checkForFileError();
	setVoice();

	$(document)
		.on('click', 'input[name="photoOpts"]:checked', function(e)
		{
			if ($(this).val() == 'change') {
				$('.newphoto').show();
			}
			else
				$('.newphoto').hide();
		})
		.on('change', '.btn-file :file', function() {
			var input = $(this),
			label = input.val();
			input.trigger('fileselect', [label]);
		})
		.on('change', '#selVoice', function() {
			$(this).removeClass('select-placeholder');
			$('.audioBtn').prop('disabled', false);
		})	
		.on('click', '.audioBtn', function(e)
		{
			$(this).blur();
			$(this).tooltip("hide");
			var audio = $('#sampleVoice').get(0);
			var desc = $('#selVoice').val();
			audio.setAttribute('src', appContextPath + '/getSample?voiceName=' + desc);
			audio.play();
		});
	
	$('.btn-file :file').on('fileselect', function(event, label) {
		var input = $(this).parents('.input-group').find(':text');
		if (input.length)
			input.val(label);
    });
});
