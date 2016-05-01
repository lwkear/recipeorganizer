function getQuestions(topicId) {
	$.ajax({
	    type: 'GET',
		contentType: 'application/json',
	    url: '/recipeorganizer/questions/' + topicId,
		dataType: 'html',
	})
	.done(function(data) {
		console.log('getQuestions done');
		$('#questionsSection').html(data);
	})
	.fail(function(jqXHR, status, error) {
		var data = jqXHR.responseJSON;
		console.log('fail data: '+ data);
		postFailed(data.msg);
	});
}
