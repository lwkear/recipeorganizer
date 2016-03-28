<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!-- user message dialog -->
<div class="modal" id="userMessageDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"><spring:message code="usermessage.title"></spring:message></h4>
			</div>
			<div class="modal-body">
				<form role="form" class="form">
					<div><span id="messageTo"></span></div>
					<div class="form-group">
			            <label class="control-label" for="message"><spring:message code="usermessage.label"></spring:message></label>
			            <textarea class="form-control maxSize" rows="5" id="message" data-max="${sizeMap['message.max']}"></textarea>
			            <span class="text-danger" id="messageMsg">${messageErr}</span>
				    </div>           
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="submitMessage"><spring:message code="common.send"></spring:message></button>
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>