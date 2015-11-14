function fixArrayIndexes(element, sequence) {
	console.log("fixArray:" + element);
	$(element).each(function(index) {
		var str = $(this).attr('name');
		$(this).attr('name',$(this).attr('name').replace(/instructions\[[0-9]+\]/,'instructions['+index+']'));
		if (sequence) {
			$(this).val(index+1);
		}
	});
};

function removeEmptyRows(element, fields) {
	console.log("removeEmptyRows");
	console.log("element=" + element);
	$(element).each(function(index) {
		console.log("row#" + index);
		var currentRow = $(this);
		var notEmpty = false;
		for	(ndx = 0; ndx < fields.length; ndx++) {
			console.log("field[" + ndx + "]=" + fields[ndx]);
			var str = currentRow.find(fields[ndx]).val().trim();
			if (str.length > 0) {
				console.log("notempty");
				notEmpty = true;
				break;
			}
		}
		if (notEmpty === false) {
			console.log("removing row#" + index);
			currentRow.remove();
		}									
	});		
}

//shorthand for document.ready
$(function() {

//these events must reside in $(document) because the dynamically added elements are not
//visible to the DOM otherwise
$(document)
	.on('click', '.addInstruction', function(e) {
		e.preventDefault();
		//see addIngredient notes above 
		var currentEntry = $(this).parents('.form-group:first');
		var	newEntry = $(currentEntry.clone()).insertAfter(currentEntry);
		newEntry.find('textarea').val('');
		newEntry.removeClass('has-error');
		newEntry.find('.instructErr').hide();
		currentEntry.find('.removeInstruction').show();		
	})    
	.on('click', '.removeInstruction', function(e)
	{
		e.preventDefault();
		//there are 2 separate <div> sections that need to be removed
		$(this).parents('.form-group:first').prev('.instructErr').remove();
		$(this).parents('.form-group:first').remove();
		return false;
	})
	.on('click', '.row-adjust', function(e) {
		console.log("row-adjust .on click in document");

		var fields = ['.instructDesc'];
	    removeEmptyRows('.instructGrp',fields);
	    
		//each of the individual elements require their array indexes to be set/reset
	    /*fixArrayIndexes('.instructId', false);*/
		fixArrayIndexes('.instructDesc', false);
	    fixArrayIndexes('.instructSeq', true);		
	})
})

