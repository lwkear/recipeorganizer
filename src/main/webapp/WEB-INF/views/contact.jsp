<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="menu.contact"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12">
			<div class="page-header">
				<h3><spring:message code="title.contact"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<h4><spring:message code="contact.account"></spring:message></h4>
			<div>
				<spring:message code="contact.account.question"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="contact.howto"></spring:message></h4>
			<div>
				<spring:message code="contact.howto.readFAQ"></spring:message>
				<spring:message code="contact.howto.question"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="contact.technical"></spring:message></h4>
			<div>
				<spring:message code="contact.technical.question"></spring:message>
				<spring:message code="contact.technical.detail"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="contact.feedback"></spring:message></h4>
			<div>
				<spring:message code="contact.feedback.letusknow"></spring:message>
				<spring:message code="contact.feedback.suggestions"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-md text-center spacer-vert-md">
			<div class="col-sm-6 col-sm-offset-3">
				<spring:message code="contact.response"></spring:message>
			</div>
			<div class="col-sm-6 col-sm-offset-3 spacer-vert-xs">
				<spring:message code="contact.helpdesk"></spring:message>
				<spring:message code="contact.hours"></spring:message>
			</div>
		</div>
	</div>
   
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
