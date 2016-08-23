<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="ingredadmin.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

<c:url var="searchUrl" value="/submitIngredientSearch"/>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header"> 		
				<h3><spring:message code="ingredadmin.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">			
			<table class="table" id="ingredientList">
				<thead>
					<tr>
						<th><spring:message code="ingredadmin.column.name"></spring:message></th>
						<th><spring:message code="ingredadmin.column.lang"></spring:message></th>
						<th><spring:message code="ingredadmin.column.usage"></spring:message></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="ingred" items="${ingredients}">
						<tr id="${ingred.id}">
							<td>${ingred.name}</td>
							<td>${ingred.displayLang}</td>
							<td>${ingred.usage}</td>
							<td>
								<form:form class="searchForm" id="searchForm" action="${searchUrl}" target = "_blank" method="post">
									<input type="text" style="display:none" name="searchTerm" id="searchTerm" value="${ingred.id}"/>
									<button class="btn btn-info btn-xs <c:if test="${ingred.usage == 0}">disabled</c:if>" type="submit" id="view${ingred.id}" onclick="this.blur();"
									data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.viewrecipes"></spring:message>">
									<span class="glyphicon glyphicon-list-alt"></span></button>
								</form:form>
							</td>
							<td>
								<button class="btn btn-success btn-xs" type="button" id="update${ingred.id}" onclick="updateIngredient(${ingred.id})"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.edit"></spring:message>">
								<span class="glyphicon glyphicon-pencil"></span></button>
							</td>
							<td>
								<button class="btn btn-success btn-xs" type="button" id="replace${ingred.id}" onclick="replaceIngredient(${ingred.id}, 
								'<spring:escapeBody javaScriptEscape="true">${ingred.name}</spring:escapeBody>')"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.replace"></spring:message>">
								<span class="glyphicon glyphicon-transfer"></span></button>
							</td>
							<td>
								<button class="btn btn-success btn-xs" type="button" id="approve${ingred.id}" onclick="approveIngredient(${ingred.id})"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.approve"></spring:message>">
								<span class="glyphicon glyphicon-ok"></span></button>
							</td>
							<td>
								<button class="btn btn-danger btn-xs" type="button" id="delete${ingred.id}" onclick="deleteIngredient(${ingred.id}, ${ingred.usage}, 
								'<spring:escapeBody javaScriptEscape="true">${ingred.name}</spring:escapeBody>')"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.delete"></spring:message>">
								<span class="glyphicon glyphicon-remove"></span></button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>	

<%@include file="../common/footer.jsp" %>

<!-- update ingredient dialog -->
<div class="modal" id="updateIngredient" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title updateName"></h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<div class="form-group">
			            <label class="control-label" for="name"><spring:message code="recipe.ingredients.ingredient"></spring:message></label>
			            <input type="text" class="form-control maxSize" id="name" data-max="${sizeMap['name.max']}"/>
			            <span class="text-danger" id="nameMsg">${nameError}</span>
				    </div>           
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submitUpdate"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<!-- replace ingredient dialog -->
<div class="modal" id="replaceIngredient" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title replaceName"></h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<input type="text" style="display:none" id="ingredTAName">
				    <input type="text" style="display:none" id="ingredTAId">
				    <div><spring:message code="ingredadmin.instruct"></spring:message></div>
					<div class="form-group spacer-vert-xs">
			            <label class="control-label" for="name"><spring:message code="ingredadmin.replace"></spring:message></label>
			            <input type="text" class="form-control" id="nameTA"/>
				    </div>
				</form>
			</div>
			<div class="modal-footer spacer-vert-md">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submitReplace"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

</body>

<!-- <script type="text/javascript">
	document.getElementsByClassName('searchForm').target = "_blank";
</script>
 -->
<!-- include user admin-specific routines -->
<script src="<c:url value="/resources/custom/typeahead.js" />"></script>
<script src="<c:url value="/resources/custom/ingredientadmin.js" />"></script>

</html>
