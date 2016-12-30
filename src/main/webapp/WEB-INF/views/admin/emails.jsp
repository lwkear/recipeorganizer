<!DOCTYPE html>
<html>
<head>

<%@include file="../common/head.jsp" %>

<title><spring:message code="emails.head"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<style type="text/css">
tr {
	cursor: pointer;
}
</style>

<body role="document">

<%@include file="../common/nav.jsp" %>

<spring:message var="yesLabel" code="common.yes"></spring:message>
<spring:message var="noLabel" code="common.no"></spring:message>

	<div class="container container-white">	
	 	<div class="col-sm-12 title-bar">
			<c:if test="${not empty warningMaint}">
				<h5 class="bold-maroon text-center"><em>${warningMaint}</em></h5>
			</c:if>
			<div class="page-header"> 		
				<h3><spring:message code="emails.title"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12 collapse in" id="emailTable">
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
					</tr>
				</thead>
				<tbody>
					<c:forEach var="email" items="${emails}">
						<tr id="${email.msgNum}">
							<td>${email.seen}</td>
							<td>
								<c:if test="${email.hasAttachment}">
									<span class="glyphicon glyphicon-paperclip"></span>
								</c:if>
							</td>
							<td data-order="${email.sortDate}"><fmt:formatDate type="both" timeStyle="short" value="${email.sentDate}" /></td>
							<td>${email.from}</td>
							<td>${email.to}</td>
							<td>${email.subject}</td>
							<td>${email.content}</td>
							<td>${email.hasAttachment}</td>
							<td>${email.sortDate}</td>
							<td>${email.fileNames}</td>
							<td>${email.fromFull}</td>
							<td>${email.toFull}</td>
							<td>${email.cc}</td>
							<td>${email.bcc}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<div class="col-sm-12 collapse" id="emailSingle">
			<div class="col-sm-9 spacer-vert-xs">
				<div class="row">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.column.to"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="to"></span>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.column.from"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="from"></span>
					</div>
				</div>
				<div class="row collapse" id="ccRow">
					<div class="col-sm-2">
						<%-- <label class="control-label"><spring:message code="emails.column.from"></spring:message>:</label> --%>
						<label class="control-label">Cc:</label>
           			</div>
					<div class="col-sm-10">
						<span id="cc"></span>
					</div>
				</div>
				<div class="row collapse" id="bccRow">
					<div class="col-sm-2">
						<%-- <label class="control-label"><spring:message code="emails.column.from"></spring:message>:</label> --%>
						<label class="control-label">Bcc:</label>
           			</div>
					<div class="col-sm-10">
						<span id="bcc"></span>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.column.subject"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="subject"></span>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.column.sent"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="sent"></span>
					</div>
				</div>
				<div class="row collapse" id="attachRow">
					<div class="col-sm-2">
						<%-- <label class="control-label"><spring:message code="emails.column.sent"></spring:message>:</label> --%>
						<label class="control-label">Attachment:</label>
           			</div>
					<div class="col-sm-10">
						<span id="attach"></span>
					</div>
				</div>
			</div>
			<div class="col-sm-3 spacer-vert-xs">
				<span class="pull-right">
					<button type="button" class="btn btn-link btn-sm" id="prevEmail"
						data-toggle="tooltip" data-placement="top" title="<spring:message code="common.table.paginate.previous"></spring:message>">
						<span class="glyphicon glyphicon-chevron-left"></span>
					</button>
					<button type="button" class="btn btn-link btn-sm" id="nextEmail"
						data-toggle="tooltip" data-placement="top" title="<spring:message code="common.table.paginate.next"></spring:message>">
						<span class="glyphicon glyphicon-chevron-right"></span>
					</button>
					<button type="button" class="btn btn-link btn-sm" id="closeEmail"
						data-toggle="tooltip" data-placement="top" title="<spring:message code="common.close"></spring:message>">
						<span class="glyphicon glyphicon-remove"></span>
					</button>
				</span>
			</div>
			<div class="row spacer-vert-xs">
				<div class="col-sm-12">
					<div class="col-sm-12 hr-black">
						<div class="row spacer-vert-xs">
							<div class="col-sm-12" id="content"></div>
						</div>
					</div>
				</div>					
			</div>
			<c:url var="imageUrl" value="/admin/attachment"/>
			<input type="hidden" id="fileId"/>
			<input type="hidden" id="imageSrc" value="${imageUrl}"/>
		</div>		
	</div>	

	<div id="imageModal" class="modal fade" tabindex="-1" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
					<img id="image" style="width: 100%"><!-- class="img-responsive"> -->
				</div>
			</div>
		</div>
	</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include user admin-specific routines -->
<script src="<c:url value="/resources/custom/emails.js" />"></script>

</html>
