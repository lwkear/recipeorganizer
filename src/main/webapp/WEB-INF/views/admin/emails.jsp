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
			<div class="col-sm-12">
				<div class="col-sm-3">
					<h4 class="control-label" id="currFolder">${currentFolder}</h4>
				</div>
				<div class="col-sm-9">
					<ul class="nav nav-pills pull-right">
						<li role="presentation" class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
									<spring:message code="emails.menu.folder"></spring:message><span class="caret"></span></a>
							<ul class="dropdown-menu" role="menu" id="folderMenu">
								<c:forEach var="folder" items="${folders}">
									<li><a href="#">${folder}</a></li>
								</c:forEach>
							</ul>
						</li>
						<li role="presentation" id="accountmenu" class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
									<spring:message code="emails.menu.account"></spring:message><span class="caret"></span></a>
							<ul class="dropdown-menu" id="accounts" role="menu">
							</ul>
						</li>
						<li role="presentation" class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
									<spring:message code="emails.menu.actions"></spring:message><span class="caret"></span></a>
							<ul class="dropdown-menu" role="menu">
								<li><a href="#" id="newmail"><spring:message code="emails.action.newmail"></spring:message></a></li>
								<li role="separator" class="divider"></li>
								<li><a href="#" id="newfolder"><spring:message code="emails.action.newfolder"></spring:message></a></li>
								<li><a href="#" id="emptyfolder"><spring:message code="emails.action.emptyfolder"></spring:message></a></li>
							</ul>
						</li>
						
					</ul>
				</div>
			</div>
			<div id="datatableSection">
				
					<%@include file="emailDatatable.jsp" %>
				
			</div>
		</div>
		
		<div class="col-sm-12 collapse" id="emailSingle">
			<div class="col-sm-12">
				<div class="row">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.column.to"></spring:message>:</label>
           			</div>
					<div class="col-sm-8">
						<span id="to"></span>
					</div>
					<div class="col-sm-2">
						<span class="pull-right">
							<button type="button" class="btn btn-link btn-sm" id="deleteEmail" style="border-bottom: 0;padding-bottom:0"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="common.delete"></spring:message>">
								<span class="glyphicon glyphicon-trash"></span>
							</button>
							<button type="button" class="btn btn-link btn-sm" id="replyEmail" style="border-bottom: 0;padding-bottom:0"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="emails.reply"></spring:message>">
								<span class="fa fa-reply"></span>
							</button>					
							<button type="button" class="btn btn-link btn-sm" id="replyAllEmail" style="border-bottom: 0;padding-bottom:0"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="emails.replyall"></spring:message>">
								<span class="fa fa-reply-all"></span>
							</button>					
							<button type="button" class="btn btn-link btn-sm" id="forwardEmail" style="border-bottom: 0;padding-bottom:0"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="emails.forward"></spring:message>">
								<span class="fa fa-share"></span>
							</button>					
						</span>
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
						<label class="control-label"><spring:message code="emails.cc"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="cc"></span>
					</div>
				</div>
				<div class="row collapse" id="bccRow">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.bcc"></spring:message>:</label>
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
					<div class="col-sm-8">
						<span id="sent"></span>
					</div>
					<div class="col-sm-2">
						<span class="pull-right">
							<button type="button" class="btn btn-link btn-sm" id="prevEmail" style="border-bottom: 0;padding-bottom:0"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="common.table.paginate.previous"></spring:message>">
								<span class="glyphicon glyphicon-chevron-left"></span>
							</button>
							<button type="button" class="btn btn-link btn-sm" id="nextEmail" style="border-bottom: 0;padding-bottom:0"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="common.table.paginate.next"></spring:message>">
								<span class="glyphicon glyphicon-chevron-right"></span>
							</button>
							<button type="button" class="btn btn-link btn-sm" id="closeEmail" style="border-bottom: 0;padding-bottom:0"
								data-toggle="tooltip" data-placement="top" title="<spring:message code="common.close"></spring:message>">
								<span class="glyphicon glyphicon-remove"></span>
							</button>
						</span>
					</div>
				</div>
				<div class="row collapse" id="attachRow">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.attachment"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="attach"></span>
					</div>
				</div>
			</div>
			<div class="col-sm-12">
				<div class="row spacer-vert-xs">
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
			<span id="fromRecip"></span>			
			<span id="toRecip"></span>
			<span id="ccRecip"></span>
		</div>		
	</div>	

<!-- display image dialog -->
<div id="imageModal" class="modal fade" tabindex="-1" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-body" style="padding:0;">
				<img id="image" style="width: 100%">
			</div>
		</div>
	</div>
</div>

<!-- reply dialog -->
<div class="modal" id="emailReply" tabindex="-1" role="dialog">
	<div class="modal-dialog">
	    <div class="modal-content">
			<div class="modal-header" id="replyHeader">
				<div class="row">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.column.to"></spring:message>:</label>
           			</div>
					<div class="col-sm-9">
						<span id="toReply"></span>
					</div>
					<div class="col-sm-1">
						<span><button type="button" class="close" data-dismiss="modal">&times;</button></span>			
					</div>
				</div>
				<div class="row collapse" id="ccReplyRow">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.cc"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="ccReply"></span>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.column.subject"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="subjectReply"></span>
					</div>
				</div>
			</div>
			<div class="modal-header" id="newHeader">
				<div class="row">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.column.from"></spring:message>:</label>
           			</div>
					<div class="col-sm-9">
						<span id="fromNew"></span>
					</div>
					<div class="col-sm-1">
						<span><button type="button" class="close" data-dismiss="modal">&times;</button></span>			
					</div>
				</div>
				<div class="row collapse" id="ccReplyRow">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.cc"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="ccReply"></span>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2">
						<label class="control-label"><spring:message code="emails.column.subject"></spring:message>:</label>
           			</div>
					<div class="col-sm-10">
						<span id="subjectReply"></span>
					</div>
				</div>
			</div>
			<div class="modal-body">
				<div class="form-group">
		            <textarea class="form-control" rows="20" id="emailMsg"></textarea>
			    </div>           
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id="submitReply"><spring:message code="common.submit"></spring:message></button>
				<button type="button" class="btn btn-default" id="cancelReply"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<%@include file="../common/footer.jsp" %>

</body>

<!-- include email admin-specific routines -->
<script src="<c:url value="/resources/custom/emails.js" />"></script>

</html>
