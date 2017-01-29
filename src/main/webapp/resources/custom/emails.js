/***************************/
/*** get email functions ***/
/***************************/
function getFolder(folder) {
	$.ajax({
	    type: 'GET',
		contentType: 'application/json',
	    url: appContextPath + '/admin/getFolder',
		dataType: 'html',
		data: {"folderName":folder}
	})
	.done(function(data) {
		console.log('getFolder done');
		refreshTable(data)
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		if (data.result != null) {
			for (i=0;i<data.result.length;i++) {
				var errMsg = data.result[i].defaultMessage;
				var errField = data.result[i].field;
				var msgId = "#" + errField + "ErrMsg";
				$(msgId).html(errMsg);
			}			
		}
		else {
			postFailed(data.msg);
		}			
	});
}

function refreshTable(data) {
	var table = $('#emailList').DataTable();
	table.clear();
	table.destroy();
	$('#datatableSection').html(data);
	//easiest to just re-initialize the datatable
	initDatatable();
}

/*******************************/
/*** display email functions ***/
/*******************************/
//function displayEmail(data, index, count) {
function displayEmail(data, first, last) {
	var names = ""; 
	var display = "";
	var separator = "";
	
	var obj = data[11];
	if (!obj) {
		$('#to').html("");
		$('#toRecip').data('names', "");
	}
	else {
		names = JSON.parse(obj);
		display = "";
		separator = "";
		$.each(names, function(index, value) {
			display = display + separator + value.full;
			separator = "<br>";
		})
		$('#to').html(display);
		$('#toRecip').data('names', names);
	}
	
	obj = data[10];
	if (!obj) {
		$('#from').html("");
		$('#fromRecip').data('names', "");
	}
	else {
		names = JSON.parse(obj);
		display = "";
		separator = "";
		$.each(names, function(index, value) {
			display = display + separator + value.full;
			separator = "<br>";
		})
		$('#from').html(display);
		$('#fromRecip').data('names', names);
	}
	
	obj = data[12];
	if (!obj) {
		$('#cc').html("");
		$('#ccRecip').data('names', "");
		$('#ccRow').collapse('hide');
	}
	else {
		names = JSON.parse(obj);
		display = "";
		separator = "";
		$.each(names, function(index, value) {
			display = display + separator + value.full;
			separator = "<br>";
		})
		$('#cc').html(display);
		$('#ccRecip').data('names', names);
		$('#ccRow').collapse('show');
	}
	
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
		filenames.forEach(function(file) {
			var btn = "";
			var fileExt = file.toLowerCase();
			if (fileExt.includes('.pdf')	||
				fileExt.includes('.xls')	||
				fileExt.includes('.doc')	||
				fileExt.includes('.txt')) {
				var src = $('#imageSrc').val();
				src += "?id=" + timestamp + "&filename=" + file;
				btn = "<a class='btn btn-link' href='" + src + "' target='_blank' style='margin:0;padding:0'>" + file + "</a>";
			}
			else
				btn = "<button class='btn btn-link btn-image' style='margin:0;padding:0'>" + file + "</button>";
			displaynames += separator + btn;
			separator = "&nbsp;&nbsp;";
		})
		$('#attach').html(displaynames);
		$('#attachRow').collapse('show');
	} 
	else
		$('#attachRow').collapse('hide');

	if (first)
		$('#prevEmail').hide();
	else
		$('#prevEmail').show();
	if (last)
		$('#nextEmail').hide();
	else
		$('#nextEmail').show();
	
	if (!$('#emailSingle').is(':visible')) {
		$('#emailTable').collapse('hide');	
		$('#emailSingle').collapse('show');
	}
}

/****************************/
/*** send email functions ***/
/****************************/
function sendEmail(allRecips) {
	//TODO: enable reply all
	$('#replyEmail').tooltip("hide");
	var obj = $('#fromRecip').data();			//the from becomes the to in the reply
	$('#toReply').html(obj.names[0].full);
	$('#subjectReply').html($('#subject').html());
	var obj = $('#ccRecip').data();
	if (!obj.names)
		$('#ccReplyRow').collapse('hide');
	else {
		//TODO: show all names
		$('#ccReply').html(obj.names[0].full);
		$('#ccReplyRow').collapse('show');
	}
	$('#emailMsg').val("");
	$("#submitReply").on('click', postEmail);
	$("#cancelReply").on('click', function() {$("#emailReply").modal('hide');});
	$('#emailReply').modal({backdrop: 'static', keyboard: false, show: false});
	$('#emailReply').on('shown.bs.modal', function () {$(this).find('#emailMsg').focus()})
	$("#emailReply").on('hidden.bs.modal', function(){$("#submitReply").unbind('click');})
	$("#emailReply").modal('show');
}

