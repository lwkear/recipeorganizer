<%@include file="head.jsp"%>

<c:set var="now" value="<%=new java.util.Date()%>" />

<div class="footer">
	<div class="container">
		<p class="text-muted text-center" style="margin-bottom:0; margin-top:0; border-bottom: none"><small>&copy;&nbsp;<fmt:formatDate pattern="yyyy" value="${now}"/>&nbsp;&nbsp;Larry Kear</small></p>
		<p class="text-muted text-center" style="margin-bottom:0; margin-top:0; border-bottom: none"><small>All Rights Reserved.</small></p>
	</div>
</div>