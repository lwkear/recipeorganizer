<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="recipe.review.title"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document" onload="blurInputFocus()">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header"> 		
				<h3><spring:message code="recipe.review.title"></spring:message></h3>
			</div>
		</div>
	 	<div class="col-sm-12">
			<div class="text-center">
				<p class="std-maroon"><spring:message code="recipe.review.gobackandfix"></spring:message></p>
			</div>
			<div class="spacer-vert-md">
				<h3>${recipe.name}</h3>
			</div>
			
			<%@include file="recipeContent.jsp" %>

			<form:form role="form" modelAttribute="recipe">
				<div class="row spacer-vert-md">
					<div class="col-sm-2 col-sm-push-5 text-center">
						<button class="btn btn-primary" type="submit" id="save" name="_eventId_save"><spring:message code="common.save"></spring:message></button>
					</div>
					<div class="col-sm-2 col-sm-pull-2 text-left">
						<button class="btn btn-default" type="submit" name="_eventId_back"><spring:message code="common.back"></spring:message></button>
					</div>
					<div class="col-sm-2 col-sm-push-6 text-right">
						<button class="btn btn-default" id="fakeSubmitCancel"><spring:message code="common.cancel"></spring:message></button>
						<button id="cancelSubmitBtn" type="submit" name="_eventId_cancel" style="display:none"></button>
					</div>
				</div>
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
			</form:form>
		</div>
	</div>		

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/review.js" />"></script>

</html>