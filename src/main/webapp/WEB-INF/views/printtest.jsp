<!DOCTYPE html>
<html>
<head>

<title>Print</title>

<%@include file="common/head.jsp" %>

<!-- <style type="text/css">
@page { size: auto;  margin: .5mm; }
</style> -->

<script type="text/javascript">

/* window.__defineSetter__(
	    "print",
	    function(){alert("GOTCHA!"); debugger;}
	);
 */

/* function printpdf() {
	document.getElementById("iframepdf").contentWindow.print();
	//var doc = document.getElementById("#iframepdf");
	//doc.focus();
	//doc.print();
	//document.window.frames["iframepdfid"].focus();
	//document.window.frames["iframepdfid"].print();
}
*/

/* function printobj() {
    //idPrint.disabled = 0;
    //objpdf.print();
    //document.getElementById("idobjpdf").contentWindow.print();
	//document.getElementById("idobjpdf").print();
	idobjpdf.print();
}
 */
 
/* function objpdf_onreadystatechange() {
    if (objpdf.readyState === 4)
        setTimeout(printobj, 1000);
}
 */
 
/* function printobj() {
	//var doc = document.getElementById("objpdf");
	//doc.print();
	objpdf.print();
} */

/* function printembed(documentId) {
	//var doc = document.getElementById("embedpdf");
	//doc.print();

	//embedpdf.print();	

	document.getElementById("btnembed").disabled = true;
	
    //var x = document.getElementById(documentId);
    //x.print();
	
	//document.getElementById(documentId).contentWindow.print();
	//document.getElementById(documentId).contentWindow.print();
	
	if (typeof document.getElementById(documentId).print == 'undefined') {
        setTimeout(function(){printembed(documentId);}, 1000);
    } else {
        var x = document.getElementById(documentId);
        x.print();
        document.getElementById("btnembed").disabled = false;
    }
}
 */
 
$(document).ready(function () {
	
	$('#htmlPrint').on('click', function(e)
	{
		//e.preventDefault();
    
		//var win = document.getElementById('iframetext').contentWindow.document.body.innerHTML;
		//win = document.getElementById('iframetext').contentWindow;
		//var html = win.document.getElementByTagName("html");
		//var doc = document.getElementById('iframetext').contentDocument;
		//html = doc.document.getElementByTagName("html");
		
		//var html = $('iframetext').find('html');
		//html = $('iframetext').find('html').innerHTML;
		//html = $('iframetext').find('html').html();

		//var iframe = $('<iframe>');
		//var win = iframe.get(0);
		//var content = win.contentWindow;

		//iframe = document.getElementsByTagName("iframe")[0];
		//win = iframe.contentWindow;
		//var title = iframe.contentWindow.title;

		//var doc = $('#iframetext').contentDocument.document;
		//var html = $(doc).contents(); //.find('html').html();
		//var win = $('#iframetext').contentWindow.document;
		//doc.find('html').replaceWith('<html moznomarginboxes mozdisallowselectionprint>');
		//var html = doc.find('html').val();
		document.getElementById("iframetext").contentWindow.print();
		//$('#iframetext').contentWindow.document.print();
	})
});


</script>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<%-- <h3><spring:message code="about.title"></spring:message></h3> --%>
				<h3>Print Test</h3>
			</div>
			<div class="col-sm-12"> <!--  style="display:none"> -->
				<%-- <iframe id="iframetext" width="100%" height="100%" src="<c:url value="/resources/TestPrint.html"/>"></iframe> --%>
				<%-- <iframe id="iframetext" name="iframetext" width="100%" height="100%" src="<c:url value="/report/gethtmlConn"/>"></iframe> --%>				
			</div>
			<div class="col-sm-12">
				<%-- <iframe id="iframepdf" width="100%" height="100%" src="<c:url value="/report/getpdf"/>"></iframe> --%>
				<!-- <iframe id="iframepdf" width="100%" height="100%" src=""></iframe> -->
				<%-- last version
				<iframe id="iframepdf" name="iframepdf" width="100%" height="100%" src="<c:url value="/resources/TestPrint.pdf"/>"></iframe> --%>
				<iframe id="iframetext" name="iframetext" width="100%" height="300" src="<c:url value="/report/gethtmlBean"/>"></iframe>				
			</div>
			<div class="col-sm-12" >
				<!-- <iframe width="100%" height="100%"> -->
					<%-- <object width="100%" height="100%" id="print-pdfobj" data="<c:url value="/resources/TestPrint.pdf"/>" type="application/pdf"></object> --%>
				<!-- </iframe> -->
				<%-- <object id="idobjpdf" name="objpdf"
					width="100%" height="100%" type="application/pdf" onreadystatechange="objpdf_onreadystatechange()"
					data="<c:url value="/resources/TestPrint.pdf"/>?#view=Fit&scrollbar=0&toolbar=0&navpanes=0">
					<span>PDF plugin is not available.</span>
				</object> --%>
				<%-- last version				
				<object id="idobjpdf" name="objpdf"
					width="200" height="100" type="application/vnd.adobe.pdfxml"
					data="<c:url value="/resources/TestPrint.pdf"/>?#view=Fit&scrollbar=0&toolbar=0&navpanes=0">
					<span>PDF plugin is not available.</span>
				</object> --%>				
			</div>
			<div class="col-sm-12" >
				<!-- <iframe width="100%" height="100%"> -->
					<%-- <embed width="100%" height="100%" id="embedpdf" src="<c:url value="/resources/TestPrint.pdf"/>" type="application/pdf"/> --%>
				<!-- </iframe> -->
			</div>
			<div class="col-sm-12 spacer-vert-lg">
				<div class="col-sm-2">
					<!-- <button class="btn btn-primary" role="button" onclick="printpage()">Print iframe</button> -->
					<button class="btn btn-primary" role="button" id="htmlPrint">Print html</button>
				</div>
				<div class="col-sm-2">
					<a class="btn btn-default" href="<c:url value="/report/gethtml"></c:url>" role="button">Download html</a>
				</div>
				<div class="col-sm-2">
					<%-- <a class="btn btn-default" href="<c:url value="/report/getpdf"></c:url>" role="button">Download PDF</a> --%>
					<%-- <a class="btn btn-default" href="<c:url value="/report/getpdf"></c:url>" target="print-pdfframe" role="button">Print pdf</a> --%>
					<button class="btn btn-primary" role="button" onclick="printpdf()">Print PDF</button>
				</div>
				<div class="col-sm-2">
					<button class="btn btn-primary" role="button" onclick="printobj()">Print Object</button>
				</div>
				<div class="col-sm-2">
					<button class="btn btn-primary" role="button" id="btnembed" onclick="printembed('embedpdf')">Print Embed</button>
				</div>
			</div>
		</div>
	</div>
   
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