//request server to send the email
function postEmail(e) {
	var obj = $('#fromRecip').data();	//the from becomes the to in the reply
	var to = obj.names;
	obj = $('#toRecip').data();
	var from = obj.names;				//the to becomes the from in the reply
	var cc = null;
	obj = $('#ccRecip').data();
	if (obj.names)
		cc = obj.names;
	var subject = $('#subject').html();
	var sent = $('#sent').html();
	var content = $('#content').html();
	var message = $('#emailMsg').val();
	message = $.trim(message);
	
	var data = {"from":from,"to":to,"cc":cc,"subject":subject,"sentDate":sent,"content":content,"message":message};
	var dataStr = JSON.stringify(data);

	$.ajax({
	    type: 'POST',
		contentType: 'application/json',
	    url: appContextPath + '/admin/sendEmail',
		dataType: 'json',
		data: dataStr
	})
	.done(function(data) {
		$("#emailReply").modal('hide');
		console.log('postShare done');
		/*var msg = getMessage('email.recipe.successful');
		var fmt = String.format(msg, recipientName);*/
		displayOKMsg("", "Email sent");
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		if (data.result != null) {
			for (i=0;i<data.result.length;i++) {
				var errMsg = data.result[i].defaultMessage;
				var errField = data.result[i].field;
				var msgId = "#" + errField + "ErrMsg";
				$(msgId).html(errMsg);
			}			
		}
		else {
			$("#emailReply").modal('hide');
			postFailed(data.msg);
		}			
	});
}

