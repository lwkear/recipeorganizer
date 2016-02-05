<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="menu.faq"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="menu.faq"></spring:message></h3>
			</div>
		</div>
		<div class="faq_container">
            <div class="faq">
				<div class="faq_question">Test question #1?</div>
				<div class="faq_answer_container">
					<div class="faq_answer">
					Test answer #1
					</div>
				</div>
			</div>
			<div class="faq">
				<div class="faq_question">Test question #2?</div>
				<div class="faq_answer_container">
					<div class="faq_answer">
					Test answer #2
					</div>
				</div>
            </div>
        </div>
	</div>
   
   
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
