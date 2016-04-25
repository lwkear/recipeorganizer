//TODO: GUI: test for special characters
var typeCookbook = $('#typeCookbook').val();
var typeMagazine = $('#typeMagazine').val();
var typeNewspaper = $('#typeNewspaper').val();
var typePerson = $('#typePerson').val();
var typeWebsite = $('#typeWebsite').val();
var typeOther = $('#typeOther').val();		

function setSourceUrl(url, query) {
	var source = $('#inputSource').val();
	var newurl = url + '?searchStr=' + query + '&type=' + source;
	return newurl;
};

remoteOpts = setBHRemoteOpts(false, '%QUERY', '/recipeorganizer/recipe/getSources', null);
remoteOpts['replace'] = function(url, query) {return setSourceUrl(url, query);}; 
bhOpts = setBHOptions(50, null, null, null, remoteOpts);
var sourceBH = new Bloodhound(bhOpts);

function initSourceTA() {
	var options = initTypeaheadOptions(true,true,1);
	var dataset = initTypeaheadDataset('source', null, 20, sourceBH);
	$('.srcTA').typeahead(options,dataset);
};

function initSource() {
	$('#coookbook').val('');
	$('#cookbookPage').val('');
	$('#magazine').val('');
	$('#magazineDate').val(null);
	$('#altMagDate').val(null);
	$('#newspaper').val('');
	$('#newspaperDate').val(null);
	$('#altNewsDate').val(null);
	$('#person').val('');
	$('#websiteUrl').val('');
	$('#recipeUrl').val('');
	$('#other').val('');
};

//if the form is returned with an error show the appropriate inputs
function setSource() {
	var ndx = $('#inputSource option:selected').index();
	//the first option is the placeholder
	if (ndx > 0) {
		var option = $('#inputSource').val();
		if (option.length > 0) {
			$('.srcGroup').hide(); 
			if (option === typeCookbook) {
				$('.bookGroup').show();			
			}		
			if (option === typeMagazine) {
				$('.magGroup').show();
				$('.magGroup').css('z-index','999999 !important');			
				$('.ui-datepicker').css('z-index','999999 !important');
			}		
			if (option === typeNewspaper) {
				$('.newsGroup').show();
				$('.newsGroup').css('z-index','999999 !important');
				$('.ui-datepicker').css('z-index','999999 !important');	
			}		
			if (option === typePerson) {
				$('.personGroup').show();			
			}		
			if (option === typeWebsite) {
				$('.webGroup').show();			
			}		
			if (option === typeOther) {
				$('.otherGroup').show();			
			}
			$('#inputSource').removeClass('select-placeholder');
		}
	}				
};

function adjustSourceFields() {
	var ndx = $('#inputSource option:selected').index();
	var option = "";
	if (ndx == 0 || ndx == 7)
		$('#inputSource').val("");
	else
		option = $('#inputSource').val();
	
	if (option !== typeCookbook) {
		$('#coookbook').val('');			
		$('#cookbookPage').val(0);
	}
	else {
		var page = $('#cookbookPage').val();
		if (!page)
			$('#cookbookPage').val(0);
	}
	if (option !== typeMagazine) {
		$('#magazine').val('');
		$('#magDate').val('');
		$('#magazineDate').val(null);
		$('#altMagDate').val(null);
	}
	else {
		var selDate = $('#altMagDate').val();
		$('#magDate').val(selDate);
	}
	if (option !== typeNewspaper) {
		$('#newspaper').val('');
		$('#newsDate').val('');
		$('#newspaperDate').val(null);
		$('#altNewsDate').val(null);
	}
	else {
		var selDate = $('#altNewsDate').val();
		$('#newsDate').val(selDate);
	}
	if (option !== typePerson) {
		$('#person').val('');
	}
	if (option !== typeWebsite) {
		$('#websiteUrl').val('');
		$('#recipeUrl').val('');
	}
	if (option !== typeOther) {
		$('#other').val('');			
	}
}

function getDateFormat() {
	var locale = $('#localeCode').val();
	if (locale == 'en')
		return "mm/dd/yy";
	if (locale == 'fr')
		return "dd/mm/yy";
};

