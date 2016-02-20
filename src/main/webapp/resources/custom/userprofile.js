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

var prefetchOpts = setBHPrefetchOpts(false, '/recipeorganizer/resources/states.json', statePrefetchFilter);
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
		$('#avatar').val('xxxREMOVExxx' + photo);
	}
	if (opt == 'change') {
		var filename = $('#photoname').val();
		if (filename == null || filename == "") {
			displayOKMsg(messageMap.get('profile.photo'), messageMap.get('recipe.optional.photo.noneselected'));
			return false;
		}
	}	
};

function checkForFileError() {
	var avatarErr = $("#avatarErr").val();
	if (avatarErr.length) {
		$("#messageTitle").text(messageMap.get('exception.file.failure'));
		$("#messageMsg").text(avatarErr);
		$(".msgDlgBtn").hide();
		$("#okBtn").show();
		$("#cnclBtn").show();
		$("#okBtn").one('click', saveProfile);
		$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
		$("#messageDlg").modal('show');
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
		});
	
	$('.btn-file :file').on('fileselect', function(event, label) {
		var input = $(this).parents('.input-group').find(':text');
		if (input.length)
			input.val(label);
    });		
});
