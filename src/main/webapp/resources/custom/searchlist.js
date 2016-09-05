function setCheckmarks(searchStr) {
	//the string looks like this: ^(cat174|cat241|cat242)$
	//need to remove the special characters, then create an array
	var str = searchStr.replace(/[\^\(\)\$]/g,'');
	console.log("regex:" + str);
	var srchArray = str.split('|');
	console.log("srchArray:" + srchArray);
	//loop through the array and check the appropriate checkboxes
	$.each(srchArray, function(index, value) {
		console.log("ndx/str:" + index + "/" + value);
		$("#" + value).attr('checked', true);
	});

}

$(function() {

	resultsTable = $('#recipeList')
		.on('stateLoaded.dt', function(e, settings, data) {
			console.log("stateLoaded.dt: " + data.columns[2].search.search);
			var searchStr = data.columns[2].search.search;
			if (searchStr.length > 0)
				setCheckmarks(searchStr);
			var searchStr = data.columns[3].search.search;
			if (searchStr.length > 0)
				setCheckmarks(searchStr);
		})
		.DataTable({
			lengthChange: false,
			language : {
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
			stateSave: false,
			stateLoadParams: function (settings, data) {
				var newSearch = $('#newSearch').val();
				//returning false should abort the process of loading the state
				//this is necessary because the state may include checked categories from the previous search
				if (newSearch === "true")
					return false;
				else
					return true;
			},
			columnDefs: [
			    {	targets: [0,2,3],
			    	visible: false
				},
				{	targets: [0,1],
					searchable: false
				},
				{	targets: [2,3],
					searchable: true
				}			
			]
		});
	
	$('.category').on('click', function() {
		var map = new Array();
		//go through the categories and build an array of id's for those that are checked
		$('.category').each(function() {
			var checked = $(this).is(':checked');
			if (checked == true) {
				var id = $(this).attr('id');
				map.push(id);
			}
		});
		
		if (map.length > 0)
			resultsTable.column([2]).search( '^(' + map.join('|') + ')$', true, false ).draw();
		else
			resultsTable.column([2]).search('').draw();
	});

	$('.source').on('click', function() {
		var map = new Array();
		//go through the categories and build an array of id's for those that are checked
		$('.source').each(function() {
			var checked = $(this).is(':checked');
			if (checked == true) {
				var id = $(this).attr('id');
				map.push(id);
			}
		});
		
		if (map.length > 0)
			resultsTable.column([3]).search( '^(' + map.join('|') + ')$', true, false ).draw();
		else
			resultsTable.column([3]).search('').draw();
	});
	
	$('#btnReset').on('click', function() {
		$('.category').each(function() {
			$(this).attr('checked', false);
		});
		$('.source').each(function() {
			$(this).attr('checked', false);
		});
		resultsTable.column([2]).search('');
		resultsTable.column([3]).search('');
		resultsTable.draw();
		blurInputFocus();
	});
})
