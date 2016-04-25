function setCategory() {
	var id = $('#catId').val();
	console.log("catId=" + id);
	if (id > 0) {
		$("#inputCategory option[data-id='" + id + "']").prop('selected',true);
		$("#inputCategory").trigger("change");
	}			
}

$(function() {

	//get categories and set the options
	$.getJSON("/recipeorganizer/admin/getCategories")
		.done(function (data) {
			$.each(data, function (index, item) {
		    	$('#inputCategory').append(
		        	$('<option>')
		        		.val(item.name)
		        		.html(item.name)
		        		.attr('data-id',item.id)
				);
			});
			setCategory();
	 	});
	
	$('#inputCategory').change(function() {	
		var entry = $(this).find(':selected');
		var id = entry.attr("data-id");
		var name = entry.val();
		$('#catId').val(id);
		$('#name').val(name);
		$(this).removeClass('select-placeholder');
	});
})
