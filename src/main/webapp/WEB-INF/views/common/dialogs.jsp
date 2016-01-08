<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!-- session timeout warning dialog -->
<div class="modal" id="sessionTimeout" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"><spring:message code="timeout.title"></spring:message></h4>
			</div>
			<div class="modal-body">
				<div class="text-center"><spring:message code="timeout.text1"></spring:message>&nbsp;<span id="timeLeft"></span>&nbsp;<spring:message code="timeout.text2"></spring:message></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="setSessionTimeout()"><spring:message code="common.ok"></spring:message></button>
			</div>
		</div>
	</div>
</div>

<!-- message dialog -->
<div class="modal" id="messageDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"><span id="messageTitle"></span></h4>
			</div>
			<div class="modal-body">
				<div class="text-center"><span id="messageMsg"></span></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary msgDlgBtn" data-dismiss="modal" id="yesBtn" style="display:none"><spring:message code="common.yes"></spring:message></button>
				<button type="button" class="btn btn-default msgDlgBtn" data-dismiss="modal" id="noBtn" style="display:none"><spring:message code="common.no"></spring:message></button>
				<button type="button" class="btn btn-primary msgDlgBtn" data-dismiss="modal" id="okBtn" style="display:none"><spring:message code="common.ok"></spring:message></button>
				<button type="button" class="btn btn-default msgDlgBtn" data-dismiss="modal" id="cancelBtn" style="display:none"><spring:message code="common.cancel"></spring:message></button>
			</div>
		</div>
	</div>
</div>