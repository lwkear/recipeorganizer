function removeEmptyRows(element, fields) {
	console.log("removeEmptyRows");
	console.log("element=" + element);
	var numRows = $(element).length;
	var rowZeroEmpty = false;
	var numNotEmpty = 0;
	console.log("#elements=" + numRows);
	$(element).each(function(index) {
		console.log("row#" + index);
		var currentRow = $(this);
		var notEmpty = false;
		for	(ndx = 0; ndx < fields.length; ndx++) {
			console.log("field[" + ndx + "]=" + fields[ndx]);
			var str = currentRow.find(fields[ndx]).val().trim();
			if (str.length > 0) {
				console.log("notempty");
				notEmpty = true;
				break;
			}
		}
		if (notEmpty === false && index === 0)
			rowZeroEmpty = true;
		else {
			if (notEmpty)
				numNotEmpty++;
			else
			{
				console.log("removing row#" + index);
				currentRow.remove();
			}
		}
	});
	
	if (rowZeroEmpty === true && numNotEmpty > 0) {
		$(element).each(function(index) {
			console.log("removing row#" + index);
			var currentRow = $(this);
			currentRow.remove();
			return false;
		});
	}
}
