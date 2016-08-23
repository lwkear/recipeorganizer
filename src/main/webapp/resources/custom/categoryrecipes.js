$(function() {

	$('#categoryRecipeList').DataTable({
		lengthChange: false,
		responsive: true,
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
		columnDefs: [
		    {	targets: [0,2,3],
		    	visible: false
			},
			{	targets: [0,1,2,3],
				searchable: false
			}			
		]
	});
})
