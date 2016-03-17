<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="commentadmin.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

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
				<h3><spring:message code="commentadmin.title"></spring:message></h3>
			</div>			
			<table class="table" id="commentList">
				<thead>
					<tr>
						<th><spring:message code="commentadmin.column.recipeName"></spring:message></th>
						<th><spring:message code="commentadmin.column.comment"></spring:message></th>
						<th><spring:message code="commentadmin.column.dateAdded"></spring:message></th>
						<th><spring:message code="commentadmin.column.userName"></spring:message></th>
						<th data-orderable="false"></th>
						<th data-orderable="false"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="comment" items="${comments}">
						<tr id="${comment.id}">
							<td>${comment.recipeName}</td>
							<td>${comment.userComment}</td>
							<td>${comment.dateAdded}</td>
							<td>${comment.lastName},&nbsp;${comment.firstName}</td>
							<td>
								<button class="btn btn-success btn-xs" type="button" onclick="removeCommentFlag(${comment.id})"><span class="glyphicon glyphicon-ok"></span></button>
							</td>
							<td>
								<button class="btn btn-danger btn-xs" type="button" onclick="deleteComment(${comment.id})"><span class="glyphicon glyphicon-remove"></span>
								</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>	

<%@include file="../common/footer.jsp" %>

</body>

<!-- include user admin-specific routines -->
<script src="<c:url value="/resources/custom/commentadmin.js" />"></script>

</html>
