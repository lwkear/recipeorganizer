<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="menu.browse"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document" onload="blurInputFocus()">

<%@include file="../common/nav.jsp" %>

<c:set var="noPhoto"><spring:message code="common.photo.nophoto"></spring:message></c:set>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="menu.browse"></spring:message></h3>
			</div>
		</div>
		<input type="hidden" id="currentCategoryId" value="${defaultCatId}"/>
		<%-- <input id="currentCategoryId" value="${defaultCatId}"/> --%>
		<div class="col-sm-12">
			<div class="row">
				<div class="col-sm-3" role="navigation">
					<ul class="nav nav-pills nav-stacked">
						<c:forEach var="category" items="${catList}" varStatus="loop">
							<li role="presentation" class="category <c:if test="${category.id eq defaultCatId}">active</c:if>">
								<a href="#none" data-toggle="tab" data-id="${category.id}" id="cat${category.id}">${category.displayName}</a>
							</li>
						</c:forEach>
					</ul>
				</div>

				<div class="col-sm-9" style="border-left: 1px solid #000">
					<div id="recipesSection">
						<div class="col-sm-12">
							<span class="search-label"><spring:message code="recipe.table.label"></spring:message></span>
						</div>
						<table class="table" id="categoryRecipeList">
							<thead>
								<tr>
									<th></th>
									<th data-orderable="false"></th>
									<th></th>
									<th></th>
									<th></th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
		</div>			
	</div>
   
   
<%@include file="../common/footer.jsp" %>	
	
</body>

<!-- include specific routines -->
<script src="<c:url value="/resources/custom/browserecipes.js" />"></script>

</html>