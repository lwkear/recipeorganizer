function checkForFileError() {
	var photoErr = $("#photoErr").val();
	var name = $("#recipeName").val();
	if (photoErr.length) {
		$("#messageTitle").text(messageMap.get('exception.file.failure'));
		$("#messageMsg").text(photoErr);
		$(".msgDlgBtn").hide();
		$("#okBtn").show();
		$("#cnclBtn").show();
		$("#okBtn").one('click', saveRecipe);
		$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
		$("#messageDlg").modal('show');
	}
}

function saveRecipe(e) {
	$("#messageDlg").modal('hide');
	//remove the file object
	$("#file").val("");
	$("#photoname").val("");
	document.forms["editForm"].submit();
}

function cancelEditRecipe() {
	$("#messageTitle").text(messageMap.get('common.cancel'));
	$("#messageMsg").text(messageMap.get('recipe.edit.cancel'));
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', continueEditCancel);
	$("#messageDlg").modal('show');
} 

function continueEditCancel(e) {
	$("#messageDlg").modal('hide');
	$("#cancelEditBtn")[0].click();
}

$(function() {
	
	checkForFileError();
	
	$(document)
	.on('click', '#save', function(e)
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
		
		num = $('#instructSections').val();
		if (!num || !num.length || num === "0")
			$('#instructSections').val(1);

		//remove the typeahead html added to the form which included the class w/o the name attribute 
	    $('.ingredQtyType').typeahead('destroy');
	    $('.ingredQual').typeahead('destroy');
	    $('.ingredDesc').typeahead('destroy');
	
		//remove rows that are completely empty
	    var fields = ['.ingredQty','.ingredQtyType','.ingredDesc','.ingredQual'];
	    removeEmptyRows('.ingredGrp',fields);
	    
		//each of the individual elements require their array indexes to be set/reset
	    num = $('#ingredSections').val();
	    for (i=0; i<num; i++) {
	    	var panel = '#ingredpanel' + i;
		    fixIngredArrayIndexes(panel + ' .recipeIngredID', false);
		    fixIngredArrayIndexes(panel + ' .ingredID', false);
			fixIngredArrayIndexes(panel + ' .ingredQty', false);
			fixIngredArrayIndexes(panel + ' .ingredQtyType', false);
			fixIngredArrayIndexes(panel + ' .ingredQual', false);
			fixIngredArrayIndexes(panel + ' .ingredSeq', true);
	    }

		//set new row id's to zero to avoid empty string conversion error
	    $('.recipeIngredID').each(function() {
			var id = $(this).val();
			if (id == "")
				$(this).val(0);
		});	    
	    $('.instructId').each(function() {
			var id = $(this).val();
			if (id == "")
				$(this).val(0);
		});	    
	    
		fields = ['.instructDesc'];
	    removeEmptyRows('.instructGrp',fields);
	    
		//each of the individual elements require their array indexes to be set/reset
	    num = $('#instructSections').val();
	    for (i=0; i<num; i++) {
	    	var panel = '#instructpanel' + i;
	    	fixInstructArrayIndexes(panel + ' .instructId', false);
	    	fixInstructArrayIndexes(panel + ' .instructDesc', false);
	    	fixInstructArrayIndexes(panel + ' .instructSeq', true);
	    }
	    
		var ndx = $('#inputSource option:selected').index();
		if (ndx == 0 || ndx == 7) {
			$('#inputSource').val("");
		}
		var page = $('#inputBookPage').val();
		if (page == null)
			$('#inputBookPage').val(0);
		
		var option = $('input[name="photoOpts"]:checked').val();
		if (option == 'change') {
			var filename = $('#photoname').val();
			if (filename == null || filename == "") {
				displayOKMsg(messageMap.get('recipe.optional.photo'), messageMap.get('recipe.optional.photo.noneselected'));
				return false;
			}
		}
		else {
			if (option == 'remove') {
				var photo = $('#hiddenphoto').val();
				$('#hiddenphoto').val('xxxREMOVExxx' + photo);
			}
		}

		var text = $('#inputTags option:selected').map(function () {
			return $(this).text();}).get();
		$('#hiddentags').val(text);
	})
	.on('click', '#fakeEditCancel', function(e)
	{
		e.preventDefault();
		cancelEditRecipe();
	})
})