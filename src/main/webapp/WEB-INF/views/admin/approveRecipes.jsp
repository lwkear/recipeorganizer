<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="approvaladmin.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

	<div class="container container-white">	
		<c:if test="${not empty warningMaint}">
			<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
		</c:if>
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="approvaladmin.head"></spring:message></h3>
			</div>			
			<table class="table" id="recipeList">
				<thead>
					<tr>
						<th><spring:message code="recipe.table.name"></spring:message></th>
						<th><spring:message code="recipe.table.description"></spring:message></th>
						<th><spring:message code="recipe.table.submittedby"></spring:message></th>
						<th><spring:message code="recipe.table.submitted"></spring:message></th>
						<th><spring:message code="recipe.table.category"></spring:message></th>
						<th><spring:message code="recipe.table.source"></spring:message></th>
						<th><spring:message code="recipe.table.status"></spring:message></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="recipe" items="${recipes}">
						<tr id="${recipe.id}">
							<td>${recipe.name}</td>
							<td>${recipe.description}</td>
							<td>${recipe.firstName} ${recipe.lastName}</td>
							<td><fmt:formatDate type="date" value="${recipe.submitted}" /></td>
							<td>${recipe.category}</td>
							<td>${recipe.sourcetype}</td>
							<td><custom:approval status="${recipe.status}"></custom:approval></td>
							<td><a class="btn btn-info btn-xs" href="../recipe/viewRecipe/${recipe.id}" id="view${recipe.id}"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.view"></spring:message>">
								<span class="glyphicon glyphicon-list-alt"></span></a>
							</td>
							<td><a class="btn btn-success btn-xs" href="../recipe/editRecipe/${recipe.id}" id="edit${recipe.id}"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.edit"></spring:message>">
								<span class="glyphicon glyphicon-pencil"></span></a>
							</td>
							<td>
								<button class="btn btn-primary btn-xs" type="button" id="action${recipe.id}" onclick="recipeAction(${recipe.userId}, ${recipe.id}, 
								'<spring:escapeBody javaScriptEscape="true">${recipe.name}</spring:escapeBody>')"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.action"></spring:message>">
								<span class="glyphicon glyphicon-ok"></span></button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>	

<!-- recipe action dialog -->
<div class="modal" id="recipeActionDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title recipeName"></h4>
			</div>
			<div class="modal-body">
				<form:form name="actionForm" role="form" modelAttribute="recipeMessageDto">
					<div class="form-group">
						<div class="row">
							<div class="col-sm-3">
								<label class="control-label" for="action"><spring:message code="approvaladmin.action"></spring:message></label>
							</div>
							<div class="col-sm-8">
								<form:select class="form-control" name="action" path="action">
									<form:options items="${approvalActions}"/> 
								</form:select>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div class="col-sm-3">
			            		<label class="control-label" for="reasons"><spring:message code="approvaladmin.optional"></spring:message></label>		<%--  --%>
			        		</div>
					        <div class="col-sm-9">
				                <form:select class="form-control" id="reasons" multiple="multiple" path="reasons">
									<form:options items="${approvalReasons}"/>		                	
				                </form:select>
						    </div>
						</div>
					</div>           
					<div class="form-group">
			            <label class="control-label" for="message"><spring:message code="usermessage.label"></spring:message></label>
			            <textarea class="form-control maxSize" rows="5" id="message" data-max="${sizeMap['message.max']}"></textarea>
			            <span class="text-danger" id="messageMsg">${messageErr}</span>
				    </div>           
				</form:form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submitActionMessage"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include list-specific routines -->
<script src="<c:url value="/resources/custom/recipelist.js" />"></script>

</html>


<%-- 
<div class="modal" id="approveRecipeDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title" id="recipeName"></h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<div class="form-group">
						<div class="row">
							<div class="col-sm-3">
								<label class="control-label" for="action"><spring:message code="approvaladmin.action"></spring:message></label>
							</div>
							<div class="col-sm-8">
								<label class="radio-inline"><input type="radio" name="action" value="${ApprovalAction.APPROVED}"><spring:message code="approvaladmin.approved"></spring:message></label>
								<label class="radio-inline"><input type="radio" name="action" value="${ApprovalAction.PENDING}"><spring:message code="approvaladmin.pending"></spring:message></label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div class="col-sm-3">
			            		<label class="control-label" for="editOption"><spring:message code="approvaladmin.edited"></spring:message></label>		
			        		</div>
					        <div class="col-sm-9">
				                <select class="form-control" id="editOption" multiple="multiple">
				                	<option value="0"><spring:message code="approvaladmin.misspelling"></spring:message></option>
				                	<option value="1"><spring:message code="approvaladmin.ingredient"></spring:message></option>
				                	<option value="2"><spring:message code="approvaladmin.other"></spring:message></option>
				                </select>
						    </div>
						</div>
					</div>           
					<div class="form-group">
			            <label class="control-label" for="message"><spring:message code="usermessage.label"></spring:message></label>
			            <textarea class="form-control maxSize" rows="5" id="message" data-max="${sizeMap['message.max']}"></textarea>
			            <span class="text-danger" id="messageMsg">${messageErr}</span>
				    </div>           
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submitApproveMessage"><spring:message code="common.send"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>
 --%>