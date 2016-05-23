<!DOCTYPE html>
<html>
<head>

<%@include file="common/head.jsp" %>

<title><spring:message code="menu.about"></spring:message> - <spring:message code="menu.product"></spring:message></title>

</head>

<body role="document">

<%@include file="common/nav.jsp" %>

	<div class="container container-white">
		<div class="col-sm-12 title-bar">
			<div class="page-header">
				<h3><spring:message code="menu.about"></spring:message></h3>
			</div>
		</div>
		<div class="col-sm-12">
			<h4><spring:message code="about.background.title"></spring:message></h4>
			<div>
				<spring:message code="about.background.description1"></spring:message>
				<spring:message code="about.background.description2"></spring:message>
				<spring:message code="about.background.description3"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="about.origin.title"></spring:message></h4>
			<div>
				<spring:message code="about.origin.description1"></spring:message>
				<spring:message code="about.origin.description2"></spring:message>
				<spring:message code="about.origin.description3"></spring:message>
				<spring:message code="about.origin.description4"></spring:message>
				<spring:message code="about.origin.description5"></spring:message>
				<spring:message code="about.origin.description6"></spring:message>
			</div>
		</div>
		<div class="col-sm-12 spacer-vert-xs">
			<h4><spring:message code="about.technical.title"></spring:message></h4>
			<div>
				<spring:message code="about.technical.description1"></spring:message>
				<c:url var="technicalUrl" value="${pageContext.servletContext.contextPath}/technical"></c:url>
				<spring:message code="about.technical.description2" arguments="${technicalUrl}"></spring:message>
				<spring:message code="about.technical.description3"></spring:message>
			</div>
		</div>
	</div>
    
<%@include file="common/footer.jsp" %>	

</body>
</html>
