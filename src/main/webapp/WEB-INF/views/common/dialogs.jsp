<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!-- session timeout warning dialog -->
<div class="modal fade" id="sessionTimeout" role="dialog">
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
				<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="setSessionTimeout()">OK</button>
			</div>
		</div>
	</div>
</div>

<!-- error dialog -->
<div class="modal fade" id="errorDlg" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title"><spring:message code="errordlg.title"></spring:message></h4>
			</div>
			<div class="modal-body">
				<div class="text-center"><span id="errorMsg"></span></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
			</div>
		</div>
	</div>
</div>