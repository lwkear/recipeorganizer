<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach var="quest" items="${questions}" varStatus="loop">
	<div class="panel-heading">
		<h5 class="panel-title">
			<a data-toggle="collapse" data-parent="#accordion" href="#panel${quest.id}">${quest.question}</a>
		</h5>
	</div>
	<div id="panel${quest.id}" class="panel-collapse collapse">
		<div class="panel-body" style="padding-top:0px;padding-bottom:10px">
			<span>${quest.answer}</span>
		</div>
	</div>
</c:forEach>