/******************************/
/*** delete email functions ***/
/******************************/
function deleteEmail(uid) {
	$('#delete' + uid).tooltip("hide")
	var folder = $('#currFolder').html();
	var emailUID = uid;

	$.ajax({
		type: 'POST',
		url: appContextPath + '/admin/deleteEmail',
		dataType: 'json',
		data: {"folderName":folder,"UID":emailUID}
	})
	.done(function(data) {
		console.log('delete email success');
		emailDeleted(emailUID);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}

function emailDeleted(uid) {
	var table = $('#emailList').DataTable();
	var row = table.row('#' + uid);
	var info = table.page.info();
	//must count only the displayed records
	var count = info.recordsDisplay;
	var page = info.page;
	var node = row.node();
	var index = node.rowIndex;
	
	if ($('#emailSingle').is(':visible')) {
		//more than a single row will be left
		if (count > 1) {
			//we're not on the first page
			if (page > 0) {
				var len = table.page.len();
				var position = (page * len) + index;
				//we're not on the last row of the page
				if (position != count) {
					$('#prevEmail').trigger('click');
					row.remove();
					table.draw(false);
				}
				else {
					table.$('tr').removeClass('default');
					var newrow;
					//we're not on the first row of the page
					if (index != 1)
						newrow = $(node).prev();
					else {
						//get the last row from the previous page
						table.page('previous').draw('page');
						newrow = $('tr:last');
					}					
					$(newrow).addClass('default');
					row.remove();
					table.draw(false);
					var data = table.row(newrow).data();
					displayEmail(data, false, true);
				}
			}
			else {
				table.$('tr').removeClass('default');
				var first = false;
				var last = false;
				var newrow;
				if (index == 1) {
					newrow = $(node).next();
					first = true;
				}
				else {
					newrow = $(node).prev();
					//last row?
					if (index == count)
						last = true;
					//second row?
					if (index == 2)
						first = true;
				}
				
				//only two rows left?
				if (count == 2) {
					first = true;
					last = true;
				}
				
				//highlight the next/prev row, delete the original row then display the next/prev row
				$(newrow).addClass('default');
				row.remove();
				table.draw(false);
				var data = table.row(newrow).data();
				displayEmail(data, first, last);
			}
		}
		else {
			row.remove();
			table.draw(false);
			$('#emailTable').collapse('show');
			$('#emailSingle').collapse('hide');			
		}
	}
	else {
		row.remove();
		table.draw(false);
	}
}

function initDatatable() {

	$('#emailList').DataTable({
    	columnDefs: [
    	    {visible: false, targets: [0,6,7,8,9,10,11,12]},
     		{width: "3%", targets: 1},
     		{width: "18%", targets: 2},
     		{width: "22%", targets: 3},
     		{width: "22%", targets: 4},
     		{width: "30%", targets: 5},
     		{width: "5%", targets: 13}
     	],
		searching: true,
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
}

function getAccounts() {
	var url = appContextPath + '/resources/email.json';
	$.getJSON(url)
		.done(function (data) {
			var item;
			$.each( data, function(ndx, account) {
				$('<li class="acct">')
					.data('fullname',account.name)
					.data('id',account.id)
					.html("<a href='#'><span id='acct" + account.id + "' class='glyphicon glyphicon-ok invisible'>&nbsp</span>" + account.shortName + "</a></li>")
					.appendTo('#accounts');
			});
	 	});
}

$(function() {
	
	//datatable initialization put into a function to enable destroying/reinitializing when a different folder is selected, 
	initDatatable();
	getAccounts();
	
	$('#closeEmail').on('click', function () {
		$(this).blur();
		$('#emailTable').collapse('show');	
		$('#emailSingle').collapse('hide');
	});
	$('#nextEmail').on('click', function () {
		var table = $('#emailList').DataTable();
		$(this).blur();
		//node returns a DOM object like $(this)
		var node = table.row('.default').node();
		//get the DOM row index (one-based)
		var index = node.rowIndex
		var len = table.page.len();
		if ((index+1) > len) {
			table.page('next').draw('page');
			index = 0;
		}
		//find the row in the table and fire the click event; must click on a specific column (see tr click event)
		//note: the DOM index is one-based, but the datatable index is zero-based, so do not add 1 to the index		
		$('#emailList').find('tbody tr:eq('+index+') td:first-child').trigger('click');
	});	
	$('#prevEmail').on('click', function () {
		var table = $('#emailList').DataTable();
		$(this).blur();
		//note: node returns a DOM object like $(this)
		var node = table.row('.default').node();
		//get the DOM row index (one-based)
		var index = node.rowIndex
		//note: the DOM index is one-based, but the datatable index is zero-based, so need to subtract 2 from the index		
		index = index - 2;
		var len = table.page.len();
		var info = table.page.info();
		var page = info.page;
		if ((page > 0) && (index < 0)) {
			table.page('previous').draw('page');
			index = len-1;
		}
		//find the row in the table and fire the click event; must click on a specific column (see tr click event)
		$('#emailList').find('tbody tr:eq('+index+') td:first-child').trigger('click');
	});

	$('#deleteEmail').on('click', function () {
		var table = $('#emailList').DataTable();
		$('#deleteEmail').tooltip("hide");
		var node = table.row('.default').node();
		//use the 'original' selector-modifier to get the row, regardless of the current sort order
		var index = table.rows({order: 'original'}).nodes().indexOf(node);
		var row = table.row(index);
		var data = row.data();
		var id = row.id();
		console.log("UID:" + id);
		deleteEmail(id);
	});
	$('#folderMenu li').click(function() {
		var selFolder = $(this).text();
		var currFolder = $('#currFolder').val();
		if (selFolder != currFolder) {
			$('*').css("cursor", "wait");
			getFolder(selFolder);
			$('#currFolder').html(selFolder);
			$('*').css("cursor", "default");
		}
	});
	
	$(document)
		.on('click', '.btn-image', function(e) {
			e.preventDefault();
			var name = $(this).text();
			var id = $('#fileId').val();
			var src = $('#imageSrc').val();
			src += "?id=" + id + "&filename=" + name;
			$('#image').attr('src', src);
			$('#imageModal').modal({backdrop: true, keyboard: true, show: false});
			$("#imageModal").modal('show');
		})
		.on('click', '#replyEmail', function(e) {
			e.preventDefault();
			sendEmail(false);
		})
		.on('click', '#replyEmailAll', function(e) {
			e.preventDefault();
			sendEmail(true);
		})
		.on('click', '#emailList tbody tr td:not(:last-child)', function(e) {
			var table = $('#emailList').DataTable();
			table.$('tr').removeClass('default');
			var node = table.row(this).node();
			$(node).addClass('default');
			if ($(node).hasClass('active'))
	            $(node).removeClass('active');
			//get the DOM row index (one-based)
			var index = node.rowIndex
			var len = table.page.len();
			var info = table.page.info();
			var page = info.page;
			var count = info.recordsDisplay;
			var first = false;
			var last = false;
			if (page === 0 && index == 1)
				first = true;
			if (((page*len)+index) === count) 
				last = true;
			var data = table.row(this).data();
			displayEmail(data, first, last);
		})
		.on('click', '#accounts li', function(e) {
			var account = $(this).text();
			account = account.trim();
			var id = $(this).data('id');
			if ($('#acct'+id).hasClass('invisible'))			
				$('#acct'+id).removeClass('invisible');
			else
				$('#acct'+id).addClass('invisible');
			var accounts = "";
			var separator = "";
			//loop through all the menu items and add any checked items to the search list
			$('.acct').each(function(ndx, account) {
				account = $(this).text();
				account = account.trim();
				var id = $(this).data('id');
				if (!$('#acct'+id).hasClass('invisible')) {			
					accounts += separator + account;
					separator = "|";
				}
			});
			var table = $('#emailList').DataTable();
			//clear the current search, if any
			table.columns(11).search('');
			//apply the new search, if any
			if (accounts.length)
				table.columns(11).search(accounts,true,false,true);
			table.draw();
		});
})
