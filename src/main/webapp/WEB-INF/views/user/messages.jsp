<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="messages.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

<c:url var="searchUrl" value="/submitIngredientSearch"/>

	<div class="container container-white">	
		<c:if test="${not empty warningMaint}">
			<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
		</c:if>
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="messages.title"></spring:message></h3>
			</div>			
			<table class="table" id="messageList">
				<thead>
					<tr>
						<th></th>
						<th><spring:message code="messages.column.sent"></spring:message></th>
						<th><spring:message code="messages.column.from"></spring:message></th>
						<th><spring:message code="messages.column.recipe"></spring:message></th>
						<th><spring:message code="messages.column.message"></spring:message></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="msg" items="${messages}">
						<tr id="${msg.id}">
							<td>${msg.viewed}</td>
							<td><fmt:formatDate type="both" timeStyle="short" value="${msg.dateSent}" /></td>
							<td>${msg.fromFirstName} ${msg.fromLastName}</td>							
							<td>${msg.recipeName}</td>
							<td>${msg.message}</td>
							<td>
								<button class="btn btn-success btn-xs <c:if test="${empty msg.recipeId}">disabled</c:if>" type="button" id="email${msg.id}"
								onclick="emailRecipe(${userId}, ${msg.fromUserId}, '${msg.fromFirstName}', '${msg.fromLastName}', ${msg.recipeId}, '${msg.recipeName}', ${msg.id})"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.email"></spring:message>">
								<span class="glyphicon glyphicon-envelope"></span></button>
							</td>
							<td>
								<button class="btn btn-primary btn-xs" type="button" id="respond${msg.id}" 
								onclick="sendMessage(${userId}, ${msg.fromUserId}, '${msg.fromFirstName}', '${msg.fromLastName}', ${msg.recipeId}, ${msg.id})"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.respond"></spring:message>">
								<span class="glyphicon glyphicon-user"></span></button>
							</td>
							<td>
								<button class="btn btn-danger btn-xs" type="button" id="delete${msg.id}" onclick="deleteMessage(${msg.id})"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.delete"></spring:message>">
								<span class="glyphicon glyphicon-remove"></span></button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>	

<!-- share recipe dialog -->
<div class="modal" id="emailRecipeDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title" id="emailRecipeName"></h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<div class="form-group">
			            <label class="control-label" id="emailToLabel"></label>
				    </div>           
					<div class="form-group">
			            <label class="control-label" for="emailRecipeMsg"><spring:message code="share.message.label"></spring:message></label>
			            <textarea class="form-control" rows="5" id="emailRecipeMsg"></textarea>
				    </div>           
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submitEmail"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<%@include file="../common/footer.jsp" %>
<%@include file="../user/userMessage.jsp" %>

</body>

<!-- include user admin-specific routines -->
<script src="<c:url value="/resources/custom/usermessage.js" />"></script>

</html>
