function checkForFileError() {
	var photoErr = $("#photoErr").val();
	var name = $("#recipeName").val();
	var continueMsg = getMessage('exception.file.continuecancel');
	if (photoErr.length) {
		$("#messageTitle").text(getMessage('exception.file.failure'));
		$("#messageMsg").text(photoErr + " " + continueMsg);
		$(".msgDlgBtn").hide();
		$("#okBtn").show();
		$("#cnclBtn").show();
		$("#okBtn").one('click', saveRecipe);
		$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
		$("#messageDlg").on('hidden.bs.modal', function(){$("#okBtn").unbind('click');})
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
	$("#messageTitle").text(getMessage('common.cancel'));
	$("#messageMsg").text(getMessage('recipe.edit.cancel'));
	$(".msgDlgBtn").hide();
	$("#yesBtn").show();
	$("#noBtn").show();
	$("#yesBtn").one('click', continueEditCancel);
	$('#messageDlg').modal({backdrop: 'static', keyboard: false, show: false});
	$("#messageDlg").on('hidden.bs.modal', function(){$("#yesBtn").unbind('click');})
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
	    
	    //adjust source fields as necessary
	    adjustSourceFields();
	    		
		var option = $('input[name="photoOpts"]:checked').val();
		if (option == 'change') {
			var filename = $('#photoname').val();
			if (filename == null || filename == "") {
				displayOKMsg(getMessage('recipe.optional.photo'), getMessage('recipe.optional.photo.noneselected'));
				return false;
			}
		}
		else {
			if (option == 'remove') {
				var photo = $('#hiddenphoto').val();
				var prefix = $('#removePrefix').val();
				$('#hiddenphoto').val(prefix + photo);
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