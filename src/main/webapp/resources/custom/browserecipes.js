var divMedia = "<div class='media'>";
var divMediaLeft = "<div class='media-left'>";
var divMediaBody = "<div class='media-body'>";
var divMediaHeading = "<h4 class='media-heading std-blue'>";
var divStyleBlack = "<div style='color:black'>";
var linkHref = "<a href='viewRecipe/";
var endDiv = "</div>";
var endHeading = "</h4>";
var endHref = "</a>";
var endTag = "'>";

var noPhoto = getMessage('common.photo.nophoto');	
	
function assembleColumn(val) {
	var str = divMedia + divMediaLeft; 
	str = str + linkHref + val.id + endTag;
	str = str + "<img id='image" + val.id + "' style='width:96px' ";
	if (val.photo)
		str = str + " class='media-object' src='photo?id=" + val.id + "&" + "filename=" + val.photo + "' alt='" + noPhoto + endTag + endHref + endDiv;
	else
		str = str + " class='media-object blankImage' alt='No photo' data-src='holder.js/96x96?auto=yes&text=" + noPhoto + endTag + endHref + endDiv;
	str = str + divMediaBody;
	str = str + linkHref + val.id + endTag;
	str = str + divMediaHeading + val.name + endHeading + endHref;
	str = str + divStyleBlack + val.description + endDiv;
	str = str + endDiv;
	str = str + endDiv;
	return str;
}

function drawBlanks() {
	Holder.run({
		images: '.blankImage'
	});
}

$(function() {

	var recipeTable = $('#categoryRecipeList').DataTable({
		ajax: {
			type: 'GET',
			url: appContextPath + '/recipe/categoryRecipes',
			dataType: 'json',
			data: function(d) {
				var currId = $("#currentCategoryId").val();
				d.categoryId = parseInt(currId);
			},
			dataSrc: ''
		},
		drawCallback: function(settings) {
			console.log('drawCallback');
			drawBlanks();
		},
		lengthChange: false,
		responsive: false,
		language: {
	    	emptyTable:     getMessage('recipe.table.emptyTable'),
		    info:           getMessage('recipe.table.info'),
		    infoEmpty:      getMessage('recipe.table.infoEmpty'),
		    infoFiltered:	getMessage('recipe.table.infoFiltered'),
		    lengthMenu:		getMessage('recipe.table.lengthMenu'),
		    zeroRecords:	getMessage('recipe.table.zeroRecords'),
		    search:			getMessage('common.table.search'),
		    paginate: {
		    	first:      getMessage('common.table.paginate.first'),
		    	last:       getMessage('common.table.paginate.last'),
		    	next:       getMessage('common.table.paginate.next'),
		    	previous:   getMessage('common.table.paginate.previous')
		    }
		},	
		stateSave: true,
		columns: [
			{ "data": "name", "className": "dt_col_hide"},
		    { "data": "description",
			  "render": function(row, type, val, meta) {
				  return assembleColumn(val);
			  },
		      "searchable": false
		    },
		    { "data": "photo", "className": "dt_col_hide"},
		    { "data": "views", "className": "dt_col_hide"},
			{ "data": "submitted", "className": "dt_col_hide"}
		]		
	});

	$('#sortOption').change(function() {	
		var entry = $(this).find(':selected');
		var option = entry.attr("data-id");
		if (option == 0)
			//order by name, date
			recipeTable.order([0, 'asc'],[4, 'desc']).draw();
		else
		if (option == 3)
			//order by views, name
			recipeTable.order([3, 'desc'],[0, 'asc']).draw();
		else
		if (option == 4)
			//order by date, name
			recipeTable.order([4, 'desc'],[0, 'asc']).draw();
	});
	
	$(document)
		.on('click', '.category', function(e) {
			var id = e.target.id;
			var link = $('#'+id);
			var catId = link.attr("data-id");
			console.log('catId:' + catId);
			$("#currentCategoryId").val(catId);
			recipeTable.ajax.reload();
			$('#sortOption option[data-id=0]').prop('selected',true);
			$("#sortOption").trigger("change");
		});
})
