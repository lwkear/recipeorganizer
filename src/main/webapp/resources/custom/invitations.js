//shorthand for document.ready
$(function() {
	
	//these events must reside in $(document) because the dynamically added elements are not
	//visible to the DOM otherwise
	$(document)
		.on('click', '.addInvitation', function(e) {
			e.preventDefault();
			var currentEntry = $(this).parents('.inviteGrp:first');
			var	newEntry = $(currentEntry.clone()).insertAfter(currentEntry);
			newEntry.find('input').val('');
			newEntry.toggleClass('has-error', false);
			newEntry.find('.inviteErrGrp').hide();
			newEntry.find('.has-error').toggleClass('has-error', false);
			//show the "minus" button for the current entry
			currentEntry.find('.removeInvitation').show();
		})    
		.on('click', '.removeInvitation', function(e)
		{
			e.preventDefault();
			//remove the parent <div> of this ingredient
			$(this).closest('.inviteGrp').remove();
			return false;
		})
		.on('click', '#btnReset', function(e) {
			console.log("btnReset .on click in document");

			var numRows = $('.inviteGrp').length;
			console.log("#elements=" + numRows);

			$('.inviteGrp').each(function(index) {
				console.log("row#" + index);
				var currentRow = $(this);
				var resultbool = currentRow.find('.resultbool');
				var val = resultbool.val();
				if (val === "true") {
					if (index > 0)
						currentRow.remove();
					else {
						currentRow.find('.firstName').val("");
						currentRow.find('.lastName').val("");
						currentRow.find('.email').val("");
						currentRow.find('.result').html("");
					}
				}
			})

			numRows = $('.inviteGrp').length;
			console.log("#elements=" + numRows);
			
			//check if there were errors - if the first row was ok, then need to get rid of it
			if (numRows > 1) {
				var currentRow = $('.inviteGrp:first');
				var resultbool = currentRow.find('.resultbool');
				var val = resultbool.val();
				if (val === "true")
					currentRow.remove();
				currentRow = $('.inviteGrp:last');
				currentRow.find('.removeInvitation').hide();				
			}
			else {
				var currentRow = $('.inviteGrp:first');
				currentRow.find('.resultbool').val("");
				currentRow.find('.removeInvitation').hide();
			}
		})
		.on('click', '#btnSubmit', function(e) {
			console.log("btnSubmit .on click in document");

			var numRows = $('.inviteGrp').length;
			console.log("#elements=" + numRows);

			$('.inviteGrp').each(function(index) {
				console.log("row#" + index);
				var currentRow = $(this);
				currentRow.find('div').toggleClass('has-error', false);
				currentRow.find('.inviteErrGrp ').hide();
				var firstName = currentRow.find('.firstName').val();
				var lastName = currentRow.find('.lastName').val();
				var email = currentRow.find('.email').val()
				var result = currentRow.find('.result');
				var resultbool = currentRow.find('.resultbool');
				result.html('');
				console.log("first/last/email: " + firstName + '/' + lastName + '/' + email);
			
				var data = {"firstName":firstName,"lastName":lastName,"email":email};
			
				$.ajax({
				    contentType: 'application/json',
					type: 'POST',
					url: appContextPath + '/admin/sendInvitation',
					dataType: 'json',
					data: JSON.stringify(data)
				})
				.done(function(data) {
					console.log('user invited: ' + email);
					resultbool.val("true");
					result.html(data.msg);
				})
				.fail(function(jqXHR, status, error) {
					resultbool.val("false");
					var data = jqXHR.responseJSON;
					console.log('fail data: '+ data);
					if (data.result != null) {
						for (i=0;i<data.result.length;i++) {
							var errMsg = data.result[i].defaultMessage;
							var errField = data.result[i].field;
							var msgClass = "." + errField + "ErrMsg";
							var error = currentRow.find(msgClass);
							error.parents('div:first').toggleClass('has-error', true);
							$(error).html(errMsg).show();
						}			
					}
					else {
						result.html(data.msg);
					}			
				})
			})
		})
})
