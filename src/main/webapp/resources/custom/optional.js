//TODO: GUI: test for special characters
function setSourceUrl(url, query) {
	var source = $('#inputSource').val();
	var newurl = url + '?searchStr=' + query + '&type=' + source;
	return newurl;
};

remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getSources', null);
remoteOpts['replace'] = function(url, query) {return setSourceUrl(url, query);}; 
bhOpts = setBHOptions(50, null, null, null, remoteOpts);
var sourceBH = new Bloodhound(bhOpts);

remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getTags', null);
remoteOpts['replace'] = function(url, query) {return setTagsUrl(url, query);};
bhOpts = setBHOptions(50, null, null, null, remoteOpts);
var tagsBH = new Bloodhound(bhOpts);

function initSourceTA() {

	var options = initTypeaheadOptions(true,true,1);
	var dataset = initTypeaheadDataset('source', null, 20, sourceBH);
	$('.srcTA').typeahead(options,dataset);
};

function initTagsTA() {

	var options = initTypeaheadOptions(true,true,1);
	var dataset = initTypeaheadDataset('tags', null, 20, tagsBH);
	$('#inputTags').typeahead(options,dataset);
};

function initSource() {
	$('#inputBookName').val('');
	$('#inputBookPage').val('');
	$('#inputMagName').val('');
	$('#inputMagDate').val(null);
	$('#inputNewsName').val('');
	$('#inputNewsDate').val(null);
	$('#inputPersonName').val('');
	$('#inputWebURL').val('');
	$('#inputRecipeURL').val('');
	$('#inputOtherDetails').val('');
}

//if the form is returned with an error show the appropriate inputs
function setSource() {
	var ndx = $('#inputSource option:selected').index();
	//the first option is the placeholder
	if (ndx > 0) {
		var today = new Date();

		var option = $('#inputSource').val();
		if (option.length > 0) {
			$('.srcGroup').hide(); 
			if (option === 'Cookbook') {
				$('.bookGroup').show();			
			}		
			if (option === 'Magazine') {
				$('.magGroup').show();
				$('.magGroup').css('z-index','999999 !important');			
				$('.ui-datepicker').css('z-index','999999 !important');
			}		
			if (option === 'Newspaper') {
				$('.newsGroup').show();
				$('.newsGroup').css('z-index','999999 !important');
				$('.ui-datepicker').css('z-index','999999 !important');	
			}		
			if (option === 'Person') {
				$('.personGroup').show();			
			}		
			if (option === 'Website') {
				$('.webGroup').show();			
			}		
			if (option === 'Other') {
				$('.otherGroup').show();			
			}
			$('#inputSource').removeClass('select-placeholder');
		}
	}				
};

//shorthand for document.ready
$(function() {
	
	//TODO: GUI: replace these JQuery UI controls with Bootstrap controls?
	//TODO: GUI: try to get the calendar icon to appear
	$.datepicker.setDefaults({
		dateFormat: "mm/dd/yy",
		defaultDate: null,
		//buttonImage: "/recipeorganizer/resources/jqueryui-smoothness/images/calendar-icon.png",
		//showOn: "both",
	    beforeShow: function() {
	    	$(this).css("z-index", 999);	//bootstrap assigns a z-index of 2 to a form-control which hides the datepicker
	    }
	});
	
	$('#inputNewsDate').datepicker();
	$('#inputMagDate').datepicker();
	
	//TODO: GUI: typeahead for tagsinput doesn't always work the first time - an initialization issue?
	$('#inputTags').tagsinput({
		tagClass: function(item) {
	    	return 'label label-primary';
	    },
	    maxtags: 10,
	    maxchars: 25,
	    trimvalue: true,
	    typeaheadjs: [ 
			{
				hint: true,
				highlight: true,
				minLength: 1
			},
			{
				name: 'tags',
				limit: 20,
				source: tagsBH 
			}
		]
	});
	
	setSource();
	initSourceTA();
	initTagsTA();
	
	//these events must reside in $(document) because the dynamically added elements are not
	//visible to the DOM otherwise
	$(document)
		.on('click', '#review', function(e)
		{
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
					alert("New photo not selected");
					return false;
				}
			}
		})
		.on('click', '#back', function(e) {
			//set index for the last set of instructions, which will be total sections minus one
		    var ndx = $('#instructSections').val();
		    console.log("_eventId_back");
		    console.log("instructSects val:" + ndx);
		    var num = parseInt(ndx);
		    if (num > 0) {
			    num = num-1;
				console.log("currInstructSect new val:" + num);
				$('#currInstructSect').val(num.toString());
		    }
		})
		.on('click', 'input[name="photoOpts"]:checked', function(e)
		{
			if ($(this).val() == 'change') {
				$('.newphoto').show();
			}
			else
				$('.newphoto').hide();
		})
		.on('change', '.btn-file :file', function() {
			var input = $(this),
			label = input.val();
			input.trigger('fileselect', [label]);
		})

	/* http://www.abeautifulsite.net/whipping-file-inputs-into-shape-with-bootstrap-3/ */
	$('.btn-file :file').on('fileselect', function(event, label) {
		var input = $(this).parents('.input-group').find(':text');
		if (input.length)
			input.val(label);
    });		
	
	$('.webGroup').on('blur', function() {
		console.log("webGroup .on blur in document");
		var ndx = $('#inputSource option:selected').index();
		var url = $(this);
		if (ndx == 5) {
			var webURL = url.val();
			var pos = webURL.search('http');
			if (pos != 0) {
				webURL = "http://" + webURL;
				url.val(webURL);
			}
		}
	})	
	
	$('#inputSource').change(function() {
	
		//prepare a date in the correct format
		var d = new Date();
		var day = d.getDate();
		day = (day < 10) ? '0' + day : day;
		var mon = d.getMonth() + 1;
		mon = (mon < 10) ? '0' + mon : mon;
		var dateStr = mon + '/' + day + '/' + d.getFullYear();
	
		initSource();
		
		var option = $(this).val();
		$('.srcGroup').hide(); 
		if (option === 'Cookbook') {
			$('.bookGroup').show();			
		}		
		if (option === 'Magazine') {
			$('.magGroup').show();
			$( '#inputMagDate' ).val(dateStr);			
		}		
		if (option === 'Newspaper') {
			$('.newsGroup').show();
			$( '#inputNewsDate' ).val(dateStr);			
		}		
		if (option === 'Person') {
			$('.personGroup').show();			
		}		
		if (option === 'Website') {
			$('.webGroup').show();			
		}		
		if (option === 'Other') {
			$('.otherGroup').show();			
		}
		$(this).removeClass('select-placeholder');				
		});
})
