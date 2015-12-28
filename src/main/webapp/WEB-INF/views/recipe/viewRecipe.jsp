<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title>${recipe.name} - <spring:message code="menu.product"></spring:message></title>

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
				</h3>
				<h5>
					<spring:message code="common.submittedby"></spring:message>&nbsp;${recipe.user.firstName}&nbsp;${recipe.user.lastName}
				</h5>
			</div>
			
			<%@include file="recipeContent.jsp" %>
			
		</div>
	</div>		
	
	<div class="col-sm-12" style="display:none">
		<iframe id="iframerpt" name="iframerpt" width="100%" height="100%" src="<c:url value="/report/getHtmlRpt/${recipe.id}"/>"></iframe>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/recipe.js" />"></script>

</html>