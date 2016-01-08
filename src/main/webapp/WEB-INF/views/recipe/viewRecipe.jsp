<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title>${recipe.name} - <spring:message code="menu.product"></spring:message></title>

</head>

<c:set var="returnLabel" value="${sessionScope.returnLabel}"/>
<c:set var="returnUrl" value="${sessionScope.returnUrl}"/>

<body role="document" onload="blurInputFocus()">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header">
				<c:if test="${not empty returnLabel}">
					<h5><a class="btn btn-link btn-xs" href="${returnUrl}"><spring:message code="${returnLabel}"></spring:message></a></h5>
				</c:if>
				<h3>${recipe.name}
					<c:if test="${favorite}">
						<button type="button" class="btn btn-link btn-sm" style="margin-left:5px;font-size:20px">
						  <span class="glyphicon glyphicon-star"></span>
						</button>
					</c:if>
					<span class="pull-right">
						<button type="button" class="btn btn-link btn-sm" id="htmlPrint" style="margin-left:30px;font-size:20px">
						  <span class="glyphicon glyphicon-print"></span>
						</button>
						<button type="button" class="btn btn-link btn-sm" id="email" style="margin-left:5px;font-size:20px">
						  <span class="glyphicon glyphicon-envelope"></span>
						</button>
						<c:if test="${not favorite}">
							<button type="button" class="btn btn-link btn-sm" id="favorite" style="margin-left:5px;font-size:20px">
							  <span class="glyphicon glyphicon-star"></span>
							</button>
						</c:if>
						<c:if test="${isAuth && (userId == recipe.user.id)}">
							<a class="btn btn-link btn-sm" id="edit" style="margin-left:5px;font-size:20px" href="../editRecipe/${recipe.id}">
								<span class="glyphicon glyphicon-pencil"></span>
							</a>
						</c:if>
					</span>
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
	<input type="hidden" id="userId" value="${recipe.user.id}"/>
	<input type="hidden" id="recipeId" value="${recipe.id}"/>
	<input type="hidden" id="returnUrl" value="${returnUrl}"/>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include recipe-specific routines -->
<script src="<c:url value="/resources/custom/recipeview.js" />"></script>

</html>