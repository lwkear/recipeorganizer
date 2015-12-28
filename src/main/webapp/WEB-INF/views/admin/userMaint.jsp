<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="useradmin.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="../common/nav.jsp" %>

<spring:message var="yesLabel" code="common.yes"></spring:message>
<spring:message var="noLabel" code="common.no"></spring:message>

	<div class="container container-white">	
	 	<div class="col-sm-12">
			<div class="page-header"> 		
				<h3><spring:message code="useradmin.title"></spring:message></h3>
			</div>			
			<table class="table" id="userList">
				<thead>
					<tr>
						<th><spring:message code="useradmin.column.email"></spring:message></th>
						<th><spring:message code="useradmin.column.name"></spring:message></th>
						<th><spring:message code="useradmin.column.joined"></spring:message></th>
						<th><spring:message code="useradmin.column.lastlogin"></spring:message></th>
						<th><spring:message code="useradmin.column.loggedIn"></spring:message></th>
						<th><spring:message code="useradmin.common.enabled"></spring:message></th>
						<th><spring:message code="useradmin.common.locked"></spring:message></th>
						<th><spring:message code="useradmin.common.role"></spring:message></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="user" items="${users}">
						<tr id="${user.id}">
							<td>${user.email}</td>
							<td>${user.lastName},&nbsp;${user.firstName}</td>
							<td>${user.dateAdded}</td>
							<td>${user.lastLogin}</td>
							<td><c:out value="${user.loggedIn ? yesLabel : noLabel}"/></td>							
							<td><c:out value="${user.enabled ? yesLabel : noLabel}"/></td>
							<td><c:out value="${user.locked ? yesLabel : noLabel}"/></td>
							<td>${user.role.name}</td>
							<td>
								<button class="btn btn-success btn-xs" type="button" onclick="updateUser(${user.id})"><span class="glyphicon glyphicon-pencil"></span></button>
							</td>
							<td>
								<button class="btn btn-danger btn-xs" type="button" onclick="checkRecipeCount(${user.id},'${user.firstName}','${user.lastName}')">
									<span class="glyphicon glyphicon-remove"></span>
								</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<input id="yesText" style="display:none" value="${yesLabel}"/>
			<input id="noText" style="display:none" value="${noLabel}"/>
		</div>
	</div>	

<!-- Delete user dialog -->
<div class="modal fade" id="deleteUser" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"><spring:message code="useradmin.delete.title"></spring:message></h4>
			</div>
			<div class="modal-body" id="noRecipes" style="display:none">			
				<div class="text-center"><spring:message code="useradmin.delete.areyousure1"></spring:message>&nbsp;<span class="userName"></span>?</div>
			</div>
			<div class="modal-body" id="hasRecipes" style="display:none">
				<div class="text-center"><span class="userName"></span>&nbsp;<spring:message code="useradmin.delete.hasrecipe1"></spring:message>&nbsp;
					<span id="recipeCount"></span>&nbsp;<spring:message code="useradmin.delete.hasrecipe2"></spring:message>&nbsp;
					<spring:message code="useradmin.delete.areyousure2"></spring:message></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="deleteUser()"><spring:message code="common.yes"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.no"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<!-- update user dialog -->
<div class="modal fade" id="updateUser" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"><spring:message code="useradmin.update.title"></spring:message></h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<div class="form-group">
						<div class="userName2"></div>
					</div>
					<div class="form-group">
			            <label class="control-label" for="inputRole"><spring:message code="useradmin.common.role"></spring:message>:</label>
		                <select class="form-control" id="inputRole">
		                	<c:forEach items="${roles}" var="role">
		                		<option data-id="${role.id}"><c:out value="${role.name}" /></option>
		                	</c:forEach>
		                </select>
				    </div>           
					<div class="form-group">
						<label class="control-label" for="enabled"><spring:message code="useradmin.common.enabled"></spring:message>:</label>
						<div class="radio" style="margin-top:0">
							<label><input type="radio" name="enabled" value="1"><spring:message code="common.yes"></spring:message></label>&nbsp;
							<label><input type="radio" name="enabled" value="0"><spring:message code="common.no"></spring:message></label>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label sm_col-2" for="locked"><spring:message code="useradmin.common.locked"></spring:message>:</label>
						<div class="radio" style="margin-top:0">
							<label><input  type="radio" name="locked" value="1" checked><spring:message code="common.yes"></spring:message></label>&nbsp;
							<label><input type="radio" name="locked" value="0"><spring:message code="common.no"></spring:message></label>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="postUser()"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<%@include file="../common/footer.jsp" %>	

</body>

<script type="text/javascript">
var yesText = $('#yesText').val();
var noText = $('#noText').val();
</script>

<!-- include user admin-specific routines -->
<script src="<c:url value="/resources/custom/useradmin.js" />"></script>

</html>
