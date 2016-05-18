<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="menu.faq"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document" onload="blurInputFocus()">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="menu.faq"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<div class="row">
				<div class="col-sm-2">
					<ul class="nav">
						<c:forEach var="topic" items="${topics}" varStatus="loop">
							<li><a id="${topic.id}" onclick="getQuestions(${topic.id})">${topic.description}</a></li>
						</c:forEach>
					</ul>
				</div>

				<div class="col-sm-10" style="border-left: 1px solid #000">
					<div id="questionsSection">
				
						<%@include file="questions.jsp" %>
						
					</div>
				</div>
			</div>
		</div>			
	</div>
   
   
<%@include file="common/footer.jsp" %>	
	
</body>

<!-- include specific routines -->
<script src="<c:url value="/resources/custom/faq.js" />"></script>

</html>