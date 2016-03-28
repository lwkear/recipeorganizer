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
		<c:if test="${not empty warningMaint}">
			<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
		</c:if>
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
						<th><spring:message code="useradmin.common.passwordExpired"></spring:message></th>
						<th><spring:message code="useradmin.common.accountExpired"></spring:message></th>
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
							<td><fmt:formatDate type="date" value="${user.dateAdded}" /></td>
							<td><fmt:formatDate type="both" timeStyle="short" value="${user.lastLogin}" /></td>
							<td><c:out value="${user.loggedIn ? yesLabel : noLabel}"/></td>							
							<td><c:out value="${user.enabled ? yesLabel : noLabel}"/></td>
							<td><c:out value="${user.locked ? yesLabel : noLabel}"/></td>
							<td><c:out value="${user.passwordExpired ? yesLabel : noLabel}"/></td>
							<td><c:out value="${user.accountExpired ? yesLabel : noLabel}"/></td>
							<td>${user.role.name}</td>
							<td>
								<button class="btn btn-success btn-xs" type="button" onclick="updateUser(${user.id})"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.edit"></spring:message>">
								<span class="glyphicon glyphicon-pencil"></span></button>
							</td>
							<td>
								<button class="btn btn-danger btn-xs" type="button" onclick="checkRecipeCount(${user.id},'${user.firstName}','${user.lastName}')"
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

<!-- update user dialog -->
<div class="modal" id="updateUser" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title userName"></h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<div class="form-group">
			            <label class="control-label" for="inputRole"><spring:message code="useradmin.common.role"></spring:message>:</label>
		                <select class="form-control" id="inputRole">
		                	<option data-id="0"><c:out value="NONE" /></option>
		                	<c:forEach items="${roles}" var="role">
		                		<option data-id="${role.id}"><c:out value="${role.name}" /></option>
		                	</c:forEach>
		                </select>
				    </div>           
					<div class="form-group">
						<div class="row">
							<div class="col-sm-7">
								<label class="control-label" for="enabled"><spring:message code="useradmin.common.enabled"></spring:message>:</label>
							</div>
							<div class="col-sm-5">
								<label class="radio-inline"><input type="radio" name="enabled" value="1"><spring:message code="common.yes"></spring:message></label>
								<label class="radio-inline"><input type="radio" name="enabled" value="0"><spring:message code="common.no"></spring:message></label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div class="col-sm-7">
								<label class="control-label" for="locked"><spring:message code="useradmin.common.locked"></spring:message>:</label>
							</div>
							<div class="col-sm-5">
								<label class="radio-inline"><input type="radio" name="locked" value="1"><spring:message code="common.yes"></spring:message></label>
								<label class="radio-inline"><input type="radio" name="locked" value="0"><spring:message code="common.no"></spring:message></label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div class="col-sm-7">
								<label class="control-label" for="pswdExpired"><spring:message code="useradmin.common.passwordExpired"></spring:message>:</label>
							</div>
							<div class="col-sm-5">
								<label class="radio-inline"><input type="radio" name="pswdExpired" value="1"><spring:message code="common.yes"></spring:message></label>
								<label class="radio-inline"><input type="radio" name="pswdExpired" value="0"><spring:message code="common.no"></spring:message></label>
							</div>
						</div>
					</div>
					<div class="form-group">
						<div class="row">
							<div class="col-sm-7">
								<label class="control-label" for="pswdExpired"><spring:message code="useradmin.common.accountExpired"></spring:message>:</label>
							</div>
							<div class="col-sm-5">
								<label class="radio-inline"><input type="radio" name="acctExpired" value="1"><spring:message code="common.yes"></spring:message></label>
								<label class="radio-inline"><input type="radio" name="acctExpired" value="0"><spring:message code="common.no"></spring:message></label>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submit"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

</body>

<!-- include user admin-specific routines -->
<script src="<c:url value="/resources/custom/useradmin.js" />"></script>

</html>
