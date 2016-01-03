function fixInstructArrayIndexes(element, sequence) {
	console.log("fixArray:" + element);
	$(element).each(function(index) {
		$(this).attr('name',$(this).attr('name').replace(/instructions\[[0-9]+\]/,'instructions['+index+']'));
		if (sequence) {
			$(this).val(index+1);
		}
	});
};

//shorthand for document.ready
$(function() {
	
	//these events must reside in $(document) because the dynamically added elements are not
	//visible to the DOM otherwise
	$(document)
		.on('click', '.addInstruction', function(e) {
			e.preventDefault();
			//see addIngredient notes above 
			var currentEntry = $(this).parents('.instructGrp:first');
			var	newEntry = $(currentEntry.clone()).insertAfter(currentEntry);
			newEntry.find('input').val('');
			newEntry.find('textarea').val('');
			newEntry.removeClass('has-error');
			newEntry.find('.instructErr').hide();
			currentEntry.find('.removeInstruction').show();		
		})    
		.on('click', '.removeInstruction', function(e)
		{
			e.preventDefault();
			//remove the parent <div> of this ingredient
			$(this).closest('.instructGrp').remove();
			return false;
		})
		.on('click', '.row-adjust', function(e) {
			console.log("row-adjust .on click in document");
	
			var fields = ['.instructDesc'];
		    removeEmptyRows('.instructGrp',fields);
		    
			//each of the individual elements require their array indexes to be set/reset
		    fixInstructArrayIndexes('.instructId', false);
		    fixInstructArrayIndexes('.instructDesc', false);			
		    fixInstructArrayIndexes('.instructSeq', true);

		    $('.instructId').each(function() {
				var id = $(this).val();
				if (id == "")
					$(this).val(0);
			});	    
		    
		    var name = $(this).attr('name');
		    if (name == '_eventId_back') {
		    	console.log("_eventId_back");
				//set index for the last set of ingredients, which will be total sections minus one
		    	var currSect = $('#currInstructSect').val();
		    	if (currSect === '0') {
				    var ndx = $('#ingredSections').val();
				    console.log("ingredSects val:" + ndx);
				    var num = parseInt(ndx);
				    if (num > 0) {
					    num = num-1;
						console.log("currIngredSect new val:" + num);
						$('#currIngredSect').val(num.toString());
				    }
		    	}
		    }
		})
})

