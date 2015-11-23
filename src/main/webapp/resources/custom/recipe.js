function toFraction(amt) {
	if (amt <= .2) return '&frac18;';
	if (amt <= .3) return '&frac14;';
	if (amt <= .35) return '&frac13;';
	if (amt <= .4) return '&frac38;';
	if (amt <= .5) return '&frac12;';
	if (amt <= .65) return '&frac58;';
	if (amt <= .7) return '&frac23;';
	if (amt <= .8) return '&frac34;';
	if (amt <= .9) return '&frac78;';
}

function convertFractions(element) {
	$(element).each(function(index) {
		var qty = $(this).html();
		var num = Math.floor(qty);
		var dec = (qty - num);
		var code = '';
		if (dec > 0)
			code = toFraction(dec);

		console.log("qty: " + qty);
		console.log("num: " + num);
		console.log("dec: " + dec);
		console.log("code: " + code);

		var frac;
		if (num > 0)
			frac = num + code;
		else
			frac = code;
		
		$(this).html(frac);
	});
}

$(function() {

	convertFractions('.ingredqty');

})
