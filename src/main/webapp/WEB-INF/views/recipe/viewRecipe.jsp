<!DOCTYPE html>
<html>
<head>

<title>DisplayRecipe</title>

<%@include file="../common/head.jsp" %>

<script type="text/javascript">
$(document).ready(function () {
	
	$('#htmlPrint').on('click', function(e)
	{
		document.getElementById("iframerpt").contentWindow.print();
	})
});
</script>

</head>

<body role="document" onload="blurInputFocus()">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header">
				<h3>${recipe.name}
					<button type="button" class="btn btn-link btn-sm" id="htmlPrint" style="margin-left:30px;font-size:20px">
					  <span class="glyphicon glyphicon-print"></span>
					</button>
					<button type="button" class="btn btn-link btn-sm" style="margin-left:5px;font-size:20px">
					  <span class="glyphicon glyphicon-envelope"></span>
					</button>
					<!-- <a class="btn btn-default btn-sm disabled" href="#"><span class="glyphicon glyphicon-print"></span></a>
					<a class="btn btn-default btn-sm disabled" href="#"><span class="glyphicon glyphicon-envelope"></span></a> -->
					<!-- <a class="" style="margin-left:30px" href="#"><span class="glyphicon glyphicon-print"></span></a>
					<a class="" style="margin-left:10px" href="#"><span class="glyphicon glyphicon-envelope"></span></a> -->
				</h3>
				<h5>
					<spring:message code="common.submittedby"></spring:message>&nbsp;${recipe.user.firstName}&nbsp;${recipe.user.lastName}
				</h5>
			</div>
			
<%@include file="recipeContent.jsp" %>
			
		</div>
	</div>		
	
	<div class="col-sm-12" style="display:none">
		<iframe id="iframerpt" name="iframerpt" width="100%" height="300" src="<c:url value="/report/getHtmlRpt/${recipe.id}"/>"></iframe>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/recipe.js" />"></script>

</html>