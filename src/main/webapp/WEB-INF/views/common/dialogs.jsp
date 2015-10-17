<!-- Session timeout warning dialog -->
<div class="modal fade" id="sessionTimeout" role="dialog">
	<div class="modal-dialog modal-sm">
	    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">Session Timeout</h4>
			</div>
			<div class="modal-body">
				<div class="text-center" id="timeoutText"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="setSessionTimeout()">OK</button>
			</div>
		</div>
	</div>
</div>