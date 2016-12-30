function displayEmail(data, index, count) {
	$('#to').html(data[11]);
	$('#from').html(data[10]);
	$('#subject').html(data[5]);
	$('#sent').html(data[2].display);
	$('#content').html(data[6]);
	var hasAttachment = data[7];
	if (hasAttachment === "true") {
		var timestamp = data[8];
		var names = data[9];
		$('#fileId').val(timestamp);
		var filenames = names.split(',');
		var displaynames = "";
		var separator = "";
		filenames.forEach(function(file){
			var btn = "<button class='btn btn-link btn-image' style='margin:0;padding:0'>" + file + "</button>";	//btn-xs
			displaynames += separator + btn;
			separator = "&nbsp;&nbsp;";
		})
		$('#attach').html(displaynames);
		$('#attachRow').collapse('show');
	} 
	else
		$('#attachRow').collapse('hide');
	var cc = data[12];
	if (!cc)
		$('#ccRow').collapse('hide');
	else {
		$('#cc').html(cc);
		$('#ccRow').collapse('show');
	}
	var bcc = data[13];
	if (!bcc)
		$('#bccRow').collapse('hide');
	else {
		$('#bcc').html(bcc);
		$('#bccRow').collapse('show');
	}

	if (index === 0)
		$('#prevEmail').hide();
	else
		$('#prevEmail').show();
	if (index === (count-1))
		$('#nextEmail').hide();
	else
		$('#nextEmail').show();
	if (!$('#emailSingle').is(':visible')) {
		$('#emailTable').collapse('hide');	
		$('#emailSingle').collapse('show');
	}
}

$(function() {
	
	var table = $('#emailList').DataTable({
    	columnDefs: [
    		
    	],
    	columnDefs: [
    	    {visible: false, targets: [0,6,7,8,9,10,11,12,13]},
     		{width: "3%", targets: 1},
     		{width: "15%", targets: 2},
     		{width: "22%", targets: 3},
     		{width: "22%", targets: 4},
     		{width: "38%", targets: 5}
     	],
		searching: false,
		lengthChange: false,
		order: [2,'desc'],
		language : {
	    	emptyTable:     getMessage('emails.table.emptyTable'),
		    info:           getMessage('emails.table.info'),
		    infoEmpty:      getMessage('emails.table.infoEmpty'),
		    infoFiltered:	getMessage('emails.table.infoFiltered'),
		    lengthMenu:		getMessage('emails.table.lengthMenu'),
		    zeroRecords:	getMessage('emails.table.zeroRecords'),
		    search:			getMessage('common.table.search'),
		    paginate: {
		    	first:      getMessage('common.table.paginate.first'),
		    	last:       getMessage('common.table.paginate.last'),
		    	next:       getMessage('common.table.paginate.next'),
		    	previous:   getMessage('common.table.paginate.previous')
		    }
		},	
		createdRow: function( row, data, dataIndex ) {
			if ( data[0] == 'false') {
				$(row).addClass('active'); 
			}
		},
		//stateSave : true
	});
	
	$('#emailList tbody').on('click', 'tr', function () {
		table.$('tr').removeClass('default');
		$(this).addClass('default');
		if ($(this).hasClass('active'))
            $(this).removeClass('active');
		var index = table.rows({order: 'applied'}).nodes().indexOf(this);
		var count = table.data().count();
		var data = table.row(this).data();		
		displayEmail(data, index, count);
    });
	$('#closeEmail').on('click', function () {
		$('#closeEmail').tooltip("hide");
		$(this).blur();
		$('#emailTable').collapse('show');	
		$('#emailSingle').collapse('hide');
	});
	$('#nextEmail').on('click', function () {
		$('#nextEmail').tooltip("hide");
		$(this).blur();
		//note: node returns a DOM object like $(this)
		var node = table.row('.default').node();
		var index = table.rows({order: 'applied'}).nodes().indexOf(node);
		index = index + 1;
		var len = table.page.len();
		var info = table.page.info();
		var page = info.page;
		index = index - (page * len);
		if (index > (len-1)) {
			table.page('next').draw('page');
			index = 0;
		}
		//find the row in the table and fire the click event
		$('#emailList').find('tbody tr:eq('+index+')').trigger('click');
		//an alternate way to get the next row; 
		//(click event code would also be required to highlight the correct row and display the email)
		/*var row = table.rows(':eq('+index+')').nodes();*/
	});
	$('#prevEmail').on('click', function () {
		$('#prevEmail').tooltip("hide");
		$(this).blur();
		//note: node returns a DOM object like $(this)
		var node = table.row('.default').node();
		var index = table.rows({order: 'applied'}).nodes().indexOf(node);
		index = index - 1;
		var len = table.page.len();
		var info = table.page.info();
		var page = info.page;
		index = index - (page * len);
		if (index < 0) {
			table.page('previous').draw('page');
			index = len-1;
		}
		//find the row in the table and fire the click event
		$('#emailList').find('tbody tr:eq('+index+')').trigger('click');
		//an alternate way to get the next row;
		//(click event code would also be required to highlight the correct row and display the email)
		/*var row = table.rows(':eq('+index+')').nodes();*/
	});

	$(document)
		.on('click', '.btn-image', function(e) {
			e.preventDefault();
			var name = $(this).text();
			var id = $('#fileId').val();
			var src = $('#imageSrc').val();
			src += "?id=" + id + "&filename=" + name;
			$('#image').attr('src', src);
			$('#imageModal').modal({backdrop: 'static', keyboard: false, show: false});
			$("#imageModal").modal('show');
		});
})
