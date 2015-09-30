<!DOCTYPE html>
<html>
<head>
<title>Error</title>
</head>

<%@include file="../common/js.jsp"%>
<%@include file="../common/head.jsp"%>

<body role="document">

    <%@include file="../common/nav.jsp" %>

	<div class="container">
		<h1>
			Error!  
		</h1>
		<div class="col-sm-12">&nbsp;</div>
		<div class="col-sm-12">&nbsp;</div>
		<div class="col-sm-12">
			<div class="span12 center alert alert-danger" style="text-align: center !important;">
				<span>
					An error has occurred: ${errorMessage}
				</span>
			</div>
		</div>
		<c:if test="${not empty addError}">
			<div class="col-sm-12">&nbsp;</div>
			<div class="col-sm-12">&nbsp;</div>
			<div class="col-sm-12 text-center">
				<a class="btn btn-default" href="addRecipe" role="button">Return to AddRecipe</a>
			</div>
		</c:if>
	</div>
	
</body>
<!-- Placed at the end of the document so the pages load faster -->
</html>