//shorthand for document.ready
$(function() {

	var locale = $('#localeCode').val();
	$('#magazineDate').datepicker($.datepicker.regional[locale])
		.datepicker("option", {
			defaultDate: null,
			dateFormat: getDateFormat(),
			altFormat: "mm/dd/yy",
			altField: '#altMagDate',
			//bootstrap assigns a z-index of 2 to a form-control which hides the datepicker
			beforeShow: function() {$(this).css("z-index", 999);}
	});

	$('#newspaperDate').datepicker($.datepicker.regional[locale])
		.datepicker("option", {
			defaultDate: null,
			dateFormat: getDateFormat(),
			altFormat: "mm/dd/yy",
			altField: '#altNewsDate',
			//bootstrap assigns a z-index of 2 to a form-control which hides the datepicker
			beforeShow: function() {$(this).css("z-index", 999);}
	});
	
	var magDate = $('#magDate').val();
	if (magDate.length > 0)
		$('#magazineDate').datepicker('setDate', new Date(magDate));
	var newsDate = $('#newsDate').val();
	if (newsDate.length > 0)
		$('#newspaperDate').datepicker('setDate', new Date(newsDate));
	
	var $select = $('#inputTags').selectize({
		delimiter: ',',
		maxItems: 5,
	    persist: true,
	    diacritics: true,
	    labelField: "item",
	    valueField: "item",
	    preload: true,
	    create: true,
	    createOnBlur: true,
	    onLoad : function(data) {
	    	console.log("onLoad:"+data);
	    	var obj = $(this);
	    	var selTags = $("#hiddentags").val();
	    	if (selTags.length > 0) {
	    		var newtags = selTags.replace(",","],[");
	    		var arr = JSON.parse("[" + newtags + "]");
	    		obj[0].setValue(arr);
	    	}
	    }
	});
	
	var selectize = $("#inputTags")[0].selectize;
	selectize.load(function(callback) {
		var userId = $('#userID').val();
		var url = '/recipeorganizer/recipe/getTags' + '?userId=' + userId;
		var tags = null;
		$.getJSON(url)
			.done(function (data) {
				console.log("json:"+data);
				tags = data.map(function(x) { return { item: x }; });
				console.log("mapped json:"+tags);
				callback(tags);
		 	});
	});

	setSource();
	initSourceTA();
	
	//these events must reside in $(document) because the dynamically added elements are not
	//visible to the DOM otherwise
	$(document)
		.on('click', '#review', function(e)
		{
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
			if (option == 'remove')
				$('#hiddenphoto').val("");					
			}

			var text = $('#inputTags option:selected').map(function () {
				return $(this).text();}).get();
			$('#hiddentags').val(text);

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
	});	
	
	$('#inputSource').change(function() {

		//prepare a date in the correct format
		var d = new Date();
		var day = d.getDate();
		day = (day < 10) ? '0' + day : day;
		var mon = d.getMonth() + 1;
		mon = (mon < 10) ? '0' + mon : mon;
		
		var locale = $('#localeCode').val();
		if (locale == 'en')
			var dateStr = mon + '/' + day + '/' + d.getFullYear();
		if (locale == 'fr')
			var dateStr = day + '/' + mon + '/' + d.getFullYear();
	
		var option = $(this).val();
		$('.srcGroup').hide(); 
		if (option === typeCookbook) {
			$('.bookGroup').show();
			$("#cookbook").focus();
		}		
		if (option === typeMagazine) {
			$('.magGroup').show();
			var currDate = $('#magDate').val();
			if (!currDate) {
				$('#magazineDate' ).val(dateStr);
				$('#altMagDate').val(dateStr);
			}
			$("#magazine").focus();
		}		
		if (option === typeNewspaper) {
			$('.newsGroup').show();
			var currDate = $('#newsDate').val();
			if (!currDate) {
				$('#newspaperDate' ).val(dateStr);
				$('#altNewsDate').val(dateStr);
			}
			$("#newspaper").focus();
		}		
		if (option === typePerson) {
			$('.personGroup').show();
			$("#person").focus();
		}		
		if (option === typeWebsite) {
			$('.webGroup').show();
			$("#websiteUrl").focus();
		}		
		if (option === typeOther) {
			$('.otherGroup').show();
			$("#other").focus();
		}
		$(this).removeClass('select-placeholder');				
	});
})
