$(function() {
	blurInputFocus();
	
	var accountKey = $('#accountKey').val();
	if (accountKey === "true")
		$('#btnAccountKey').prop('disabled', true);
	var domainKey = $('#domainKey').val();
	if (domainKey === "true")
		$('#btnDomainKey').prop('disabled', true);
	var registered = $('#registered').val();
	if (registered === "true")
		$('#btnRegister').prop('disabled', true);
	var agreement = $('#agreement').val();
	if (agreement === "true")
		$('#btnAcceptTOS').prop('disabled', true);
	var challengeDone = true;
    $('.challengedState').each(function() {
		var state = $(this).html();
		if (state === "No" || state === "Non")
			challengeDone = false;
	});	    
	var certificate = $('#certificate').val();
	if (certificate === "true" || challengeDone === false)
		$('#btnCertificate').prop('disabled', true);
	
	$(document)
	.on('click', '#btnAccountKey', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#genAccountKey').val();
		document.forms["certForm"].submit();
	})
	.on('click', '#btnRegister', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#registerAccount').val();
		document.forms["certForm"].submit();
	})
	.on('click', '#btnAcceptTOS', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#acceptAgreement').val();
		document.forms["certForm"].submit();
	})
	.on('click', '.btnChallenge', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#testChallenge').val();
		document.forms["certForm"].submit();
	})
	.on('click', '#btnDomainKey', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#genDomainKey').val();
		document.forms["certForm"].submit();
	})
	.on('click', '#btnCertificate', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#getCertificate').val();
		document.forms["certForm"].submit();
	})
	.on('click', '.certMode', function(e) {
		e.preventDefault();
		document.forms["certForm"].action = $('#changeMode').val();
		document.forms["certForm"].submit();
	})
})
