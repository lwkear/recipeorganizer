<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="col-sm-12">
	<table class="table" id="emailList">
		<thead>
			<tr>
				<th></th>
				<th></th>
				<th><spring:message code="emails.column.sent"></spring:message></th>
				<th><spring:message code="emails.column.from"></spring:message></th>
				<th><spring:message code="emails.column.to"></spring:message></th>
				<th><spring:message code="emails.column.subject"></spring:message></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th data-orderable="false"></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="email" items="${emails}">
				<tr id="${email.UID}">
					<td>${email.seen}</td>
					<td>
						<c:if test="${email.hasAttachment}">
							<span class="glyphicon glyphicon-paperclip"></span>
						</c:if>
					</td>
					<td data-order="${email.sortDate}"><fmt:formatDate type="both" timeStyle="short" value="${email.sentDate}" /></td>
					<td>
						<c:forEach var="from" items="${email.from}">
							${from.name}
						</c:forEach>
					</td>
					<td>
						<c:forEach var="to" items="${email.to}">
							${to.name}
						</c:forEach>
					</td>
					<td>${email.subject}</td>
					<td>${email.content}</td>
					<td>${email.hasAttachment}</td>
					<td>${email.sortDate}</td>
					<td>${email.fileNames}</td>
					<td>${email.jsonFrom}</td>
					<td>${email.jsonTo}</td>
					<td>${email.jsonCc}</td>
					<td>
						<button class="btn btn-danger btn-xs" type="button" id="delete${email.UID}" onclick="deleteEmail(${email.UID})"
						data-toggle="tooltip" data-placement="top" title="<spring:message code="tooltip.delete"></spring:message>">
						<span class="glyphicon glyphicon-trash"></span></button>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
