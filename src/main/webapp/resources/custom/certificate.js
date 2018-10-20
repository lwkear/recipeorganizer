$(function() {
	blurInputFocus();

	$('#btnAccountKey').prop('disabled', true);
	$('#btnDomainKey').prop('disabled', true);
	$('#btnAcceptTOS').prop('disabled', true);
	$('#btnRegister').prop('disabled', true);
	$('#btnAuthorized').prop('disabled', true);
	$('#btnCertificate').prop('disabled', true);
	
	var accountKey = $('#accountKey').val();
	var domainKey = $('#domainKey').val();
	var agreement = $('#agreement').val();
	var registered = $('#registered').val();
	var authorized = $('#authorized').val();
	var certificate = $('#certificate').val();
	
	if (accountKey === "false")
		$('#btnAccountKey').prop('disabled', false);
	else	
	if (domainKey === "false")
		$('#btnDomainKey').prop('disabled', false);
	else
	if (agreement === "false")
		$('#btnAcceptTOS').prop('disabled', false);
	else
	if (registered === "false")
		$('#btnRegister').prop('disabled', false);
	else
	if (authorized === "false")
		$('#btnAuthorized').prop('disabled', false);
	else
	if (certificate === "false")
		$('#btnCertificate').prop('disabled', false);
	
	$(document)
	.on('click', '#btnAccountKey', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#genAccountKey').val();
		document.forms["certForm"].submit();
	})
	.on('click', '#btnDomainKey', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#genDomainKey').val();
		document.forms["certForm"].submit();
	})
	.on('click', '#btnAcceptTOS', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#acceptAgreement').val();
		document.forms["certForm"].submit();
	})
	.on('click', '#btnRegister', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#registerAccount').val();
		document.forms["certForm"].submit();
	})
	.on('click', '#btnAuthorized', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#authorizeDomains').val();
		document.forms["certForm"].submit();
	})
	/*.on('click', '.btnChallenge', function(e){
		e.preventDefault();
		document.forms["certForm"].action = $('#testChallenge').val();
		document.forms["certForm"].submit();
	})*/
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
