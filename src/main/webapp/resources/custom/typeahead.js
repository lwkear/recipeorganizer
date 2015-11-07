function setBHPrefetchOpts(cache, prefetchUrl, prefetchFilter) {

	var prefetchOptions = {};

	prefetchOptions['url'] = prefetchUrl;
	prefetchOptions['cache'] = cache;
	prefetchOptions['filter'] = prefetchFilter;

	return prefetchOptions;
}

function setBHRemoteOpts(cache, wildcard, remoteUrl, remoteFilter) {

	var remoteOptions = {};

	remoteOptions['url'] = remoteUrl;
	remoteOptions['cache'] = cache;
	remoteOptions['wildcard'] = wildcard; 
	remoteOptions['filter'] = remoteFilter;

	return remoteOptions;
}

function setBHOptions(sufficient, datumToken, queryToken, prefetchOptions, remoteOptions) {

	var token = Bloodhound.tokenizers.whitespace; 
	
 	var options = {};
	
 	options['sufficient'] = sufficient; 
	options['datumTokenizer'] = datumToken === null ? token : datumToken;
	options['queryTokenizer'] = queryToken === null ? token : queryToken;

	if (prefetchOptions != null)
		options['prefetch'] = prefetchOptions;

	if (remoteOptions != null)
		options['remote'] = remoteOptions;

	return options;
}	

function initTypeaheadOptions(hint, highlight, minLength) {

	var options = {};
	
	options['hint'] = hint;
	options['highlight'] = highlight;
	options['minLength'] = minLength;

	return options;
};

function initTypeaheadDataset(name, displayKey, limit, source) {

	var dataset = {};

	dataset['name'] = name;
	dataset['displayKey'] = displayKey;
	dataset['limit'] = limit;
    dataset['source'] = source;
	
	return dataset;
};
