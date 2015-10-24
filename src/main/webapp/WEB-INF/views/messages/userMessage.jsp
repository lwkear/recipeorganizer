<!DOCTYPE html>
<html>
<head>
<title>Message</title>
</head>

<%@include file="../common/head.jsp"%>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">
	 	<div class="col-sm-12">
			<div class="page-header">
				<h3>${title}</h3>
			</div>			
			<div class="row">
				<div class="span12 center alert alert-success" style="text-align: center !important;">
					<span>${message}</span>
				</div>
			</div>
		</div>
	</div>	

<%@include file="../common/footer.jsp" %>

</body>
</html>
