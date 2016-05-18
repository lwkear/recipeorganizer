<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="menu.contact"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="title.contact"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<h4><spring:message code="contact.account"></spring:message></h4>
			<div>
				<spring:message code="contact.account.question" arguments="${properties['company.email.support.account']}"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="contact.howto"></spring:message></h4>
			<div>
				<spring:message code="contact.howto.readFAQ" arguments="${contextPath}"></spring:message>
				<spring:message code="contact.howto.question" arguments="${properties['company.email.support.website']}"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="contact.technical"></spring:message></h4>
			<div>
				<spring:message code="contact.technical.question" arguments="${properties['company.email.support.technical']}"></spring:message>
				<spring:message code="contact.technical.detail"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="contact.feedback"></spring:message></h4>
			<div>
				<spring:message code="contact.feedback.letusknow"></spring:message>
				<spring:message code="contact.feedback.suggestions" arguments="${properties['company.email.feedback']}"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-md spacer-vert-md text-center">
			<div class="col-sm-6 col-sm-offset-3">
				<spring:message code="contact.response"></spring:message>
			</div>
			<div class="col-sm-6 col-sm-offset-3 spacer-vert-sm small">
				<spring:message code="contact.helpdesk" arguments="${properties['company.phonenumber']}"></spring:message><br>
				<spring:message code="contact.hours"></spring:message>
			</div>
		</div>
	</div>
   
<%@include file="common/footer.jsp" %>	
	
</body>
</html>
